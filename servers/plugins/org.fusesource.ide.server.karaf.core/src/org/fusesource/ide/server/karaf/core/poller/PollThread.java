/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.poller;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.Messages;
import org.jboss.ide.eclipse.as.core.server.IPollResultListener;
import org.jboss.ide.eclipse.as.core.server.IServerStatePoller;
import org.jboss.ide.eclipse.as.core.server.IServerStatePoller.PollingException;
import org.jboss.ide.eclipse.as.core.server.IServerStatePoller.RequiresInfoException;
import org.jboss.ide.eclipse.as.core.util.IEventCodes;

/**
 * @author lhein
 *
 */
public class PollThread extends Thread {
	// PollThread status objects look like this:
	// 00000001xxxxxxxxaaaaaaaaaaaaaaaa  
	// 00000001000000010000000000xxx00x
	public static final int POLLING_ROOT_CODE = IEventCodes.POLLING_ROOT_CODE;
	public static final int SUCCESS = 0x1;
	public static final int FAIL = 0;
	public static final int POLLING_FAIL_CODE = POLLING_ROOT_CODE | FAIL;
	public static final int STATE_MASK = 0x38;   // 0b111000;
	public static final int STATE_UNKNOWN = IServer.STATE_UNKNOWN << 3;
	public static final int STATE_STARTING = IServer.STATE_STARTING << 3;
	public static final int STATE_STARTED = IServer.STATE_STARTED << 3;
	public static final int STATE_STOPPING = IServer.STATE_STOPPING << 3;
	public static final int STATE_STOPPED = IServer.STATE_STOPPED << 3;
	
	public static final String SERVER_STARTING = "org.jboss.ide.eclipse.as.core.runtime.server.PollThread.server.starting"; //$NON-NLS-1$
	public static final String SERVER_STOPPING = "org.jboss.ide.eclipse.as.core.runtime.server.PollThread.server.stopping"; //$NON-NLS-1$

	private boolean expectedState, abort, stateStartedOrStopped;
	private IServerStatePoller poller;
	private String abortMessage;
	private String pollerId;
	private IPollResultListener listener;
	private IServer server;

	public PollThread(boolean expectedState, IServerStatePoller poller, IPollResultListener listener, IServer server) {
		super(getThreadName(server));
		this.expectedState = expectedState;
		this.poller = poller;
		this.server = server;
		this.listener = listener;
		this.abort = false;
	}

	private static String getThreadName(IServer server) {
		return NLS.bind(Messages.ServerPollerThreadName, server.getName());
	}

	public void cancel() {
		cancel(null);
	}

	public void cancel(String message) {
		abort = true;
		abortMessage = message;
		poller.cancel(IServerStatePoller.CANCEL);
	}

	public int getTimeout() {
		if (expectedState == IServerStatePoller.SERVER_UP)
			return (getServer().getStartTimeout() - 2) * 1000;
		else
			return (getServer().getStopTimeout() - 2) * 1000;
	}

	@Override
	public void run() {
		// Poller not found. Abort
		if (poller == null) {
			alertEventLogStarting();
			alertPollerNotFound();
			alertListener(!expectedState);
			return;
		}

		int maxWait = getTimeout();
		alertEventLogStarting();

		long startTime = System.currentTimeMillis();
		boolean done = false;
		try {
			poller.beginPolling(getServer(), expectedState);
	
			// begin the loop; ask the poller every so often
			while (!stateStartedOrStopped 
					&& !abort 
					&& !done
					&& !timeoutReached(startTime, maxWait)) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
	
				try {
					done = poller.isComplete();
				} catch (PollingException e) {
					// abort and put the message in event log
					poller.cancel(IServerStatePoller.CANCEL);
					poller.cleanup();
					alertEventLogPollerException(e);
					alertListener(!expectedState);
					return;
				} catch (RequiresInfoException rie) {
					// This way each request for new info is checked only once.
					if (!rie.getChecked()) {
						rie.setChecked();
						fireRequestCredentials(expectedState, poller);
					}
				}
				stateStartedOrStopped = checkServerState();
			}
		} catch(Exception e) {
			abort = true;
			Status s = new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.ServerStatePollerUnexpectedError, e);
			Activator.getDefault().getLog().log(s);
		}

		if (stateStartedOrStopped) {
			// we stopped. Did we abort?
			handleUncertainTermination();
		} else if (abort) {
			// Definite abort
			poller.cleanup();
			alertEventLogAbort();
		} else if (done) {
			// the poller has an answer
			handlePollerHasAnswer();
		} else {
			// we timed out. get response from preferences
			handleTimeoutTermination();
		}
	}

	private void handlePollerHasAnswer() {
		try {
			boolean currentState = poller.getState();
			poller.cleanup();
			alertListener(currentState);
			alertEventLog(currentState);
		} catch (PollingException pe) {
			// Poller's answer was exception:  abort and put the message in event log
			poller.cancel(IServerStatePoller.CANCEL);
			poller.cleanup();
			alertEventLogPollerException(pe);
			alertListener(!expectedState);
			return;
		} catch (RequiresInfoException rie) {
			// You don't have an answer... liar!
		}
	}
	
	private void handleTimeoutTermination() {
		poller.cancel(IServerStatePoller.TIMEOUT_REACHED);
		int behavior = poller.getTimeoutBehavior();
		poller.cleanup();
		alertEventLogTimeout();
		if (behavior != IServerStatePoller.TIMEOUT_BEHAVIOR_IGNORE) {
			// xnor;
			// if behavior is to succeed and we're expected to go up,
			// we're up
			// if behavior is to fail and we're expecting to be down,
			// we're up (failed to shutdown)
			// all other cases, we're down.
			boolean currentState = (expectedState == (behavior == IServerStatePoller.TIMEOUT_BEHAVIOR_SUCCEED));
			alertListener(currentState);
		}
	}
	
	private void handleUncertainTermination() {
		int state = server.getServerState();
		boolean success;
		if (expectedState == IServerStatePoller.SERVER_UP)
			success = state == IServer.STATE_STARTED;
		else
			success = state == IServer.STATE_STOPPED;

		poller.cancel(success ? IServerStatePoller.SUCCESS
				: IServerStatePoller.FAILED);
		poller.cleanup();
	}
	
	private boolean timeoutReached(long startTime, int maxWait) {
		return System.currentTimeMillis() >= (startTime + maxWait);
	}

	protected boolean checkServerState() {
		int state = server.getServerState();
		if (state == IServer.STATE_STARTED)
			return true;
		if (state == IServer.STATE_STOPPED)
			return true;
		return false;
	}

	protected void alertEventLog(boolean currentState) {
		if (currentState != expectedState) {
			alertEventLogFailure();			
		} else {
			alertEventLogSuccess(currentState);
		}
	}

	protected void alertListener(boolean currentState) {
		if (currentState != expectedState) {
			listener.stateNotAsserted(expectedState, currentState);
		} else {
			listener.stateAsserted(expectedState, currentState);
		}
	}
	
	protected IServer getServer() {
		return server;
	}

	/*
	 * Event Log Stuff here!
	 */
	protected void alertEventLogStarting() {
		String message = expectedState ?
				Messages.PollingStarting : Messages.PollingShuttingDown;
		int state = expectedState ? STATE_STARTING : STATE_STOPPING;
		
		IStatus status = new Status(IStatus.INFO,
				Activator.PLUGIN_ID, POLLING_ROOT_CODE | state, message, null);
		Activator.getDefault().getLog().log(status);
	}

	protected void alertEventLogPollerException(PollingException e) {
		IStatus status = new Status(IStatus.ERROR,
				Activator.PLUGIN_ID, POLLING_FAIL_CODE, Messages.PollerFailure, e);
		Activator.getDefault().getLog().log(status);
	}

	protected void alertEventLogAbort() {
		IStatus status = new Status(IStatus.WARNING,
				Activator.PLUGIN_ID, POLLING_FAIL_CODE | getStateMask(expectedState, false), 
				NLS.bind(Messages.PollerAborted, abortMessage), null);
		Activator.getDefault().getLog().log(status);
	}

	protected void alertEventLogTimeout() {
		IStatus status = new Status(IStatus.ERROR,
				Activator.PLUGIN_ID, POLLING_FAIL_CODE | getStateMask(expectedState, false), "", null); //$NON-NLS-1$
		Activator.getDefault().getLog().log(status);
	}

	protected void alertEventLogFailure() {
		String startupFailed = Messages.PollingStartupFailed;
		String shutdownFailed = Messages.PollingShutdownFailed;
		IStatus status = new Status(IStatus.ERROR,
				Activator.PLUGIN_ID, POLLING_FAIL_CODE | getStateMask(expectedState, false),
				expectedState ? startupFailed : shutdownFailed, null);
		Activator.getDefault().getLog().log(status);
	}

	protected void alertEventLogSuccess(boolean currentState) {
		String startupSuccess = Messages.PollingStartupSuccess;
		String shutdownSuccess = Messages.PollingShutdownSuccess;
		int state = getStateMask(expectedState, true);
		IStatus status = new Status(IStatus.INFO,
				Activator.PLUGIN_ID, POLLING_ROOT_CODE | state |  SUCCESS,
				expectedState ? startupSuccess : shutdownSuccess, null);
		Activator.getDefault().getLog().log(status);
	}

	protected void alertPollerNotFound() {
		String startupPollerNotFound = NLS.bind(Messages.StartupPollerNotFound, pollerId); 
		String shutdownPollerNotFound = NLS.bind(Messages.ShutdownPollerNotFound, pollerId);
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
				POLLING_FAIL_CODE | getStateMask(expectedState, false), 
				expectedState ? startupPollerNotFound : shutdownPollerNotFound, null);
		Activator.getDefault().getLog().log(status);
	}
	
	protected int getStateMask(boolean expected, boolean success) {
		if( expected && success )
			return STATE_STARTED;
		if( !expected && !success)
			return STATE_STARTED;
		return STATE_STOPPED;
	}
	
	public static void fireRequestCredentials(boolean expectedState, IServerStatePoller poller) {
		//PollThreadUtils.requestCredentialsAsynch(poller, poller.getRequiredProperties());
	}
}

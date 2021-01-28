/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.fusesource.ide.camel.model.service.core.jmx.camel.ICamelDebuggerMBeanFacade;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessage;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessageParser;
import org.fusesource.ide.launcher.Activator;

/**
 * Listens to events from the CAMEL VM and fires corresponding 
 * debug events.
 */
class EventDispatchJob extends Job {
	
	private static final int TIME_WAIT_BETWEEN_CHECKS = 2000;
	
	private final CamelDebugTarget camelDebugTarget;

	public EventDispatchJob(CamelDebugTarget camelDebugTarget) {
		super("Camel Debug Event Dispatch");
		this.camelDebugTarget = camelDebugTarget;
		setSystem(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		while (!camelDebugTarget.isTerminated() && !monitor.isCanceled()) {
			checkSuspendedBreakpoints();
		}
		return Status.OK_STATUS;
	}

	private void checkSuspendedBreakpoints() {
		ICamelDebuggerMBeanFacade debugger = camelDebugTarget.getDebugger();
		if (debugger != null
				&& !camelDebugTarget.isSuspended()
				&& !camelDebugTarget.isDisconnected()
				&& !camelDebugTarget.isTerminated()) {
			try {
				Set<String> suspendedBreakpoints = debugger.getSuspendedBreakpointNodeIds();
				if (suspendedBreakpoints != null && !suspendedBreakpoints.isEmpty()) {
					handleSuspendedBreakpoints(suspendedBreakpoints);
				}
				// wait a bit to keep jmx traffic lower - if we are too fast sometimes a breakpoint is
				// hit but no message dump is available yet -> bad coding on camel side?
				Thread.sleep(TIME_WAIT_BETWEEN_CHECKS);
			} catch (IOException ioe) {
				Activator.getLogger().warning("The remote connection has been lost, debugger will be disconnected.", ioe);
				try {
					camelDebugTarget.disconnect();
				} catch (DebugException e) {
					Activator.getLogger().error(e);
				}
			} catch (Exception ex) {
				Activator.getLogger().error(ex);
			} 
		}
	}

	private void handleSuspendedBreakpoints(Set<String> suspendedBreakpoints) throws DebugException {
		// we need to suspend the debug target
		camelDebugTarget.suspend();
		
		for (String nodeId : suspendedBreakpoints) {
			BacklogTracerEventMessage evMsg = new BacklogTracerEventMessageParser().getBacklogTracerEventMessage(camelDebugTarget.getMessagesForNode(nodeId));
			String id = camelDebugTarget.generateKey(evMsg);
			CamelThread t = camelDebugTarget.getThreadForId(id);
				
			// now we can access the stack frames
			CamelStackFrame toStackFrame = t.getTopStackFrame();
			String endpointId = toStackFrame != null ? toStackFrame.getEndpointId() : null;
			if (nodeId.equals(endpointId)) {
				// its the same breakpoint we already hit for that exchange - ignore it
				continue;
			}
			
			if (!t.isSuspended()) {
				// process the breakpoint
				camelDebugTarget.breakpointHit(nodeId, evMsg);
				
				// now resume
				camelDebugTarget.resume();
			}
		}
	}
}
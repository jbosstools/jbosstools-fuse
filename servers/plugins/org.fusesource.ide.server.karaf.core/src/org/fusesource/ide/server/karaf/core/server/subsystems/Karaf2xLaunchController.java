/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.poller.BaseKarafPoller;
import org.fusesource.ide.server.karaf.core.poller.PollThread;
import org.fusesource.ide.server.karaf.core.server.KarafServerDelegate;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfigurator;
import org.jboss.ide.eclipse.as.core.server.IPollResultListener;
import org.jboss.ide.eclipse.as.core.server.IServerStatePoller2;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ILaunchServerController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IServerShutdownController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IShutdownControllerDelegate;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.AbstractStartJavaServerLaunchDelegate;

public class Karaf2xLaunchController extends AbstractSubsystemController
		implements ILaunchServerController, ILaunchConfigurationDelegate2,
		IShutdownControllerDelegate {

	private AbstractStartJavaServerLaunchDelegate launchDelegate;

	private IPollResultListener pollListener = new IPollResultListener() {
		@Override
		public void stateNotAsserted(boolean expectedState, boolean currentState) {
			// server is not up...something went wrong on startup
			((ControllableServerBehavior)getControllableBehavior()).setServerStopped();
		}
		
		@Override
		public void stateAsserted(boolean expectedState, boolean currentState) {
			// server is up and running, so set the server state accordingly
			((ControllableServerBehavior)getControllableBehavior()).setServerStarted();
		}
	};

	private AbstractStartJavaServerLaunchDelegate getLaunchDelegate() {
		if (launchDelegate == null) {
			launchDelegate = new AbstractStartJavaServerLaunchDelegate(){

				@Override
				protected void initiatePolling(IServer server) {
					PollThread pollThread = new PollThread(true, getPoller(), pollListener, server);
					getControllableBehavior().putSharedData(BaseKarafPoller.KEY_POLLER, pollThread);
					pollThread.start();
				}

				@Override
				protected void cancelPolling(IServer server) {
					Object o = getControllableBehavior().getSharedData(BaseKarafPoller.KEY_POLLER);
					if (o instanceof PollThread) {
						PollThread pollThread = (PollThread)o;
						pollThread.cancel();
					}							
				}

				@Override
				protected void logStatus(IServer server, IStatus stat) {
					// jbt logs this to a 'server log' view, but that's not available here
					Activator.getDefault().getLog().log(stat);
				}

				@Override
				protected IStatus isServerStarted(IServer server) {
					return getPoller().getCurrentStateSynchronous(getServer());
				}

				@Override
				protected void validateServerStructure(IServer server)
						throws CoreException {
					// a-ok
				}};
		}
		return launchDelegate;
	}
	
	private IServerStatePoller2 getPoller() {
		return new BaseKarafPoller();
	}

	@Override
	public IStatus canStart(String launchMode) {
		KarafServerDelegate d = ((KarafServerDelegate)getServer().loadAdapter(KarafServerDelegate.class, null));
		return d == null ? Status.CANCEL_STATUS : d.validate();
	}

	@Override
	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy,
			IProgressMonitor monitor) throws CoreException {
		ILaunchConfigConfigurator configurator = getConfigurator();
		if (configurator != null) {
			configurator.configure(workingCopy);
		}
	}

	protected ILaunchConfigConfigurator getConfigurator() throws CoreException {
		KarafServerDelegate serverDel = (KarafServerDelegate)getServer().loadAdapter(KarafServerDelegate.class, new NullProgressMonitor());
		if (serverDel != null) {
			ILaunchConfigConfigurator cfg = serverDel.getLaunchConfigurator();
			if (cfg != null) {
				return cfg;
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to retrieve a launch configuration for server type " + getServer().getServerType().getId()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.
	 * eclipse.debug.core.ILaunchConfiguration, java.lang.String,
	 * org.eclipse.debug.core.ILaunch,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		// FOr this method we assume everything has already been set up properly
		// and we just launch with our standard local launch delegate
		// which checks things like if a server is up already, or
		// provides profiling integration with wtp's profiling for servers
		getLaunchDelegate().launch(configuration, mode, launch, monitor);
	}

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		return getLaunchDelegate().getLaunch(configuration, mode);
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return getLaunchDelegate().buildForLaunch(configuration, mode, monitor);
	}

	@Override
	public boolean finalLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return getLaunchDelegate().finalLaunchCheck(configuration, mode, monitor);
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return getLaunchDelegate().preLaunchCheck(configuration, mode, monitor);
	}

	@Override
	public IServerShutdownController getShutdownController() {
		try {
			return (IServerShutdownController)((IControllableServerBehavior)getServer()).getController(ControllableServerBehavior.SYSTEM_SHUTDOWN);
		} catch (CoreException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}
}

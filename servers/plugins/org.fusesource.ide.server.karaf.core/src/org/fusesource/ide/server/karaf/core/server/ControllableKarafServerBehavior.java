/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ILaunchServerController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IModuleStateController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IServerShutdownController;

public class ControllableKarafServerBehavior extends ControllableServerBehavior {

	@Override
	protected IPublishController getPublishController() throws CoreException {
		return (IPublishController)getController(SYSTEM_PUBLISH, null);
	}
	
	@Override
	protected IModuleStateController getModuleStateController() throws CoreException {
		return (IModuleStateController)getController(SYSTEM_MODULES, null);
	}

	@Override
	protected IServerShutdownController getShutdownController() throws CoreException {
		return (IServerShutdownController)getController(SYSTEM_SHUTDOWN, null);
	}
	
	@Override
	protected ILaunchServerController getLaunchController() throws CoreException {
		return (ILaunchServerController)getController(SYSTEM_LAUNCH, null);
	}
	
	@Override
	public void setServerStarted() {
		super.setServerStarted();
		updateModuleStates();
	}
	
	/**
	 * triggers status updates for all deployed modules
	 */
	protected void updateModuleStates() {
		// Once the server is marked started, we want to update the module publish state
		final IServer server = getServer();
		Job moduleStateJob = new WorkspaceJob("Module State Update Job") {
			
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
				throws CoreException {
				IModule[] modules = server.getModules();
				monitor.beginTask("Verifying Module State", modules.length * 1000); //$NON-NLS-1$
				for( int i = 0; i < modules.length; i++ ) {
					IModule[] temp = new IModule[]{modules[i]};
					
					//boolean started = verifier.isModuleStarted(server, temp, new SubProgressMonitor(monitor, 1000));
					//int state = started ? IServer.STATE_STARTED : IServer.STATE_STOPPED;
					int state = getModuleStateController().getModuleState(temp, new SubProgressMonitor(monitor, 1000));
					((Server)server).setModuleState(temp, state);
				}
				return Status.OK_STATUS;
			}
		};
		moduleStateJob.schedule();	
	}
}

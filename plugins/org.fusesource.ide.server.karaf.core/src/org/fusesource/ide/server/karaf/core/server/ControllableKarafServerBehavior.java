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
package org.fusesource.ide.server.karaf.core.server;

import org.eclipse.core.runtime.CoreException;
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
}

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.fuse.core.server;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.server.fuse.core.server.subsystems.Fuse6xStartupLaunchConfigurator;
import org.fusesource.ide.server.karaf.core.server.KarafServerDelegate;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfigurator;

/**
 * @author lhein
 */
public class Fuse6xServerDelegate extends KarafServerDelegate {
	
	public static final String DEFAULT_FUSEESB_SSH_USER = "admin";
	public static final String DEFAULT_FUSEESB_SSH_PASSWORD = "admin";
	
	@Override
	protected String getDefaultUsername() {
		return DEFAULT_FUSEESB_SSH_USER;
	}
	
	@Override
	protected String getDefaultPassword() {
		return DEFAULT_FUSEESB_SSH_PASSWORD;
	}
	
	@Override
	public ILaunchConfigConfigurator getLaunchConfigurator() throws CoreException {
		return new Fuse6xStartupLaunchConfigurator(getServer());
	}
}

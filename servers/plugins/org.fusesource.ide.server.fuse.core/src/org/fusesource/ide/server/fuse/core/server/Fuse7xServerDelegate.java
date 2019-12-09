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
package org.fusesource.ide.server.fuse.core.server;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.fuse.core.server.subsystems.Fuse7xStartupLaunchConfigurator;
import org.fusesource.ide.server.karaf.core.server.Karaf4xServerDelegate;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfigurator;

/**
 * @author lhein
 */
public class Fuse7xServerDelegate extends Karaf4xServerDelegate {
	
	@Override
	protected String getDefaultUsername() {
		return FuseServerConstants.DEFAULT_FUSEESB_SSH_USER;
	}
	
	@Override
	protected String getDefaultPassword() {
		return FuseServerConstants.DEFAULT_FUSEESB_SSH_PASSWORD;
	}
	
	@Override
	public ILaunchConfigConfigurator getLaunchConfigurator() throws CoreException {
		return new Fuse7xStartupLaunchConfigurator(getServer());
	}
	
	@Override
	public IStatus validate() {
		// check if the folder exists and the karaf.jar is in place
		if (getServer() != null && getServer().getRuntime() != null) {
			IPath rtLoc = getServer().getRuntime().getLocation();
			File[] files = rtLoc.append("lib").append("boot").toFile().listFiles( (File file) -> file.getName().startsWith("org.apache.karaf.main-") && file.getName().endsWith(".jar"));
			if (files != null && files.length>0) {
				return Status.OK_STATUS;	
			}
		}
		return Status.CANCEL_STATUS;
	}
}

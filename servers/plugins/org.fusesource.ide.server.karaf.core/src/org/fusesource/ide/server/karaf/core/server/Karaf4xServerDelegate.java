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
package org.fusesource.ide.server.karaf.core.server;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.server.karaf.core.server.subsystems.Karaf4xStartupLaunchConfigurator;
import org.fusesource.ide.server.karaf.core.util.KarafUtils;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfigurator;

/**
 * @author lheinema
 *
 */
public class Karaf4xServerDelegate extends Karaf3xServerDelegate {
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.KarafServerDelegate#validate()
	 */
	@Override
	public IStatus validate() {
		// check if the folder exists and the karaf.jar is in place
		if (getServer() != null && getServer().getRuntime() != null) {
			IPath rtLoc = getServer().getRuntime().getLocation();
			String version = KarafUtils.getVersion(rtLoc.toFile());
			IPath karafJar = rtLoc.append("lib").append("boot").append(String.format("org.apache.karaf.main-%s.jar", version.replaceAll(".SNAPSHOT", "-SNAPSHOT")));
			if (rtLoc.toFile().exists() && rtLoc.toFile().isDirectory() && karafJar.toFile().exists() && karafJar.toFile().isFile()) {
				return Status.OK_STATUS;	
			}
		}
		return Status.CANCEL_STATUS;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.Karaf3xServerDelegate#getLaunchConfigurator()
	 */
	@Override
	public ILaunchConfigConfigurator getLaunchConfigurator() throws CoreException {
		return new Karaf4xStartupLaunchConfigurator(getServer());
	}
}

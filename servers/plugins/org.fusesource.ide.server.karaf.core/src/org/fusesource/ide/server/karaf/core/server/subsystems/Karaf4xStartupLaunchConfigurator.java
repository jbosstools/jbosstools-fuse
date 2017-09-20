/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants;

/**
 * @author lheinema
 */
public class Karaf4xStartupLaunchConfigurator extends Karaf3xStartupLaunchConfigurator {
	
	public Karaf4xStartupLaunchConfigurator(IServer server) throws CoreException {
		super(server);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.BaseKarafStartupLaunchConfigurator#isSupportedRuntimeVersion(java.lang.String)
	 */
	@Override
	protected boolean isSupportedRuntimeVersion(String version) {
		return !Strings.isBlank(version) && version.startsWith(IKarafToolingConstants.KARAF_VERSION_4X);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xStartupLaunchConfigurator#findJars(org.eclipse.core.runtime.IPath, java.util.List)
	 */
	@Override
	protected void findJars(IPath path, List<Object> cp) {
		// need to override because findJars is used for isStartable() check for servers
		File[] libs = path.toFile().listFiles( (File pathname) -> pathname.isDirectory() || (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar")));
		if (libs != null) {
			for (File lib : libs) {
				String libName = lib.getName();
				IPath p = path.append(libName);
				if (lib.isFile()) {
					if (libName.toLowerCase().indexOf("karaf")!=-1 || lib.getPath().toLowerCase().indexOf("boot")!=-1) {
						cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(p));
					}
				} else {
					findJars(p, cp);
				}
			}
		}
	}
}

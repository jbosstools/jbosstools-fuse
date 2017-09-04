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
import java.io.FileFilter;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;

/**
 * @author lheinema
 *
 */
public class Karaf4xStartupLaunchConfigurator extends Karaf3xStartupLaunchConfigurator {
	
	public Karaf4xStartupLaunchConfigurator(IServer server)
			throws CoreException {
		super(server);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xStartupLaunchConfigurator#getClassPathEntries(java.lang.String)
	 */
	@Override
	protected String[] getClassPathEntries(String installPath) {
		String[] cpEntries = super.getClassPathEntries(installPath);
		return cpEntries;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xStartupLaunchConfigurator#findJars(org.eclipse.core.runtime.IPath, java.util.List)
	 */
	@Override
	protected void findJars(IPath path, List<Object> cp) {
		File[] libs = path.toFile().listFiles(new FileFilter() {
			/*
			 * (non-Javadoc)
			 * @see java.io.FileFilter#accept(java.io.File)
			 */
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() || (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar"));
			}
		});
		for (File lib : libs) {
			IPath p = path.append(lib.getName());
			if (lib.isFile()) {
				if (lib.getName().toLowerCase().indexOf("karaf")!=-1 || lib.getPath().toLowerCase().indexOf("boot")!=-1) {
					cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(p));
				}
			} else {
				findJars(p, cp);
			}
		}
	}
}

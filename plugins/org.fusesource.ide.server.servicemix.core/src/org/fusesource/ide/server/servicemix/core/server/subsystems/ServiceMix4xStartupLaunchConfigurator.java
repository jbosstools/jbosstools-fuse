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
package org.fusesource.ide.server.servicemix.core.server.subsystems;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xStartupLaunchConfigurator;
import org.fusesource.ide.server.servicemix.core.util.IServiceMixToolingConstants;

/**
 * @author lhein
 */
public class ServiceMix4xStartupLaunchConfigurator extends
		Karaf2xStartupLaunchConfigurator {
	
	public ServiceMix4xStartupLaunchConfigurator(IServer server)
			throws CoreException {
		super(server);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xStartupLaunchConfigurator#doConfigure(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	protected void doConfigure(ILaunchConfigurationWorkingCopy workingCopy)
			throws CoreException {

		IKarafRuntime runtime = null;
		if (server.getRuntime() != null) {
			runtime = (IKarafRuntime)server.getRuntime().loadAdapter(IKarafRuntime.class, null);
		}
		
		if (runtime != null) {
			String karafInstallDir = runtime.getLocation().toOSString();
			String mainProgram = null;
			String vmArguments = null;
			
			String version = runtime.getVersion();
			if (version != null) {
				if (version.startsWith(IServiceMixToolingConstants.SMX_VERSION_4x)) {
					// handle 4x specific program arguments
					vmArguments = get4xVMArguments(karafInstallDir);
					mainProgram = get4xMainProgram();
				} else if (version.startsWith(IServiceMixToolingConstants.SMX_VERSION_5x)) {
					// handle 5x specific program arguments
					vmArguments = get5xVMArguments(karafInstallDir);
					mainProgram = get5xMainProgram();
				} else {
					System.err.println("Unhandled ServiceMix Version (" + version + ")!");
				}
			}
			
			// For java tabs
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, karafInstallDir);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainProgram);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArguments);

			List<String> classPathList = new LinkedList<String>();
			String[] classPathEntries = getClassPathEntries(karafInstallDir);
			if (classPathEntries != null && classPathEntries.length > 0) {
				for (String jarName : classPathEntries) {
					IPath jarPath = new Path(jarName);
					IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(jarPath);
					classPathList.add(entry.getMemento());
				}
			} else {
				// FIXME No jar files.
			}
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classPathList);
		}
	}
	
	protected String get4xMainProgram() {
		return getMainProgram();
	}

	protected String get5xMainProgram() {
		return getMainProgram();
	}

	protected String get4xVMArguments(String karafInstallDir) {
		return super.get2xVMArguments(karafInstallDir);
	}
	
	protected String get5xVMArguments(String karafInstallDir) {
		return super.get2xVMArguments(karafInstallDir);
	}

	@Override
	protected void doOverrides(ILaunchConfigurationWorkingCopy launchConfig)
			throws CoreException {
		super.doOverrides(launchConfig);
	}
}

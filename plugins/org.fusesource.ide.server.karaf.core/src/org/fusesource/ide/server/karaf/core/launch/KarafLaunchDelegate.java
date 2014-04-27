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

package org.fusesource.ide.server.karaf.core.launch;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.fusesource.ide.server.karaf.core.server.KarafServerBehaviourDelegate;


/**
 * @author lhein
 */
public class KarafLaunchDelegate implements ILaunchConfigurationDelegate {

	private static final String QUOTE = "\"";
	private static final String EMPTY_STRING = "";
	
	public static final String ATTR_SERVER_ID = "server-id";
	
	/**
	 * empty constructor
	 */
	public KarafLaunchDelegate() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		String serverId = configuration.getAttribute(ATTR_SERVER_ID, (String) null);
		IServer server = ServerCore.findServer(serverId);
		server.getHost();

		KarafServerBehaviourDelegate behaviorDelegate = (KarafServerBehaviourDelegate) server.loadAdapter(KarafServerBehaviourDelegate.class, null);
		
		monitor.beginTask("Starting Server...", 10);
		behaviorDelegate.setupLaunch(mode);		
		
		IProcess prs = launchUsingJavaLaunchConfig(behaviorDelegate, configuration, mode, launch,	monitor);
		behaviorDelegate.setKarafProcess(prs, monitor);
		monitor.done();
	}
	
	/**
	 * This runs a native java call. Its expected that java is there in the
	 * system path.
	 * 
	 * @param configuration
	 * @param mode
	 * @param launch
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	@SuppressWarnings("unused")
	private IProcess launchUsingJavaCommand(ILaunchConfiguration configuration,
			String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		List classPath = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, Collections.EMPTY_LIST);

		StringBuffer classPathBuffer = new StringBuffer(" -classpath \"");
		for (Object classPathEntry : classPath) {
			IRuntimeClasspathEntry entry = JavaRuntime.newRuntimeClasspathEntry(classPathEntry.toString());
			classPathBuffer.append(entry.getPath().toOSString()	+ File.pathSeparator);
		}
		classPathBuffer.append("\" ");
		String vmArgs = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, EMPTY_STRING);

		String launchProgram = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, EMPTY_STRING);

		// Boolean useDefaultClassPath = configuration .getAttribute(
		// IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true);

		String workingDir = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, EMPTY_STRING);

		File workDirFile = new File(workingDir);

		String javaCommandPath = "java ";
		IVMInstall defaultVMInstall = JavaRuntime.getDefaultVMInstall();
		if (defaultVMInstall != null) {
			String javaPath = defaultVMInstall.getInstallLocation().getAbsolutePath();
			javaCommandPath = QUOTE + javaPath + File.separator + "bin"	+ File.separator + "java" + QUOTE;
		}
		String launchCommand = javaCommandPath + classPathBuffer.toString()	+ vmArgs + " " + launchProgram;

		try {
			Process karafProcess = Runtime.getRuntime().exec(launchCommand, null, workDirFile);
			IProcess prs = DebugPlugin.newProcess(launch, karafProcess, "Karaf", Collections.EMPTY_MAP);
			return prs;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This makes use of java launch configuration. But the problem with that
	 * approach is that we don't get a callback while termination. Incomplete!!!
	 * 
	 * @param configuration
	 * @param mode
	 * @param launch
	 * @param monitor
	 * @return
	 * @throws CoreException
	 */
	private IProcess launchUsingJavaLaunchConfig(KarafServerBehaviourDelegate behaviorDelegate,
			ILaunchConfiguration configuration, String mode, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		if (manager != null) {
			ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
			if (type != null) {
				String serverId = configuration.getAttribute(ATTR_SERVER_ID, EMPTY_STRING);
				ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, serverId);
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, Collections.EMPTY_LIST));
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, EMPTY_STRING));
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, EMPTY_STRING));
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true));
				Map<String, String> vmMap = new HashMap<String, String>(4);
				vmMap.put(IJavaLaunchConfigurationConstants.ATTR_JAVA_COMMAND, "java");
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP, vmMap);
				workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, EMPTY_STRING));

				ILaunch launch2 = workingCopy.launch(mode, monitor);
				if (launch2 != null) {
					//behaviorDelegate.setLaunch(launch2);
					IProcess[] processes = launch2.getProcesses();
					if (processes != null && processes.length > 0) {
//						Activator.getLogger().debug(processes[0].getAttribute(IProcess.ATTR_CMDLINE));
						launch.addProcess(processes[0]);
						return processes[0];
					}
				}
			}
		}
		return null;
	}
}

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
package org.fusesource.ide.launcher.tests.integration.remote.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.remote.debug.RemoteCamelLaunchConfigurationDelegate;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RemoteCamelLaunchConfigurationDelegateIT {
	
	private ProjectWithDebugAvailableDeployedHelper projectWithDebugAvailableDeployedHelper;

	private IFile camelFile;
	private IProject project;
	private ILaunch remoteDebuglaunch;
	
	@Before
	public void setup() throws Exception {
		projectWithDebugAvailableDeployedHelper = new ProjectWithDebugAvailableDeployedHelper();
		projectWithDebugAvailableDeployedHelper.start();
		camelFile = projectWithDebugAvailableDeployedHelper.getCamelFile();
		project = projectWithDebugAvailableDeployedHelper.getProject();
	}
	
	@After
	public void tearDown() throws DebugException {
		projectWithDebugAvailableDeployedHelper.clean();
	}

	@Test
	public void testRemoteCamelLaunch() throws Exception {
		remoteDebuglaunch = launchRemoteDebug();
		new RemoteCamelDebugTester(remoteDebuglaunch, project, camelFile).test();
	}
	
	private ILaunch launchRemoteDebug() throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = manager.getLaunchConfigurationType(RemoteCamelLaunchConfigurationDelegate.LAUNCH_CONFIGURATION_TYPE);
 		ILaunchConfigurationWorkingCopy configuration = launchConfigurationType.newInstance(null, "Remote Camel Debug - "+ camelFile.getName());
 		
 		configuration.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, camelFile.getLocation().toOSString());
 		configuration.setAttribute(ICamelDebugConstants.ATTR_JMX_URI_ID, "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi/camel");
 		
		return configuration.doSave().launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
	}
}

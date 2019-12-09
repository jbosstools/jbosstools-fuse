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
package org.fusesource.ide.launcher.tests.integration.remote.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.fusesource.ide.camel.tests.util.MasterPasswordDisabler;
import org.fusesource.ide.launcher.remote.debug.RemoteCamelLaunchConfigurationDelegate;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractRemoteCamelLaunchConfigurationDelegate {

	private ProjectWithDebugAvailableDeployedHelper projectWithDebugAvailableDeployedHelper;
	private IFile camelFile;
	private IProject project;
	private ILaunch remoteDebuglaunch;
	private MasterPasswordDisabler masterPasswordDisabler;
	
	@Before
	public void setup() throws Exception {
		masterPasswordDisabler = new MasterPasswordDisabler();
		masterPasswordDisabler.setup();
		
		projectWithDebugAvailableDeployedHelper = createProjectHelper();
		projectWithDebugAvailableDeployedHelper.start();
		camelFile = projectWithDebugAvailableDeployedHelper.getCamelFile();
		project = projectWithDebugAvailableDeployedHelper.getProject();
	}

	protected ProjectWithDebugAvailableDeployedHelper createProjectHelper() {
		return new ProjectWithDebugAvailableDeployedHelper(this.getClass().getSimpleName());
	}
	
	@After
	public void tearDown() throws CoreException {
		if(remoteDebuglaunch != null) {
			remoteDebuglaunch.terminate();
			remoteDebuglaunch.getLaunchConfiguration().delete();
		}
		projectWithDebugAvailableDeployedHelper.clean();
		masterPasswordDisabler.tearDown();
	}
	
	@Test
	public void testRemoteCamelLaunch() throws Exception {
		remoteDebuglaunch = launchRemoteDebug();
		new RemoteCamelDebugTester(remoteDebuglaunch, project, camelFile).test();
	}

	private ILaunch launchRemoteDebug() throws Exception {
		ILaunchConfigurationWorkingCopy configuration = getInitialLaunchConfiguration();
		configureCamelContextFile(configuration);
		
		configureConnection(configuration);
		
		return configuration.doSave().launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
	}
	
	protected abstract void configureConnection(ILaunchConfigurationWorkingCopy configuration) throws Exception;

	private void configureCamelContextFile(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, camelFile.getLocation().toOSString());
	}

	private ILaunchConfigurationWorkingCopy getInitialLaunchConfiguration() throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = manager.getLaunchConfigurationType(RemoteCamelLaunchConfigurationDelegate.LAUNCH_CONFIGURATION_TYPE);
		return launchConfigurationType.newInstance(null, "Remote Camel Debug - "+ camelFile.getName());
	}

}
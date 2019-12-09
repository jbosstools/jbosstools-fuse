/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.ui.launch;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.fusesource.ide.launcher.ui.Messages;

/**
 * @author lhein
 *
 */
public class ExecutePomActionNoTests extends ExecutePomActionSupport {

	public ExecutePomActionNoTests() {
		super(
				"org.fusesource.ide.launcher.ui.launchConfigurationTabGroup.camelContext",
				CamelContextLaunchConfigConstants.CAMEL_CONTEXT_LAUNCH_CONFIG_TYPE_ID,
				CamelContextLaunchConfigConstants.DEFAULT_MAVEN_GOALS_ALL + " -Dmaven.test.skip=true");
	}
	
	@Override
	protected void appendAttributes(IContainer basedir, ILaunchConfigurationWorkingCopy workingCopy, String goal) {
		String path = getSelectedFilePath();
		workingCopy.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, path == null ? "" : path); // basedir.getLocation().toOSString()
		workingCopy.setAttribute(MavenLaunchConstants.ATTR_SKIP_TESTS, true);
		workingCopy.setAttribute(ICamelDebugConstants.ATTR_JMX_URI_ID, ICamelDebugConstants.DEFAULT_JMX_URI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.fusesource.ide.launcher.ui.launch.ExecutePomActionSupport#
	 * isTestStrategyMatching(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	protected boolean isTestStrategyMatching(ILaunchConfiguration configuration) {
		return isSkipTest(configuration);
	}
	
	@Override
	protected String getBasicLaunchConfigurationName( IFile camelFile) {
		return NLS.bind(Messages.launchConfiguraNameWithoutTest, super.getBasicLaunchConfigurationName(camelFile));
	}
}

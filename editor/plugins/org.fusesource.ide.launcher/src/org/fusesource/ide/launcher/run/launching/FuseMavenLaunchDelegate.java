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
package org.fusesource.ide.launcher.run.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.m2e.internal.launch.MavenLaunchDelegate;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.util.SecureStorageUtil;

public abstract class FuseMavenLaunchDelegate extends MavenLaunchDelegate {

	protected String goals = "";
	protected boolean skipTests = false;
	
	/**
	 * 
	 * @param goals
	 */
	public FuseMavenLaunchDelegate(String goals) {
		this.goals = goals;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2e.internal.launch.MavenLaunchDelegate#getGoals(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	@Override
	protected String getGoals(ILaunchConfiguration configuration)
			throws CoreException {
		return goals;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.m2e.internal.launch.MavenLaunchDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		super.launch(configuration, mode, launch, monitor);
		
		if (mode.equals(ILaunchManager.DEBUG_MODE) && launch.getProcesses() != null && launch.getProcesses().length>0) {
			// grab the JMX information
			String jmxUrl = configuration.getAttribute(ICamelDebugConstants.ATTR_JMX_URI_ID, ICamelDebugConstants.DEFAULT_JMX_URI);
			String jmxUser = configuration.getAttribute(ICamelDebugConstants.ATTR_JMX_USER_ID, "");
			String jmxPass = getPassword(configuration);
					
			IProcess p = launch.getProcesses()[0];
			IDebugTarget target = new CamelDebugTarget(launch, p, jmxUrl, jmxUser, jmxPass);
			launch.addDebugTarget(target);
		}
	}
	
	private String getPassword(ILaunchConfiguration configuration) {
		String s = SecureStorageUtil.getFromSecureStorage(Activator.getBundleID(), configuration, ICamelDebugConstants.ATTR_JMX_PASSWORD_ID);
		return s != null ? s : "";
	}
	
	/**
	 * sets the new goals
	 * 
	 * @param newGoals
	 */
	protected void setGoals(String newGoals) {
		this.goals = newGoals;
	}
	
	/**
	 * enables / disables tests
	 * 
	 * @param skipTests
	 */
	protected void setSkipTests(boolean skipTests) {
		this.skipTests = skipTests;
	}
	
	@Override
	public String getVMArguments(ILaunchConfiguration configuration) throws CoreException {
		StringBuilder sb = new StringBuilder();
		
		// user configured entries
		sb.append(" ").append(super.getVMArguments(configuration));
		// set our defined eclipse process name
		sb.append(" -DECLIPSE_PROCESS_NAME=\"'" + getEclipseProcessName() + "'\"");
		// switch the test flag
		sb.append(" -Dmaven.test.skip=" + this.skipTests);
		// enable jmx
		sb.append(" -Dorg.apache.camel.jmx.createRmiConnector=True");
		
		return sb.toString();
	}

	/**
	 * returns a process name which will be interpreted by the jmx tooling
	 * 
	 * @return
	 */
	public abstract String getEclipseProcessName();
}
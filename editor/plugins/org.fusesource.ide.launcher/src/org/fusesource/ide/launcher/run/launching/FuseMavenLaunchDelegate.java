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
package org.fusesource.ide.launcher.run.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.m2e.internal.launch.MavenLaunchDelegate;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.util.CamelJMXLaunchConfiguration;

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
	
	@Override
	protected String getGoals(ILaunchConfiguration configuration)
			throws CoreException {
		return goals;
	}
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		super.launch(configuration, mode, launch, monitor);
		
		if (ILaunchManager.DEBUG_MODE.equals(mode) && hasProcess(launch)) {
			CamelJMXLaunchConfiguration conf = new CamelJMXLaunchConfiguration(configuration, ICamelDebugConstants.DEFAULT_JMX_URI);
			IProcess p = launch.getProcesses()[0];
			IDebugTarget target = new CamelDebugTarget(launch, p, conf.getJMXUrl(), conf.getJMXUser(), conf.getJMXPassword());
			launch.addDebugTarget(target);
		}
	}
	
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		// Workaround to FUSETOOLS-3058 with new feature "advanced source lookup" breaking Camel Source Lookup
		return null;
	}

	private boolean hasProcess(ILaunch launch) {
		return launch.getProcesses() != null && launch.getProcesses().length > 0;
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
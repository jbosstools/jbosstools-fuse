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
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.jvmmonitor.core.IJvmModelChangeListener;
import org.jboss.tools.jmx.jvmmonitor.core.JvmModel;
import org.jboss.tools.jmx.jvmmonitor.core.JvmModelEvent;
import org.jboss.tools.jmx.local.JVMConnectionUtility;

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
		if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			connectCamelDebuggerToLocalProcess(launch);
		}
		super.launch(configuration, mode, launch, monitor);
	}

	private void connectCamelDebuggerToLocalProcess(ILaunch launch) {
		JvmModel model = JvmModel.getInstance();
		model.addJvmModelChangeListener(new IJvmModelChangeListener() {

			@Override
			public void jvmModelChanged(JvmModelEvent jvmModelEvent) {				
				if (JvmModelEvent.State.JvmAdded.equals(jvmModelEvent.state)
						&& jvmModelEvent.jvm instanceof IActiveJvm
						&& "localhost".equals(jvmModelEvent.jvm.getHost().getName())
						&& isJVMLaunchCommandOfCamel(jvmModelEvent.jvm.getLaunchCommand())
						&& isLocalCamelProcess(launch)) {
					model.removeJvmModelChangeListener(this);
					try {
						IConnectionWrapper connectionWrapper = JVMConnectionUtility.findConnectionForJvm((IActiveJvm) jvmModelEvent.jvm);
						IDebugTarget target = new CamelDebugTarget(launch, launch.getProcesses()[0], connectionWrapper);
						launch.addDebugTarget(target);
					} catch (CoreException e) {
						Activator.getLogger().error("Error while connecting the Camel debugger", e);
					}
				}
			}
		});
	}
	
	private boolean isJVMLaunchCommandOfCamel(String launchCommand) {
		return launchCommand.contains(CamelContextLaunchConfigConstants.ATTR_CONTEXT_FILE);
	}
	
	protected boolean isLocalCamelProcess(ILaunch launch) {
		if(launch.getProcesses().length > 0) {
			String cmdLine = launch.getProcesses()[0].getAttribute(IProcess.ATTR_CMDLINE);
			return cmdLine !=null && cmdLine.contains(getEclipseProcessName()) && cmdLine.contains("-DECLIPSE_PROCESS_NAME");
		}
		return false;
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
		return sb.toString();
	}
	
	@Override
	public String getVMArguments(ILaunchConfiguration configuration, String mode) throws CoreException {
		if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			StringBuilder sb = new StringBuilder();
			sb.append(" ").append(super.getVMArguments(configuration, mode));
			// enable JMX RMI Connector - allows remote debug for Fuse 7.8-
			sb.append(" -Dorg.apache.camel.jmx.createRmiConnector=True");
			// ensure process is not forked so that current implementation of Camel Debugger can still automatically connect with Fuse 7.9+
			sb.append(" -Dspring-boot.run.fork=false");
			return sb.toString();
		}
		return super.getVMArguments(configuration, mode);
	}

	/**
	 * returns a process name which will be interpreted by the jmx tooling
	 * 
	 * @return
	 */
	public abstract String getEclipseProcessName();
}
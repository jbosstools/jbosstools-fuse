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

package org.fusesource.ide.server.karaf.core.server;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xStartupLaunchConfigurator;


/**
 * @author lhein
 */
@Deprecated
public class KarafServerBehaviourDelegate extends ServerBehaviourDelegate {

	public static final String QUOTE = "\"";
	public static final String SPACE = " ";
	public static final String SEPARATOR = File.separator;
	
	public static final String KARAF_MAIN_CLASS = "org.apache.karaf.main.Main";
	public static final String KARAF_STOP_CLASS = "org.apache.karaf.main.Stop";
	
	private IProcess process;
	
	private ILaunchConfigurationWorkingCopy wc;
	
	/**
	 * empty default constructor
	 */
	public KarafServerBehaviourDelegate() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#initialize(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void initialize(IProgressMonitor monitor) {
		super.initialize(monitor);
		setServerState(IServer.STATE_STOPPED);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#dispose()
	 */
	@Override
	public void dispose() {
		stop(true);
		super.dispose();
	}

	/**
	 * sets the servers state to launching and also sets the correct launch mode
	 * 
	 * @param mode
	 */
	public void setupLaunch(String mode) {
		setServerRestartState(false);
		setServerState(IServer.STATE_STARTING);
		setMode(mode);
	}
	
	/**
	 * sets the servers state to STARTED
	 */
	public void setLaunched() {
		setServerState(IServer.STATE_STARTED);
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#setupLaunchConfiguration(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy,
			IProgressMonitor monitor) throws CoreException {
		new Karaf2xStartupLaunchConfigurator(getServer()).configure(workingCopy);
	}
	
	public void setKarafProcess(IProcess proc, IProgressMonitor monitor) {
		this.process = proc;
		if (proc != null) setupProcessMonitor();
	}
	
	/**
	 * monitors the server process and updates the server status on changes
	 */
	private void setupProcessMonitor() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (process != null && !process.isTerminated()) {
					// wait for the process to end
					try {
						Thread.sleep(2000L);
					} catch (InterruptedException ex) {
						// ignore
					}
				}
				KarafServerBehaviourDelegate.this.stop(false);
			}
		});
		t.setName("Process Watcher " + process.getLabel());
		t.start();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#stop(boolean)
	 */
	@Override
	public void stop(boolean force) {
		if (getServer().getServerState() == IServer.STATE_STOPPING ||
			getServer().getServerState() == IServer.STATE_STOPPED) {
			// no need to stop a server which is already stopped or being stopped atm
			return;
		}
		
		setServerState(IServer.STATE_STOPPING);

		if (process != null && wc != null) {
			try {
				String workingDir = wc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, "");
				File workDirFile = new File(workingDir + File.separator + "bin");
				ProcessBuilder pb = new ProcessBuilder();
				boolean isWindows = System.getProperty("os.name" ).toLowerCase().indexOf("windows") != -1;
				if (isWindows) {
					pb.command("cmd", "/C", "stop.bat");
				} else {
					pb.command("./stop");
				}
				pb.directory(workDirFile);
				Process karafStopProcess = pb.start();
				while (true) {
					try {
						karafStopProcess.exitValue();
						break;
					} catch (IllegalThreadStateException ex) {
						// still running
						try {
							Thread.sleep(1000);
						} catch (InterruptedException ie) {
							
						}
					}
				}
			} catch (Exception e) {
				try {
					process.terminate();	
				} catch (DebugException ex) {
					ex.printStackTrace();
				}				
			}
			process = null;
		}

		getServer().stop(false);
		setServerState(IServer.STATE_STOPPED);
		setModulesStopped();
	}
	
	/**
	 * stops all modules
	 */
	protected void setModulesStopped() {
		IModule[] modules = getServer().getModules();
		if (modules != null && modules.length > 0) {
			for (IModule mod : modules) {
				setModuleState(new IModule[] { mod }, IServer.STATE_STOPPED);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.ServerBehaviourDelegate#canRestart(java.lang.String)
	 */
	@Override
	public IStatus canRestart(String mode) {
		return Status.CANCEL_STATUS;
	}

	protected String getMainProgram() {
		if (this.process != null) {
			return KARAF_STOP_CLASS;
		} else {
			return KARAF_MAIN_CLASS;	
		}
	}
}

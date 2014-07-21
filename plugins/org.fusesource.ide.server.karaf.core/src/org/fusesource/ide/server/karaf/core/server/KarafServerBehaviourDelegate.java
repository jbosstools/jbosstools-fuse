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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;


/**
 * @author lhein
 */
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
		/*
		 * First we need to check if the launch config is already configured. If
		 * it is just exist. This logic asssumes if ATTR_MAIN_TYPE_NAME of java
		 * launch config, then its already configured. You can also have a
		 * custom key of your own
		 */
		String mainType = workingCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, ""); //$NON-NLS-1$
		if (!"".equals(mainType)) { //$NON-NLS-1$
//			return;
		}
		// This is the first time, launch config is set to defaults.
		IKarafRuntime runtime = null;
		if (getServer().getRuntime() == null) {
			
		} else {
			runtime = (IKarafRuntime)getServer().getRuntime().loadAdapter(IKarafRuntime.class, null);
		}
		
		if (runtime != null) {
			String karafInstallDir = runtime.getKarafInstallDir();
			String vmArguments = getVMArguments(karafInstallDir);
			String mainProgram = getMainProgram();
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
			
			workingCopy.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, "org.fusesource.ide.server.karaf.core.server.sourceLocator");
			workingCopy.setAttribute(ISourcePathComputer.ATTR_SOURCE_PATH_COMPUTER_ID, "org.fusesource.ide.server.karaf.core.server.sourcePathComputerDelegate");
			this.wc = workingCopy;
		}
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
		
		Display.getDefault().syncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				setServerState(IServer.STATE_STOPPING);
			}
		});
		
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
	
	protected String[] getClassPathEntries(String installPath) {
		List<IRuntimeClasspathEntry> cp = new ArrayList<IRuntimeClasspathEntry>();
		
		IPath libPath = new Path(String.format("%s%s%s%s", installPath, SEPARATOR, "lib", SEPARATOR));
		if (libPath.toFile().exists()) {
			findJars(libPath, cp);
		}
		
		String[] entries = new String[cp.size() + 1];
		entries[0] = installPath + SEPARATOR + "etc";
		int i=1;
		for (Object o : cp) {
			IRuntimeClasspathEntry e = (IRuntimeClasspathEntry)o;
			entries[i++]=e.getLocation();
		}

		return entries;
	}
	
	private void findJars(IPath path, List<IRuntimeClasspathEntry> cp) {
		File[] libs = path.toFile().listFiles(new FileFilter() {
			/*
			 * (non-Javadoc)
			 * @see java.io.FileFilter#accept(java.io.File)
			 */
			public boolean accept(File pathname) {
				return pathname.isDirectory() || (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar"));
			}
		});
		for (File lib : libs) {
			IPath p = path.append(lib.getName());
			if (lib.isFile()) {
				cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(p));
			} else {
				findJars(p, cp);
			}
		}
	}

	protected String getDeployDir() {
		IKarafRuntime runtime = (IKarafRuntime)getServer().getRuntime().loadAdapter(IKarafRuntime.class, null);
		if (runtime != null) {
			String karafInstallDir = runtime.getKarafInstallDir();
			return karafInstallDir + SEPARATOR + "deploy";
		}
		return ""; //$NON-NLS-1$
	}

	protected String getMainProgram() {
		if (this.process != null) {
			return KARAF_STOP_CLASS;
		} else {
			return KARAF_MAIN_CLASS;	
		}
	}

	protected String getVMArguments(String karafInstallDir) {
		StringBuilder vmArguments = new StringBuilder();

		vmArguments.append("-Xms128M  -Xmx512M -XX:+UnlockDiagnosticVMOptions -XX:+UnsyncloadClass ");
		vmArguments.append(SPACE + "-Dderby.system.home=" + QUOTE + karafInstallDir + SEPARATOR + "data" + SEPARATOR + "derby" + QUOTE); 
		vmArguments.append(SPACE + "-Dderby.storage.fileSyncTransactionLog=true");
		//vmArguments.append(SPACE + "-Dcom.sun.management.jmxremote");
		vmArguments.append(SPACE + "-Dkaraf.startLocalConsole=false");
		vmArguments.append(SPACE + "-Dkaraf.startRemoteShell=true");
		vmArguments.append(SPACE + "-Dkaraf.home=" + QUOTE + karafInstallDir + QUOTE); 
		vmArguments.append(SPACE + "-Dkaraf.base=" + QUOTE + karafInstallDir + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.instances=" + QUOTE + karafInstallDir + SEPARATOR + "instances" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.data=" + QUOTE + karafInstallDir + SEPARATOR + "data" + QUOTE);
		vmArguments.append(SPACE + "-Djava.util.logging.config.file=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + SEPARATOR + "java.util.logging.properties" + QUOTE);
			
		return vmArguments.toString();
	}
}

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
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.jboss.ide.eclipse.as.core.util.ArgsUtil;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.LaunchConfiguratorWithOverrides;

public class Karaf2xStartupLaunchConfigurator extends
		LaunchConfiguratorWithOverrides {
	
	public static final String QUOTE = "\"";
	public static final String SPACE = " ";
	public static final String SEPARATOR = File.separator;
	
	public static final String KARAF_MAIN_CLASS = "org.apache.karaf.main.Main";
	public static final String KARAF_STOP_CLASS = "org.apache.karaf.main.Stop";
	
	
	public Karaf2xStartupLaunchConfigurator(IServer server)
			throws CoreException {
		super(server);
	}

	@Override
	protected void doConfigure(ILaunchConfigurationWorkingCopy workingCopy)
			throws CoreException {

		IKarafRuntime runtime = null;
		if (server.getRuntime() != null) {
			runtime = (IKarafRuntime)server.getRuntime().loadAdapter(IKarafRuntime.class, null);
		}
		
		if (runtime != null) {
			String karafInstallDir = runtime.getLocation().toOSString();
			
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
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, getJreContainerPath(runtime));
		}
	}

	protected String getJreContainerPath(IKarafRuntime runtime) {
		IVMInstall vmInstall = runtime.getVM();
		if (vmInstall == null) {
			return null;
		}
		return JavaRuntime.newJREContainerPath(vmInstall).toPortableString();
	}
	
	protected String[] getClassPathEntries(String installPath) {
		List cp = new ArrayList();
		
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
	
	private void findJars(IPath path, List cp) {
		File[] libs = path.toFile().listFiles(new FileFilter() {
			/*
			 * (non-Javadoc)
			 * @see java.io.FileFilter#accept(java.io.File)
			 */
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() || (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar"));
			}
		});
		for (File lib : libs) {
			IPath p = path.append(lib.getName());
			if (lib.isFile()) {
				if (lib.getName().toLowerCase().startsWith("karaf")) cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(p));
			} else {
				findJars(p, cp);
			}
		}
	}

	protected String getDeployDir() {
		IKarafRuntime runtime = (IKarafRuntime)server.getRuntime().loadAdapter(IKarafRuntime.class, null);
		if (runtime != null) {
			String karafInstallDir = runtime.getLocation().toOSString();
			return karafInstallDir + SEPARATOR + "deploy";
		}
		return ""; //$NON-NLS-1$
	}

	protected String getMainProgram() {
		return KARAF_MAIN_CLASS;
	}

	protected String getVMArguments(String karafInstallDir) {
		StringBuilder vmArguments = new StringBuilder();

		String endorsedDirs = System.getProperty("java.endorsed.dirs");
		String extDirs = System.getProperty("java.ext.dirs");
		
		if (server.getRuntime() != null) {
			IKarafRuntime runtime = (IKarafRuntime)server.getRuntime().loadAdapter(IKarafRuntime.class, null);
			File vmLoc = runtime.getVM().getInstallLocation();
			
//			JAVA_ENDORSED_DIRS="${JAVA_HOME}/jre/lib/endorsed:${JAVA_HOME}/lib/endorsed:${KARAF_HOME}/lib/endorsed"
			endorsedDirs = createEndorsedDirValue(karafInstallDir, vmLoc);
//		    JAVA_EXT_DIRS="${JAVA_HOME}/jre/lib/ext:${JAVA_HOME}/lib/ext:${KARAF_HOME}/lib/ext"
			extDirs = createExtDirValue(karafInstallDir, vmLoc);
		}
				
		vmArguments.append("-Xms128M");
		vmArguments.append(SPACE + "-Xmx512M");
		vmArguments.append(SPACE + "-XX:+UnlockDiagnosticVMOptions");
		vmArguments.append(SPACE + "-XX:+UnsyncloadClass");
		vmArguments.append(SPACE + "-Dderby.system.home=" + QUOTE + karafInstallDir + SEPARATOR + "data" + SEPARATOR + "derby" + QUOTE); 
		vmArguments.append(SPACE + "-Dderby.storage.fileSyncTransactionLog=true");
		vmArguments.append(SPACE + "-server");
		vmArguments.append(SPACE + "-Dcom.sun.management.jmxremote");
		vmArguments.append(SPACE + "-Djava.endorsed.dirs=" + QUOTE + endorsedDirs + QUOTE);
		vmArguments.append(SPACE + "-Djava.ext.dirs=" + QUOTE + extDirs + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.startLocalConsole=false");
		vmArguments.append(SPACE + "-Dkaraf.startRemoteShell=true");
		vmArguments.append(SPACE + "-Dkaraf.home=" + QUOTE + karafInstallDir + QUOTE); 
		vmArguments.append(SPACE + "-Dkaraf.base=" + QUOTE + karafInstallDir + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.instances=" + QUOTE + karafInstallDir + SEPARATOR + "instances" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.data=" + QUOTE + karafInstallDir + SEPARATOR + "data" + QUOTE);
		vmArguments.append(SPACE + "-Djava.util.logging.config.file=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + SEPARATOR + "java.util.logging.properties" + QUOTE);
			
		return vmArguments.toString();
	}

	/**
	 * @param karafInstallDir
	 * @param vmLoc
	 * @return
	 */
	String createEndorsedDirValue(String karafInstallDir, File vmLoc) {
		//@formatter:off
		return  Paths.get(vmLoc.getPath(), "jre", "lib", "endorsed").toString() + File.pathSeparator +
				Paths.get(vmLoc.getPath(), "lib", "endorsed").toString() + File.pathSeparator +
				Paths.get(karafInstallDir, "lib", "endorsed").toString();
		//@formatter:on
	}

	/**
	 * @param karafInstallDir
	 * @param vmLoc
	 * @return
	 */
	String createExtDirValue(String karafInstallDir, File vmLoc) {
		//@formatter:off
		return  Paths.get(vmLoc.getPath(), "jre", "lib", "ext").toString() + File.pathSeparator +
				Paths.get(vmLoc.getPath(), "lib", "ext").toString() + File.pathSeparator +
				Paths.get(karafInstallDir, "lib", "ext").toString();
		//@formatter:on
	}

	@Override
	protected void doOverrides(ILaunchConfigurationWorkingCopy launchConfig) throws CoreException {
		String vmArguments = launchConfig.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
		if (server.getRuntime() != null) {
			String karafInstallDir = server.getRuntime().getLocation().toOSString();
			IKarafRuntime karafRuntime = (IKarafRuntime) server.getRuntime().loadAdapter(IKarafRuntime.class, null);
			File vmLoc = karafRuntime.getVM().getInstallLocation();
			vmArguments = ArgsUtil.setArg(vmArguments, null, "-Djava.ext.dirs", createExtDirValue(karafInstallDir, vmLoc));
			vmArguments = ArgsUtil.setArg(vmArguments, null, "-Djava.endorsed.dirs", createEndorsedDirValue(karafInstallDir, vmLoc));
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArguments);
		}
	}
}

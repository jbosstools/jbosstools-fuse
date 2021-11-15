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
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.AbstractVMInstall;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.jboss.ide.eclipse.as.core.util.ArgsUtil;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.LaunchConfiguratorWithOverrides;
import org.jboss.tools.common.jdt.debug.JavaUtilities;

/**
 * @author lheinema
 */
public abstract class BaseKarafStartupLaunchConfigurator extends LaunchConfiguratorWithOverrides {

	protected static final String QUOTE = "\"";
	protected static final String SPACE = " ";
	protected static final String SEPARATOR = File.separator;
	protected static final String ENDORSED = "endorsed";
	protected static final String EXT = "ext";
	protected static final String LIB = "lib";
	
	protected static final String KARAF_MAIN_CLASS = "org.apache.karaf.main.Main";
	protected static final String KARAF_STOP_CLASS = "org.apache.karaf.main.Stop";
	
	/**
	 * creates a basic karaf launch configurator for the given server
	 * 
	 * @param server
	 * @throws CoreException
	 */
	public BaseKarafStartupLaunchConfigurator(IServer server) throws CoreException {
		super(server);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.ide.eclipse.as.wtp.core.server.launch.LaunchConfiguratorWithOverrides#doConfigure(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	protected void doConfigure(ILaunchConfigurationWorkingCopy launchConfig) throws CoreException {
		IKarafRuntime runtime = null;
		if (server.getRuntime() != null) {
			runtime = (IKarafRuntime)server.getRuntime().loadAdapter(IKarafRuntime.class, null);
		}
		
		if (runtime != null) {
			String karafInstallDir = runtime.getLocation().toOSString();
			
			String runtimeVersion = runtime.getVersion();
			if (!isSupportedRuntimeVersion(runtimeVersion)) { 
				Activator.getLogger().error("Unhandled Version (" + runtimeVersion + ")!");
			}
			
			File vmLoc = runtime.getVM().getInstallLocation();
//			JAVA_ENDORSED_DIRS="${JAVA_HOME}/jre/lib/endorsed:${JAVA_HOME}/lib/endorsed:${KARAF_HOME}/lib/endorsed"
			String endorsedDirs = createEndorsedDirValue(karafInstallDir, vmLoc);
//		    JAVA_EXT_DIRS="${JAVA_HOME}/jre/lib/ext:${JAVA_HOME}/lib/ext:${KARAF_HOME}/lib/ext"
			String extDirs = createExtDirValue(karafInstallDir, vmLoc);
			
			String vmArguments = getVMArguments(karafInstallDir, runtime, endorsedDirs, extDirs);
			String mainProgram = getMainProgram();
			
			// For java tabs
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, karafInstallDir);
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainProgram);
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArguments);

			List<String> classPathList = new LinkedList<>();
			String[] classPathEntries = getClassPathEntries(karafInstallDir);
			if (classPathEntries != null && classPathEntries.length > 0) {
				for (String jarName : classPathEntries) {
					IPath jarPath = new Path(jarName);
					IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(jarPath);
					classPathList.add(entry.getMemento());
				}
			}
			
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classPathList);
			launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, getJreContainerPath(runtime));
		}
	}

	/**
	 * /!\ Public for test purpose
	 * 
	 */
	@Override
	public void doOverrides(ILaunchConfigurationWorkingCopy launchConfig) throws CoreException {
		IRuntime serverRuntime = server.getRuntime();
		if (serverRuntime != null) {
			IKarafRuntime runtime = (IKarafRuntime)serverRuntime.loadAdapter(IKarafRuntime.class, null);
			String karafInstallDir = serverRuntime.getLocation().toOSString();
			configureJRE(launchConfig, runtime, karafInstallDir);
			configureVMArguments(launchConfig, runtime, karafInstallDir);
		}
	}

	protected void configureVMArguments(ILaunchConfigurationWorkingCopy launchConfig, IKarafRuntime karafRuntime, String karafInstallDir)
			throws CoreException {
		String vmArguments = launchConfig.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
		IVMInstall vm = karafRuntime.getVM();
		if (!isJigsawRunning(vm)) {
			File vmLoc = vm.getInstallLocation();
			vmArguments = ArgsUtil.setArg(vmArguments, null, "-Djava.ext.dirs", createExtDirValue(karafInstallDir, vmLoc));
			vmArguments = ArgsUtil.setArg(vmArguments, null, "-Djava.endorsed.dirs", createEndorsedDirValue(karafInstallDir, vmLoc));
		}
		launchConfig.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArguments);
	}

	/**
	 *  /!\ public for test purpose
	 */
	public void configureJRE(ILaunchConfigurationWorkingCopy workingCopy, IKarafRuntime runtime, String karafInstallDir) throws CoreException {
		List<String> classPathList = new LinkedList<>();
		String[] classPathEntries = getClassPathEntries(karafInstallDir);
		if (classPathEntries != null && classPathEntries.length > 0) {
			for (String jarName : classPathEntries) {
				IPath jarPath = new Path(jarName);
				IRuntimeClasspathEntry entry = JavaRuntime.newArchiveRuntimeClasspathEntry(jarPath);
				classPathList.add(entry.getMemento());
			}
		} else {
			// ignored for now
		}
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, false);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, classPathList);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, getJreContainerPath(runtime));
	}

	/**
	 * this method checks if the given runtime is handled by this launch configurator
	 * 
	 * Override this method for every new server adapter 
	 * @return
	 */
	protected abstract boolean isSupportedRuntimeVersion(String version);
	
	protected String getJreContainerPath(IKarafRuntime runtime) {
		IVMInstall vmInstall = runtime.getVM();
		if (vmInstall == null) {
			return null;
		}
		return JavaRuntime.newJREContainerPath(vmInstall).toPortableString();
	}
	
	protected String[] getClassPathEntries(String installPath) {
		List<Object> cp = new ArrayList<>();
		
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
	
	protected void findJars(IPath path, List<Object> cp) {
		File[] libs = path.toFile().listFiles( (File pathname) -> pathname.isDirectory() || (pathname.isFile() && pathname.getName().toLowerCase().endsWith(".jar")));
		if (libs != null) {
			for (File lib : libs) {
				IPath p = path.append(lib.getName());
				if (lib.isFile()) {
					if (lib.getName().toLowerCase().startsWith("karaf")) cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(p));
				} else {
					findJars(p, cp);
				}
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
	
	/**
	 * /!\ Public for test purpose
	 */
	public String getVMArguments(String karafInstallDir, IKarafRuntime runtime, String endorsedDirs, String extDirs) {
		return getBaseVMArguments(karafInstallDir, runtime, endorsedDirs, extDirs);
	}
	
	private String getBaseVMArguments(String karafInstallDir, IKarafRuntime runtime, String endorsedDirs, String extDirs) {
		StringBuilder vmArguments = new StringBuilder();

		vmArguments.append("-Xms128M");
		vmArguments.append(SPACE + "-Xmx512M");
		vmArguments.append(SPACE + "-XX:+UnlockDiagnosticVMOptions");
		IVMInstall vm = runtime.getVM();
		if (!isJigsawRunning(vm)) {
			vmArguments.append(SPACE + "-XX:+UnsyncloadClass");
			vmArguments.append(SPACE + "-Djava.endorsed.dirs=" + QUOTE + endorsedDirs + QUOTE);
			vmArguments.append(SPACE + "-Djava.ext.dirs=" + QUOTE + extDirs + QUOTE);
		}
		vmArguments.append(SPACE + "-server");
		vmArguments.append(SPACE + "-Dcom.sun.management.jmxremote");
		vmArguments.append(SPACE + "-Dkaraf.startLocalConsole=false");
		vmArguments.append(SPACE + "-Dkaraf.startRemoteShell=true");
		vmArguments.append(SPACE + "-Dkaraf.home=" + QUOTE + karafInstallDir + QUOTE); 
		vmArguments.append(SPACE + "-Dkaraf.base=" + QUOTE + karafInstallDir + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.instances=" + QUOTE + karafInstallDir + SEPARATOR + "instances" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.data=" + QUOTE + karafInstallDir + SEPARATOR + "data" + QUOTE);
		vmArguments.append(SPACE + "-Djava.util.logging.config.file=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + SEPARATOR + "java.util.logging.properties" + QUOTE);
			
		return vmArguments.toString();
	}

	protected boolean isJigsawRunning(IVMInstall vm) {
		return vm instanceof AbstractVMInstall && JavaUtilities.isJigsawRunning(((AbstractVMInstall) vm).getJavaVersion());
	}

	/**
	 * /!\ Public for test purpose
	 * @param karafInstallDir
	 * @param vmLoc
	 * @return
	 */
	public String createEndorsedDirValue(String karafInstallDir, File vmLoc) {
		//@formatter:off
		return  Paths.get(vmLoc.getPath(), "jre", LIB, ENDORSED).toString() + File.pathSeparator +
				Paths.get(vmLoc.getPath(), LIB, ENDORSED).toString() + File.pathSeparator +
				Paths.get(karafInstallDir, LIB, ENDORSED).toString();
		//@formatter:on
	}

	/**
	 * /!\ Public for test purpose
	 * @param karafInstallDir
	 * @param vmLoc
	 * @return
	 */
	public String createExtDirValue(String karafInstallDir, File vmLoc) {
		//@formatter:off
		return  Paths.get(vmLoc.getPath(), "jre", LIB, EXT).toString() + File.pathSeparator +
				Paths.get(vmLoc.getPath(), LIB, EXT).toString() + File.pathSeparator +
				Paths.get(karafInstallDir, LIB, EXT).toString();
		//@formatter:on
	}	
}

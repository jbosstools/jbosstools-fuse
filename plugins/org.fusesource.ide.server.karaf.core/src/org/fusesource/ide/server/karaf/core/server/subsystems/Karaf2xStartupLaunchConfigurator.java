package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
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
		}
		
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

	@Override
	protected void doOverrides(ILaunchConfigurationWorkingCopy launchConfig)
			throws CoreException {
		// TODO Auto-generated method stub
		
	}
}
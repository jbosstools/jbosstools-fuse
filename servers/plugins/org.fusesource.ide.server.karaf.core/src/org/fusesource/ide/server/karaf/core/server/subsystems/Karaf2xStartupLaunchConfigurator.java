/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants;
import org.jboss.ide.eclipse.as.core.util.ArgsUtil;

/**
 * @author lheinema
 */
public class Karaf2xStartupLaunchConfigurator extends BaseKarafStartupLaunchConfigurator {

	public Karaf2xStartupLaunchConfigurator(IServer server) throws CoreException {
		super(server);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.BaseKarafStartupLaunchConfigurator#isSupportedRuntimeVersion(java.lang.String)
	 */
	@Override
	protected boolean isSupportedRuntimeVersion(String version) {
		return !Strings.isBlank(version) && version.startsWith(IKarafToolingConstants.KARAF_VERSION_2X);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.BaseKarafStartupLaunchConfigurator#doOverrides(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
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
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.BaseKarafStartupLaunchConfigurator#getVMArguments(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected String getVMArguments(String karafInstallDir, String endorsedDirs, String extDirs) {
		// karaf 2.4+ uses the karaf 3&4 launch arguments instead of the 2.x
		if (server.getRuntime() != null && server.getRuntime().getRuntimeType().getVersion().startsWith("2.4")) {
			return getKaraf24AndLaterVMArguments(karafInstallDir, endorsedDirs, extDirs);
		}
		return super.getVMArguments(karafInstallDir, endorsedDirs, extDirs);
	}
	
	private String getKaraf24AndLaterVMArguments(String karafInstallDir, String endorsedDirs, String extDirs) {
		StringBuilder vmArguments = new StringBuilder();
		
		vmArguments.append("-Xms128M");
		vmArguments.append(SPACE + "-Xmx512M");
		vmArguments.append(SPACE + "-XX:+UnlockDiagnosticVMOptions");
		vmArguments.append(SPACE + "-XX:+UnsyncloadClass");
		vmArguments.append(SPACE + "-server ");
		vmArguments.append(SPACE + "-Dcom.sun.management.jmxremote");
		vmArguments.append(SPACE + "-Djava.endorsed.dirs=" + QUOTE + endorsedDirs + QUOTE);
		vmArguments.append(SPACE + "-Djava.ext.dirs=" + QUOTE + extDirs + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.startLocalConsole=false");
		vmArguments.append(SPACE + "-Dkaraf.startRemoteShell=true");
		vmArguments.append(SPACE + "-Dkaraf.home=" + QUOTE + karafInstallDir + QUOTE); 
		vmArguments.append(SPACE + "-Dkaraf.base=" + QUOTE + karafInstallDir + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.instances=" + QUOTE + karafInstallDir + SEPARATOR + "instances" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.data=" + QUOTE + karafInstallDir + SEPARATOR + "data" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.etc=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + QUOTE);
		vmArguments.append(SPACE + "-Djava.io.tmpdir=" + QUOTE + karafInstallDir + SEPARATOR + "data" + SEPARATOR + "tmp" + QUOTE);
		vmArguments.append(SPACE + "-Djava.util.logging.config.file=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + SEPARATOR + "java.util.logging.properties" + QUOTE);
		
		return vmArguments.toString();
	}
}

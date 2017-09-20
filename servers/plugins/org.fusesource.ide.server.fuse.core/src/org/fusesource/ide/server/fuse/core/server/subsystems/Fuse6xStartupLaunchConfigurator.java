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
package org.fusesource.ide.server.fuse.core.server.subsystems;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.fuse.core.util.FuseToolingConstants;
import org.fusesource.ide.server.karaf.core.server.subsystems.BaseKarafStartupLaunchConfigurator;

/**
 * @author lhein
 */
public class Fuse6xStartupLaunchConfigurator extends BaseKarafStartupLaunchConfigurator {
	
	public Fuse6xStartupLaunchConfigurator(IServer server) throws CoreException {
		super(server);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.BaseKarafStartupLaunchConfigurator#isSupportedRuntimeVersion(java.lang.String)
	 */
	@Override
	protected boolean isSupportedRuntimeVersion(String version) {
		return !Strings.isBlank(version) && version.startsWith(FuseToolingConstants.FUSE_VERSION_6X);
	}
	
	@Override
	protected String getVMArguments(String karafInstallDir, String endorsedDirs, String extDirs) {
		// removed derby, introduced 2 new XX:(Max)PermSize parameters
		StringBuilder vmArguments = new StringBuilder();
		
		vmArguments.append("-Xms128M");
		vmArguments.append(SPACE + "-Xmx512M");
		vmArguments.append(SPACE + "-XX:+UnlockDiagnosticVMOptions");
		vmArguments.append(SPACE + "-XX:+UnsyncloadClass");
		vmArguments.append(SPACE + "-XX:PermSize=16M -XX:MaxPermSize=128M ");
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

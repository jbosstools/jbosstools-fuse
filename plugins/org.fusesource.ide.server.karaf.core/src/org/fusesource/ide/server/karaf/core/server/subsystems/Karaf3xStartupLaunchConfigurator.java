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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;

public class Karaf3xStartupLaunchConfigurator extends Karaf2xStartupLaunchConfigurator {
	

	public Karaf3xStartupLaunchConfigurator(IServer server)
			throws CoreException {
		super(server);
	}
	
	@Override
	protected String getVMArguments(String karafInstallDir) {
		return get3xVMArguments(karafInstallDir);
	}
	
	protected String get2xVMArguments(String karafInstallDir) {
		return super.getVMArguments(karafInstallDir);
	}	
	
	protected String get3xVMArguments(String karafInstallDir) {
		StringBuilder vmArguments = new StringBuilder();

		vmArguments.append("-Xms128M");
		vmArguments.append(SPACE + "-Xmx512M");
		vmArguments.append(SPACE + "-XX:+UnlockDiagnosticVMOptions");
		vmArguments.append(SPACE + "-XX:+UnsyncloadClass");
		vmArguments.append(SPACE + "-server ");
		vmArguments.append(SPACE + "-Dcom.sun.management.jmxremote");
		vmArguments.append(SPACE + "-Dkaraf.startLocalConsole=false");
		vmArguments.append(SPACE + "-Dkaraf.startRemoteShell=true");
		vmArguments.append(SPACE + "-Dkaraf.home=" + QUOTE + karafInstallDir + QUOTE); 
		vmArguments.append(SPACE + "-Dkaraf.base=" + QUOTE + karafInstallDir + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.instances=" + QUOTE + karafInstallDir + SEPARATOR + "instances" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.data=" + QUOTE + karafInstallDir + SEPARATOR + "data" + QUOTE);
		vmArguments.append(SPACE + "-Dkaraf.etc=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + QUOTE);
		vmArguments.append(SPACE + "-Djava.io.tmpdir=" + QUOTE + karafInstallDir + SEPARATOR + "tmp" + QUOTE);
		vmArguments.append(SPACE + "-Djava.util.logging.config.file=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + SEPARATOR + "java.util.logging.properties" + QUOTE);
		vmArguments.append(SPACE + "-Djavax.management.builder.initial=org.apache.karaf.management.boot.KarafMBeanServerBuilder" + QUOTE);
		
		return vmArguments.toString();
	}
}
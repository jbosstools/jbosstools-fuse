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
package org.fusesource.ide.server.servicemix.core.server.subsystems;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.runtime.IKarafRuntime;
import org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xStartupLaunchConfigurator;

/**
 * @author lhein
 */
public class ServiceMix5xStartupLaunchConfigurator extends
		Karaf2xStartupLaunchConfigurator {
	
	public ServiceMix5xStartupLaunchConfigurator(IServer server)
			throws CoreException {
		super(server);
	}

	protected String getMainProgram() {
		return super.getMainProgram();
	}

	protected String getVMArguments(String karafInstallDir) {
		IKarafRuntime runtime = null;
		if (server.getRuntime() != null) {
			runtime = (IKarafRuntime)server.getRuntime().loadAdapter(IKarafRuntime.class, null);
			if (runtime.getVersion().startsWith("5.2.") || runtime.getVersion().startsWith("5.1.") || runtime.getVersion().startsWith("5.0.") || runtime.getVersion().startsWith("4.")) {
				super.getVMArguments(karafInstallDir);
			}
		}
		String args = super.getVMArguments(karafInstallDir);
		args += SPACE + "-Djavax.management.builder.initial=org.apache.karaf.management.boot.KarafMBeanServerBuilder";
		args += SPACE + "-Dkaraf.etc=\"" + karafInstallDir + File.separator + "etc\""; // <<< NEEDED since 5.3 
		return args;
	}
}

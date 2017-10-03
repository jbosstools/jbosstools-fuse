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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants;

/**
 * @author lheinema
 */
public class Karaf2xStartupLaunchConfigurator extends BaseKarafStartupLaunchConfigurator {

	public Karaf2xStartupLaunchConfigurator(IServer server) throws CoreException {
		super(server);
	}
	
	@Override
	protected boolean isSupportedRuntimeVersion(String version) {
		return !Strings.isBlank(version) && version.startsWith(IKarafToolingConstants.KARAF_VERSION_2X);
	}
	
	@Override
	public String getVMArguments(String karafInstallDir, String endorsedDirs, String extDirs) {
		StringBuilder vmArguments = new StringBuilder(super.getVMArguments(karafInstallDir, endorsedDirs, extDirs));
		if (isNewerThanKaraf24()) {
			vmArguments.append(SPACE + "-Dkaraf.etc=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + QUOTE);
			return vmArguments.toString();
		} else {
			vmArguments.append(SPACE + "-Dderby.system.home=" + QUOTE + karafInstallDir + SEPARATOR + "data" + SEPARATOR + "derby" + QUOTE); 
			vmArguments.append(SPACE + "-Dderby.storage.fileSyncTransactionLog=true");
			return vmArguments.toString();
		}
	}

	protected boolean isNewerThanKaraf24() {
		return server.getRuntime() != null && server.getRuntime().getRuntimeType().getVersion().startsWith("2.4");
	}
}

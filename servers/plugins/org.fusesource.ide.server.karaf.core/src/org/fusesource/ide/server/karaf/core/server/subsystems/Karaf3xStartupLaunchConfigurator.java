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
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.server.karaf.core.util.IKarafToolingConstants;

public class Karaf3xStartupLaunchConfigurator extends BaseKarafStartupLaunchConfigurator {
	
	public Karaf3xStartupLaunchConfigurator(IServer server) throws CoreException {
		super(server);
	}
	
	@Override
	protected boolean isSupportedRuntimeVersion(String version) {
		return !Strings.isBlank(version) && version.startsWith(IKarafToolingConstants.KARAF_VERSION_3X);
	}
	
	@Override
	public String getVMArguments(String karafInstallDir, String endorsedDirs, String extDirs) {
		StringBuilder vmArguments = new StringBuilder(super.getVMArguments(karafInstallDir, endorsedDirs, extDirs));
		vmArguments.append(SPACE + "-Dkaraf.etc=" + QUOTE + karafInstallDir + SEPARATOR + "etc" + QUOTE);
		return vmArguments.toString();
	}
}

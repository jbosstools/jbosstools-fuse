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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;
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
		return super.getVMArguments(karafInstallDir);
	}
}

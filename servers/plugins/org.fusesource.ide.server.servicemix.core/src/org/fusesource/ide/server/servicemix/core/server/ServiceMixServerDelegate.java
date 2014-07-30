/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.servicemix.core.server;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.server.karaf.core.server.KarafServerDelegate;
import org.fusesource.ide.server.servicemix.core.server.subsystems.ServiceMix4xStartupLaunchConfigurator;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfigurator;

/**
 * @author lhein
 */
public class ServiceMixServerDelegate extends KarafServerDelegate {
	
	public static final String DEFAULT_SMX_SSH_USER = "smx";
	public static final String DEFAULT_SMX_SSH_PASSWORD = "smx";
	
	@Override
	protected String getDefaultUsername() {
		return DEFAULT_SMX_SSH_USER;
	}
	
	@Override
	protected String getDefaultPassword() {
		return DEFAULT_SMX_SSH_PASSWORD;
	}
	
	@Override
	public ILaunchConfigConfigurator getLaunchConfigurator() throws CoreException {
		return new ServiceMix4xStartupLaunchConfigurator(getServer());
	}
}

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
import org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xLaunchController;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfigurator;

/**
 * @author lhein
 *
 */
public class ServiceMix4xLaunchController extends Karaf2xLaunchController {

	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.server.karaf.core.server.subsystems.Karaf2xLaunchController#getConfigurator()
	 */
	@Override
	protected ILaunchConfigConfigurator getConfigurator() throws CoreException {
		return new ServiceMix4xStartupLaunchConfigurator(getServer());
	}
}

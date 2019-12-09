/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.tests.integration.remote.debug;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

public class RemoteCamelLaunchConfigurationDelegateOverJMXIT extends AbstractRemoteCamelLaunchConfigurationDelegate {
	
	@Override
	protected void configureConnection(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ICamelDebugConstants.ATTR_JMX_URI_ID, "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi/camel");
	}
}

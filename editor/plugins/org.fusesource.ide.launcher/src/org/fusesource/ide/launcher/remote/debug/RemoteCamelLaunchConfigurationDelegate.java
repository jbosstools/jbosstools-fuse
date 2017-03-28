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
package org.fusesource.ide.launcher.remote.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.util.CamelJMXLaunchConfiguration;

public class RemoteCamelLaunchConfigurationDelegate extends LaunchConfigurationDelegate {
	
	public static final String LAUNCH_CONFIGURATION_TYPE = "org.fusesource.ide.launcher.camelcontext.remote";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		CamelJMXLaunchConfiguration conf = new CamelJMXLaunchConfiguration(configuration, ICamelDebugConstants.DEFAULT_REMOTE_JMX_URI);
		IDebugTarget target = new CamelDebugTarget(launch, null, conf.getJMXUrl(), conf.getJMXUser(), conf.getJMXPassword());
		launch.addDebugTarget(target);
	}

}

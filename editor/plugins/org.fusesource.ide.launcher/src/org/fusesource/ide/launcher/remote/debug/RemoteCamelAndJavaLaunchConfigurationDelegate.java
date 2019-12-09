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
package org.fusesource.ide.launcher.remote.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.internal.launching.JavaRemoteApplicationLaunchConfigurationDelegate;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.util.CamelJMXLaunchConfiguration;

public class RemoteCamelAndJavaLaunchConfigurationDelegate extends JavaRemoteApplicationLaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		super.launch(configuration, mode, launch, monitor);
		
		CamelJMXLaunchConfiguration conf = new CamelJMXLaunchConfiguration(configuration, ICamelDebugConstants.DEFAULT_REMOTE_JMX_URI);
		IDebugTarget target = new CamelDebugTarget(launch, null, conf.getJMXUrl(), conf.getJMXUser(), conf.getJMXPassword());
		launch.addDebugTarget(target);
	}
}

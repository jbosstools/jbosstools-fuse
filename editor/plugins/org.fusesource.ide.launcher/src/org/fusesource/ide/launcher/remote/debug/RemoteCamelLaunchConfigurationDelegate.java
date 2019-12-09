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

import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.util.CamelJMXLaunchConfiguration;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;

public class RemoteCamelLaunchConfigurationDelegate extends LaunchConfigurationDelegate {
	
	public static final String LAUNCH_CONFIGURATION_TYPE = "org.fusesource.ide.launcher.camelcontext.remote";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		CamelJMXLaunchConfiguration conf = new CamelJMXLaunchConfiguration(configuration, ICamelDebugConstants.DEFAULT_REMOTE_JMX_URI);
		IDebugTarget target = createDebugTarget(configuration, launch, conf);
		launch.addDebugTarget(target);
	}

	private IDebugTarget createDebugTarget(ILaunchConfiguration configuration, ILaunch launch, CamelJMXLaunchConfiguration conf) throws CoreException {
		String providerId = configuration.getAttribute(ICamelDebugConstants.ATTR_JMX_CONNECTION_WRAPPER_PROVIDER_ID, "");
		String connectionName = configuration.getAttribute(ICamelDebugConstants.ATTR_JMX_CONNECTION_WRAPPER_CONNECTION_NAME, "");
		IDebugTarget target;
		if(providerId.isEmpty()){
			target = new CamelDebugTarget(launch, null, conf.getJMXUrl(), conf.getJMXUser(), conf.getJMXPassword());
		} else {
			IConnectionProvider provider = ExtensionManager.getProvider(providerId);
			IConnectionWrapper jmxConnectionWrapper = Stream.of(provider.getConnections())
					.filter(connection -> connectionName.equals(provider.getName(connection)))
					.findAny()
					.orElseThrow(() -> new CoreException(new Status(IStatus.ERROR, Activator.getBundleID(), "Cannot retrieve the JMX Connection "+ connectionName)));
				target = new CamelDebugTarget(launch, null, jmxConnectionWrapper);
		}
		return target;
	}

}

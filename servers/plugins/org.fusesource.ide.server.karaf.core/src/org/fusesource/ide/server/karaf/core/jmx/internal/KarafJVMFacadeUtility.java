/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.server.karaf.core.jmx.internal;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.jboss.ide.eclipse.as.core.util.JBossServerBehaviorUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.AbstractStartJavaServerLaunchDelegate;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.jvmmonitor.core.IHost;
import org.jboss.tools.jmx.jvmmonitor.core.JvmModel;

public class KarafJVMFacadeUtility {
	public static IActiveJvm findJvmForServer(IServer server) {
		String  progArgs, main;
		main = progArgs = null;
		IControllableServerBehavior beh = JBossServerBehaviorUtils.getControllableBehavior(server);
		if( beh != null && server.getServerState() == IServer.STATE_STARTED ) {
			// Find the IProcess
			try {
				Object o = beh.getSharedData(AbstractStartJavaServerLaunchDelegate.PROCESS);
				if( o instanceof IProcess ) {
					IProcess proc = (IProcess)o;
					ILaunch launch = proc.getLaunch();
					ILaunchConfiguration lc = launch.getLaunchConfiguration();
					progArgs = lc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String)null);
					main = lc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String)null);
				}
			} catch( CoreException ce) {
				Activator.getLogger().error(ce);
			}
		}
		
		String target = main + (progArgs == null ? "" : (" " + progArgs));
		target = target.replaceAll("\"", "").trim();
		JvmModel model = JvmModel.getInstance();
		List<IHost> hosts = model.getHosts();
		for (IHost host : hosts) {
			List<IActiveJvm> jvms = host.getActiveJvms();
			for (IActiveJvm jvm : jvms) {
				String command = jvm.getLaunchCommand();
				if( command.equals(target)) {
					return jvm;
				}
			}
		}
		return null;
	}
}

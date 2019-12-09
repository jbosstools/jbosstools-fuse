/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.server.karaf.core.util;

import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

public class ServerNamingUtil {

	public static String getDefaultServerName(IServer server, IRuntime rt) {
		String runtimeName = rt.getName();
		String base;
		if( runtimeName == null || runtimeName.trim().isEmpty()) //$NON-NLS-1$
			base = server.getServerType().getName();
		else 
			base = NLS.bind("{0} Server", runtimeName);
		return getDefaultServerName( base);
	}
	
	public static String getDefaultServerName( String base) {
		if( findServer(base) == null ) {
			return base;
		}
		int i = 1;
		while( findServer(
				NLS.bind("{0} ({1})", base, i)) != null ) //$NON-NLS-1$
			i++;
		return NLS.bind("{0} ({1})", base, i); //$NON-NLS-1$
	}

	public static IServer findServer(String name) {
		IServer[] servers = ServerCore.getServers();
		for( int i = 0; i < servers.length; i++ ) {
			if (name.trim().equals(servers[i].getName()))
				return servers[i];
		}
		return null;
	}
}

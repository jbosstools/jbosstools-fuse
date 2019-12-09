/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.publish;

import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author lhein
 */
public interface IPublishBehaviour {
	/**
	 * publishes the module to the given server
	 * 
	 * @param server  		the IServer to publish to
	 * @param module  		the IModule being published
	 * @param symbolicName  The name of the bundle
	 * @param version  		the version of the bundle
	 * @param file     		The file representing the deployment
	 * @return
	 */
	public int publish(IServer server, IModule[] module, String symbolicName, String version, IPath file);
	
	/**
	 * 
	 * uninstalls the given module from the server
	 * 
	 * @param server			the server to remove module from
	 * @param module			the module to remove
	 * @param symbolicName		the symbolic name of the bundle
	 * @param version			the version of the bundle
	 * @return	true on success	
	 */
	public boolean uninstall(IServer server, IModule[] module, String symbolicName, String version);
}

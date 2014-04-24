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
package org.fusesource.ide.server.karaf.core.publish;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;

/**
 * @author lhein
 */
public interface IPublishBehaviour {
	/**
	 * publishes the module to the given server
	 * 
	 * @param server	the server to publish to
	 * @param module	the module to publish
	 * @return	true on success
	 */
	public boolean publish(IServer server, IModule[] module);
	
	/**
	 * uninstalls the given module from the server
	 * 
	 * @param server	the server to remove module from
	 * @param module	the module to remove
	 * @return	true on success
	 */
	public boolean uninstall(IServer server, IModule[] module);
}

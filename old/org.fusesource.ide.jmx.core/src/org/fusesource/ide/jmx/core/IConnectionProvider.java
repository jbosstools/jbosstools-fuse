/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/
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

package org.fusesource.ide.jmx.core;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;

/**
 * API for a connection provider.
 * A connection provider represents, creates, adds, and removes
 * instances of one type of jmx connection
 */
public interface IConnectionProvider {

	/**
	 * Get the id of the provider
	 * @return
	 */
	public String getId();

	/**
	 * Get a displayable name for a specific connection
	 * @param wrapper
	 * @return
	 */
	public String getName(IConnectionWrapper wrapper);

	/**
	 * Get all connections that can be found
	 * @return
	 */
	public IConnectionWrapper[] getConnections();

	/**
	 * Can this provider create new connection wrappers via this interface?
	 * @return
	 */
	public boolean canCreate();

	/**
	 * Can this provider remove this connection?
	 * @param wrapper
	 * @return
	 */
	public boolean canDelete(IConnectionWrapper wrapper);

	/**
	 * Create a new connection wrapper based on a HashMap
	 * filled with properties of any object type.
	 * Do not persist it. Just return it.
	 * @param map
	 * @return The created wrapper, or null
	 * @throws CoreException if an error occurs
	 */
	public IConnectionWrapper createConnection(Map map) throws CoreException;

	/**
	 * Now persist the connection properly; save it, whatever
	 * @param connection
	 */
	public void addConnection(IConnectionWrapper connection);
	public void removeConnection(IConnectionWrapper connection);

	public void addListener(IConnectionProviderListener listener);
	public void removeListener(IConnectionProviderListener listener);

}

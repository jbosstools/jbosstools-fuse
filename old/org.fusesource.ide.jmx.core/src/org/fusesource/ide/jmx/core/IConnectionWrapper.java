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

import javax.management.remote.JMXConnector;

import org.eclipse.core.runtime.CoreException;
import org.fusesource.ide.jmx.core.tree.Root;



/**
 * API for a connection wrapper
 */
public interface IConnectionWrapper {
	public IConnectionProvider getProvider();

	public JMXConnector getConnector();
	
	public boolean isConnected();
	public boolean canControl();
	public void connect() throws Exception;
	public void disconnect() throws Exception;
	
	/**
	 * Loads the root object in the current thread if it is not loaded.
	 * If it is loaded, does nothing.
	 */
	public void loadRoot();
	
	/**
	 * Gets the current root object, or null if its not yet loaded.
	 * @return
	 */
	public Root getRoot();
	public void run(IJMXRunnable runnable) throws CoreException;
}

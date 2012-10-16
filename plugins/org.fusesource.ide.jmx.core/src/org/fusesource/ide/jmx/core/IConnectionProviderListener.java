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
package org.fusesource.ide.jmx.core;

/**
 * Listen to connection provider events
 *   // TODO this needs improvement
 */
public interface IConnectionProviderListener {
	public void connectionAdded(IConnectionWrapper connection);
	public void connectionRemoved(IConnectionWrapper connection);
	public void connectionChanged(IConnectionWrapper connection);
}

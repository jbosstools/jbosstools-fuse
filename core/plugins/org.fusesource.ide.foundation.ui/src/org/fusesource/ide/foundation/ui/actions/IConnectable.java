/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.actions;

public interface IConnectable {

	public boolean isConnected();

	/**
	 * We can try to connect but it could keep trying for a while and fail, so even though we might not have managed to connect
	 * yet, we should maybe disconnect before trying to connect again.
	 * 
	 * Returns true if we should try and connect as we have not yet - or false if we've connected or are currently trying to connect.
	 */
	public boolean shouldConnect();

	public void connect() throws Exception;

	public void disconnect() throws Exception;
}

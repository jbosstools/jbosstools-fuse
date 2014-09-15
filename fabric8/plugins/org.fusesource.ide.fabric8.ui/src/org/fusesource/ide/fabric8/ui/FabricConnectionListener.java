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
package org.fusesource.ide.fabric8.ui;

/**
 * @author lhein
 *
 */
public interface FabricConnectionListener {
	public static final String EVENT_TYPE_CONNECT = "CONNECTED";
	public static final String EVENT_TYPE_DISCONNECT = "DISCONNECTED";
	
	/**
	 * called when connecting
	 */
	void onFabricConnected();
	
	/**
	 * called when disconnecting
	 */
	void onFabricDisconnected();
}

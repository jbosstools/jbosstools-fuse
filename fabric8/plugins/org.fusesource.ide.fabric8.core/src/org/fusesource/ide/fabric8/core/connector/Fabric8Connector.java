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
package org.fusesource.ide.fabric8.core.connector;

import java.io.IOException;

/**
 * connector for Fabric8
 * 
 * @author lhein
 */
public class Fabric8Connector {
	
	private Fabric8ConnectorType connection;
	
	/**
	 * creates a fabric8 connector using the specified connection
	 * 
	 * @param connection	the connection to use
	 */
	public Fabric8Connector(Fabric8ConnectorType connection) {
		this.connection = connection;
	}
	
	/**
	 * returns the connection
	 * 
	 * @return the connection
	 */
	public Fabric8ConnectorType getConnection() {
		return this.connection;
	}
	
	/**
	 * connects to the fabric
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException {
		if (this.connection != null) {
			this.connection.connect();
		}
	}
	
	/**
	 * disconnects from the fabric
	 */
	public void disconnect() {
		if (this.connection != null) {
			this.connection.disconnect();
		}
	}
	
	/**
	 * checks wether the connection is established or not
	 * 
	 * @return
	 */
	public boolean isConnected() {
		if (this.connection != null) {
			return this.connection.isConnected();
		}
		return false;
	}
}

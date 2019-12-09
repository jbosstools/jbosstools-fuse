/*******************************************************************************
 * Copyright (c)2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.activemq.internal;


import java.util.Collection;
import java.util.Collections;

/**
 * Query for a single connection.
 */
public class ConnectionQuery {

	private final BrokerFacade mBrokerFacade;
	private String mConnectionID;

	public ConnectionQuery(BrokerFacade brokerFacade) {
		mBrokerFacade = brokerFacade;
	}

	public void destroy() {
		// empty
	}

	public void setConnectionID(String connectionID) {
		mConnectionID = connectionID;
	}

	public String getConnectionID() {
		return mConnectionID;
	}

	public ConnectionViewFacade getConnection() throws Exception {
		String connectionID = getConnectionID();
		if (connectionID == null)
			return null;
		return mBrokerFacade.getConnection(connectionID);
	}

	public Collection<SubscriptionViewFacade> getConsumers() throws Exception {
		String connectionID = getConnectionID();
		if (connectionID == null)
			return Collections.emptyList();
		return mBrokerFacade.getConsumersOnConnection(connectionID);
	}

}
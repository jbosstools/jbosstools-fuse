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

package org.fusesource.ide.jmx.activemq.navigator;


import io.fabric8.activemq.facade.BrokerFacade;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;


public class BrokerNode extends NodeSupport implements ImageProvider, Refreshable {

	// TODO - Connection, Connector, Subscription
	private final BrokerFacade facade;
	private final String brokerName;
	private String userName;
	private String password;

	public BrokerNode(Node parent, BrokerFacade facade, String brokerName) {
		super(parent);
		this.facade = facade;
		this.brokerName = brokerName;

		addChild(new QueuesNode(this));
		addChild(new TopicsNode(this));
		addChild(new ConnectionsNode(this));

		try {
			setPropertyBean(facade.getBrokerAdmin());
		} catch (Exception e) {
			Activator.getLogger().warning("Failed to get broker admin: "+ e, e);
		}
	}

	@Override
	public String toString() {
		return brokerName;
	}

	public BrokerFacade getFacade() {
		return facade;
	}


	@Override
	public void refresh() {
		// TODO don't use refresh parent & reselect it yet as for now this means
		// reloading the entire JMX connection typically, which doesn't work too well right now
		// as the parent node becomes a JvmConnectionWrapper rather than Root
		// so we can't easily find the nodes to expand again.
		//
		// refreshParent();
		if (getParent() instanceof Refreshable) {
			Refreshable refreshable = (Refreshable) getParent();
			refreshable.refresh();
		}

	}

	public void handleException(Node node, Exception e) {
		ActiveMQJMXPlugin.getLogger().warning("Failed to load node " + node + " due to: " + e, e);
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	@Override
	public Image getImage() {
		return ActiveMQJMXPlugin.getDefault().getImage("message_broker.png");
	}

}

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

package org.fusesource.ide.jmx.activemq.navigator;

import java.util.Collection;
import java.util.Objects;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;
import org.fusesource.ide.jmx.activemq.internal.BrokerFacade;
import org.fusesource.ide.jmx.activemq.internal.SubscriptionViewFacade;
import org.jboss.tools.jmx.ui.ImageProvider;


public class TopicConsumersNode extends RefreshableCollectionNode implements ImageProvider {

	private final BrokerNode brokerNode;
	private final TopicNode queueNode;

	public TopicConsumersNode(TopicNode queueNode) {
		super(queueNode);
		this.queueNode = queueNode;
		this.brokerNode = queueNode.getBrokerNode();
	}

	@Override
	public String toString() {
		return "Consumers";
	}

	@Override
	protected void loadChildren() {
		try {
			Collection<SubscriptionViewFacade> list = getFacade().getTopicConsumers(queueNode.getName());
			if (list != null) {
				for (SubscriptionViewFacade mbean : list) {
					addChild(new SubscriptionNode(this, mbean));
				}
			}
		} catch (Exception e) {
			brokerNode.handleException(this, e);
		}
	}

	@Override
	public void refresh() {
		// TODO for some reason this doesn't work, so lets refresh the broker
		getBrokerNode().refresh();
	}

	public BrokerNode getBrokerNode() {
		return brokerNode;
	}

	@Override
	public Image getImage() {
		return ActiveMQJMXPlugin.getDefault().getImage("listeners.gif");
	}

	protected BrokerFacade getFacade() {
		return getBrokerNode().getFacade();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TopicConsumersNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), brokerNode, queueNode, "TopicConsumer");
	}
}

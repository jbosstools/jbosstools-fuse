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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.drop.DropHandler;
import org.fusesource.ide.foundation.ui.drop.DropHandlerFactory;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IMessage;
import org.fusesource.ide.jmx.commons.tree.MessageDropHandler;
import org.fusesource.ide.jmx.commons.tree.MessageDropTarget;
import org.jboss.tools.jmx.core.tree.Node;



public abstract class DestinationNodeSupport extends NodeSupport implements MessageDropTarget, DropHandlerFactory {

	protected Set<String> ignoreSendHeaders = new HashSet<String>(Arrays.asList("JMSDestination", "JMSMessageID"));

	private final BrokerNode brokerNode;
	private final DestinationViewMBean destination;

	public DestinationNodeSupport(Node parent, BrokerNode brokerNode, DestinationViewMBean destination) {
		super(parent);
		this.brokerNode = brokerNode;
		this.destination = destination;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return destination.getName();
	}

	@Override
	public DropHandler createDropHandler(DropTargetEvent event) {
		return new MessageDropHandler(this);
	}

	@Override
	public void dropMessage(IMessage message) {
		Map<String, Object> headers = message.getHeaders();
		Map<String, String> cleanHeaders = new HashMap<>();
		Set<Entry<String, Object>> entrySet = headers.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			Object value = entry.getValue();
			String sValue = null;
			if (value != null && !ignoreSendHeaders.contains(key)) {
				if (Objects.equal("JMSReplyTo", key)) {
					sValue = JmsTypeConverters.toDestination(value).toString();
				} else if (Objects.equal("JMSExpiration", key)) {
					sValue = JmsTypeConverters.toTimestamp(value).toString();
				} else if (Objects.equal("JMSTimestamp", key)) {
					sValue = JmsTypeConverters.toTimestamp(value).toString();
				} else if (Objects.equal("JMSDeliveryMode", key)) {
					sValue = JmsTypeConverters.toDeliveryMode(value).toString();
				} else if (Objects.equal("JMSRedelivered", key)) {
					sValue = JmsTypeConverters.toBoolean(value).toString();
				} else if (Objects.equal("JMSPriority", key)) {
					sValue = JmsTypeConverters.toInteger(value).toString();
				}

				if (sValue != null) {
					cleanHeaders.put(key, sValue);
				}
			}
		}

		String body = Strings.getOrElse(message.getBody());

		try {
			// TODO store username/pwd on a queue basis?
			String userName = getBrokerNode().getUserName();
			String password = getBrokerNode().getPassword();
			if (userName != null && password != null) {
				destination.sendTextMessage(cleanHeaders, body, userName, password);
			} else {
				destination.sendTextMessage(cleanHeaders, body);
			}
		} catch (Exception e) {
			ActiveMQJMXPlugin.showUserError("Send message to " + this + " failed", "Could not send message to " + this, e);
		}
	}

	public BrokerNode getBrokerNode() {
		return brokerNode;
	}

	public DestinationViewMBean getDestination() {
		return destination;
	}

	protected IExchange createExchange(Object object) {
		return ActiveMQJMXPlugin.getConverter().toExchange(object);
	}
}
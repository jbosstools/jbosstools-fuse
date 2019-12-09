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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.foundation.ui.tree.ConnectedNode;
import org.fusesource.ide.foundation.ui.tree.GraphableNode;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.foundation.ui.util.Shells;
import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;
import org.fusesource.ide.jmx.activemq.Messages;
import org.fusesource.ide.jmx.activemq.internal.BrokerFacade;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;


public class TopicNode extends DestinationNodeSupport  implements ImageProvider, ContextMenuProvider, GraphableNode, ConnectedNode {

	private final TopicsNode topicsNode;
	private final TopicViewMBean topic;
	private final TopicConsumersNode consumersNode;
	private final TopicProducersNode producersNode;
	private final TopicDurableConsumersNode durableConsumersNode;
		
	public TopicNode(TopicsNode topicsNode, TopicViewMBean topic) {
		super(topicsNode, topicsNode.getBrokerNode(), topic);
		this.topicsNode = topicsNode;
		this.topic = topic;
		consumersNode = new TopicConsumersNode(this);
		durableConsumersNode = new TopicDurableConsumersNode(this);
		producersNode = new TopicProducersNode(this);
		addChild(consumersNode);
		addChild(durableConsumersNode);
		addChild(producersNode);
		setPropertyBean(topic);
	}

	@Override
	public String toString() {
		return topic.getName();
	}

	public TopicsNode getTopicsNode() {
		return topicsNode;
	}

	public TopicViewMBean getTopic() {
		return topic;
	}

	public List<Node> getConsumerChildren() {
		List<Node> answer = new ArrayList<>();
		answer.addAll(Arrays.asList(consumersNode.getChildren()));
		answer.addAll(Arrays.asList(durableConsumersNode.getChildren()));
		return answer;
	}
	
	@Override
	public List<Node> getChildrenGraph() {
		List<Node> answer = getConsumerChildren();
		answer.addAll(Arrays.asList(producersNode.getChildren()));
		return answer;
	}
	
	@Override
	public List<?> getConnectedTo() {
		return getConsumerChildren();
	}

	
	@Override
	public Image getImage() {
		return ActiveMQJMXPlugin.getDefault().getImage("topic.png");
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		Action deleteTopicAction = new Action(Messages.DeleteTopicAction, SWT.CHECK) {
			@Override
			public void run() {
				showDeleteTopicDialog();
			}

		};
		deleteTopicAction.setToolTipText(Messages.DeleteTopicActionToolTip);
		deleteTopicAction.setImageDescriptor(ActiveMQJMXPlugin.getDefault().getImageDescriptor("delete.gif"));
		menu.add(deleteTopicAction);

	}

	protected void showDeleteTopicDialog() {
		String message = Messages.bind(Messages.DeleteTopicDialogMessage, topic.getName());
		boolean confirm = MessageDialog.openConfirm(Shells.getShell(), Messages.DeleteTopicDialogTitle,
				message);
		if (confirm) {
			deleteTopic();
		}
	}

	protected void deleteTopic() {
		String name = topic.getName();
		try {
			getFacade().getBrokerAdmin().removeTopic(name);
			topicsNode.refresh();
		} catch (Exception e) {
			ActiveMQJMXPlugin.showUserError("Failed to delete Topic", "Failed to delete topic: " + topic, e);
		}
	}

	protected BrokerFacade getFacade() {
		return getBrokerNode().getFacade();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TopicNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), topic);
	}
}

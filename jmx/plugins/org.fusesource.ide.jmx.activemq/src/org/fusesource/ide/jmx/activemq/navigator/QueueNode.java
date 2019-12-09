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

import javax.management.openmbean.CompositeData;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.fusesource.ide.foundation.ui.tree.ConnectedNode;
import org.fusesource.ide.foundation.ui.tree.GraphableNode;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.foundation.ui.util.Shells;
import org.fusesource.ide.jmx.activemq.ActiveMQConverter;
import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;
import org.fusesource.ide.jmx.activemq.Messages;
import org.fusesource.ide.jmx.activemq.internal.BrokerFacade;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IExchangeBrowser;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;


public class QueueNode extends DestinationNodeSupport implements IExchangeBrowser, ImageProvider, ContextMenuProvider, IPropertySourceProvider, GraphableNode, ConnectedNode {

	private final QueuesNode queuesNode;
	private final QueueViewMBean queue;
	private final QueueConsumersNode consumersNode;
	private final QueueProducersNode producersNode;
	private ActiveMQConverter converter = new ActiveMQConverter();

	public QueueNode(QueuesNode queuesNode, QueueViewMBean queue) {
		super(queuesNode, queuesNode.getBrokerNode(), queue);
		this.queuesNode = queuesNode;
		this.queue = queue;
		consumersNode = new QueueConsumersNode(this);
		addChild(consumersNode);
		producersNode = new QueueProducersNode(this);
		addChild(producersNode);
		setPropertyBean(queue);
	}

	public QueuesNode getQueuesNode() {
		return queuesNode;
	}

	public QueueViewMBean getQueue() {
		return queue;
	}

	@Override
	public List<Node> getChildrenGraph() {
		List<Node> answer = new ArrayList<>();
		answer.addAll(Arrays.asList(consumersNode.getChildren()));
		answer.addAll(Arrays.asList(producersNode.getChildren()));
		return answer;
	}

	@Override
	public List<?> getConnectedTo() {
		return getConsumersNode().getChildrenList();
	}

	public QueueConsumersNode getConsumersNode() {
		return consumersNode;
	}

	public QueueProducersNode getProducersNode() {
		return producersNode;
	}

	@Override
	public List<IExchange> browseExchanges() {
		List<IExchange> answer = new ArrayList<>();
		try {
			CompositeData[] cds = queue.browse();
			if (cds != null) {
				for (CompositeData cd : cds) {
					IExchange exchange = converter.toExchange(cd);
					if (exchange != null) {
						answer.add(exchange);
					}
				}
			}
		} catch (Exception e) {
			ActiveMQJMXPlugin.getLogger().warning("Failed to browse queue " + e, e);
		}
		return answer;
	}

	@Override
	public Image getImage() {
		return ActiveMQJMXPlugin.getDefault().getImage("queue.png");
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		Action deleteQueueAction = new Action(Messages.DeleteQueueAction, SWT.CHECK) {
			@Override
			public void run() {
				showDeleteQueueDialog();
			}

		};
		deleteQueueAction.setToolTipText(Messages.DeleteQueueActionToolTip);
		deleteQueueAction.setImageDescriptor(ActiveMQJMXPlugin.getDefault().getImageDescriptor("delete.gif"));
		menu.add(deleteQueueAction);

	}

	protected void showDeleteQueueDialog() {
		String message = Messages.bind(Messages.DeleteQueueDialogMessage, queue.getName());
		boolean confirm = MessageDialog.openConfirm(Shells.getShell(), Messages.DeleteQueueDialogTitle,
				message);
		if (confirm) {
			deleteQueue();
		}
	}

	protected void deleteQueue() {
		String name = queue.getName();
		try {
			getFacade().getBrokerAdmin().removeQueue(name);
			queuesNode.refresh();
		} catch (Exception e) {
			ActiveMQJMXPlugin.showUserError("Failed to delete Queue", "Failed to delete queue: " + queue, e);
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
		return obj instanceof QueueNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), queue);
	}
}

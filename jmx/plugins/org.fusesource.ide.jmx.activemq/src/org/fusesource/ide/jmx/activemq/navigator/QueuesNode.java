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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.foundation.ui.util.Shells;
import org.fusesource.ide.jmx.activemq.ActiveMQJMXPlugin;
import org.fusesource.ide.jmx.activemq.Messages;
import org.fusesource.ide.jmx.activemq.internal.BrokerFacade;
import org.fusesource.ide.jmx.activemq.internal.QueueViewFacade;
import org.jboss.tools.jmx.ui.ImageProvider;


public class QueuesNode extends RefreshableCollectionNode implements ImageProvider, ContextMenuProvider {

	private final BrokerNode brokerNode;
	private final BrokerFacade facade;

	public QueuesNode(BrokerNode brokerNode) {
		super(brokerNode);
		this.brokerNode = brokerNode;
		this.facade = brokerNode.getFacade();
	}

	@Override
	public String toString() {
		return "Queues";
	}

	@Override
	protected void loadChildren() {
		try {
			Collection<QueueViewFacade> queues = facade.getQueues();
			if (queues != null) {
				for (QueueViewFacade queue : queues) {
					addChild(new QueueNode(this, queue));
				}
			}
		} catch (Exception e) {
			brokerNode.handleException(this, e);
		}
	}

	public BrokerNode getBrokerNode() {
		return brokerNode;
	}

	@Override
	public Image getImage() {
		return ActiveMQJMXPlugin.getDefault().getImage("queue_folder.png");
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		Action createQueueAction = new Action(Messages.CreateQueueAction, SWT.CHECK) {
			@Override
			public void run() {
				showCreateQueueDialog();
			}

		};
		createQueueAction.setToolTipText(Messages.CreateQueueActionToolTip);
		createQueueAction.setImageDescriptor(ActiveMQJMXPlugin.getDefault().getImageDescriptor("new_queue.png"));
		menu.add(createQueueAction);

	}

	protected void showCreateQueueDialog() {
		InputDialog dialog = new InputDialog(Shells.getShell(), Messages.CreateQueueDialogTitle,
				Messages.CreateQueueDialogMessage, "", null);
		int result = dialog.open();
		if (result == Window.OK) {
			String queue = dialog.getValue();
			createQueue(queue);
		}
	}

	protected void createQueue(String queue) {
		if (!Strings.isBlank(queue)) {
			try {
				getFacade().getBrokerAdmin().addQueue(queue);
				refresh();
			} catch (Exception e) {
				ActiveMQJMXPlugin.showUserError("Failed to create Queue", "Failed to create queue: " + queue, e);
			}
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
		return obj instanceof QueuesNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), brokerNode, "Queues");
	}
}

package org.fusesource.ide.fabric.activemq.navigator;

import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

import org.fusesource.fabric.activemq.facade.BrokerFacade;
import org.fusesource.fabric.activemq.facade.QueueViewFacade;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.activemq.Messages;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;


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
		return FabricPlugin.getDefault().getImage("queue_folder.png");
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
		createQueueAction.setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("new_queue.png"));
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
				FabricPlugin.showUserError("Failed to create Queue", "Failed to create queue: " + queue, e);
			}
		}
	}

	protected BrokerFacade getFacade() {
		return getBrokerNode().getFacade();
	}

}

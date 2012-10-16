package org.fusesource.ide.fabric.activemq.navigator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.InvalidSelectorException;

import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.fusesource.fabric.activemq.facade.BrokerFacade;
import org.fusesource.fon.util.messages.IExchange;
import org.fusesource.fon.util.messages.IExchangeBrowser;
import org.fusesource.ide.commons.tree.ConnectedNode;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.activemq.FabricActiveMQPlugin;
import org.fusesource.ide.fabric.activemq.Messages;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;


public class QueueNode extends DestinationNodeSupport implements IExchangeBrowser, ImageProvider, ContextMenuProvider, IPropertySourceProvider, GraphableNode, ConnectedNode {

	private final QueuesNode queuesNode;
	private final QueueViewMBean queue;
	private final QueueConsumersNode consumersNode;
	private final QueueProducersNode producersNode;
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
	
	public List<Node> getChildrenGraph() {
		List<Node> answer = new ArrayList<Node>();
		answer.addAll(Arrays.asList(consumersNode.getChildren()));
		answer.addAll(Arrays.asList(producersNode.getChildren()));
		return answer;
	}
	
	public List<?> getConnectedTo() {
		return getConsumersNode().getChildrenList();
	}

	public QueueConsumersNode getConsumersNode() {
		return consumersNode;
	}

	public QueueProducersNode getProducersNode() {
		return producersNode;
	}

	public List<IExchange> browseExchanges() {
		List<IExchange> answer = new ArrayList<IExchange>();
		try {
			List<?> messages = queue.browseMessages();
			for (Object object : messages) {
				IExchange exchange = createExchange(object);
				if (exchange != null) {
					answer.add(exchange);
				}
			}
		} catch (InvalidSelectorException e) {
			FabricActiveMQPlugin.getLogger().warning("Failed to browse queue " + e, e);
		}
		return answer;
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("queue.png");
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		Action deleteQueueAction = new Action(Messages.DeleteQueueAction, SWT.CHECK) {
			public void run() {
				showDeleteQueueDialog();
			}

		};
		deleteQueueAction.setToolTipText(Messages.DeleteQueueActionToolTip);
		deleteQueueAction.setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("delete.gif"));
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
			FabricPlugin.showUserError("Failed to delete Queue", "Failed to delete queue: " + queue, e);
		}
	}

	protected BrokerFacade getFacade() {
		return getBrokerNode().getFacade();
	}

}

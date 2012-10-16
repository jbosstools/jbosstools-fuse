package org.fusesource.ide.fabric.activemq.navigator;

import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.fusesource.fabric.activemq.facade.BrokerFacade;
import org.fusesource.fabric.activemq.facade.ProducerViewFacade;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricPlugin;


public class QueueProducersNode extends RefreshableCollectionNode implements ImageProvider {

	private final BrokerNode brokerNode;
	private final BrokerFacade facade;
	private final QueueNode queueNode;

	public QueueProducersNode(QueueNode queueNode) {
		super(queueNode);
		this.queueNode = queueNode;
		this.brokerNode = queueNode.getBrokerNode();
		this.facade = queueNode.getFacade();
	}

	@Override
	public String toString() {
		return "Producers";
	}

	@Override
	protected void loadChildren() {
		try {
			String name = queueNode.getName();
			Collection<ProducerViewFacade> list = getFacade().getQueueProducers(name);
			if (list != null) {
				for (ProducerViewFacade mbean : list) {
					addChild(new ProducerNode(this, queueNode, mbean));
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
		return FabricPlugin.getDefault().getImage("jms/listeners.gif");
	}

	protected BrokerFacade getFacade() {
		return getBrokerNode().getFacade();
	}

}

package org.fusesource.ide.fabric.activemq.navigator;

import java.util.Collections;
import java.util.List;

import org.apache.activemq.broker.jmx.ProducerViewMBean;
import org.fusesource.ide.commons.tree.ConnectedNode;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.NodeSupport;




public class ProducerNode extends NodeSupport implements ConnectedNode {
	private final ProducerViewMBean mbean;
	private final Node destinationNode;

	public ProducerNode(Node parent, Node destinationNode, ProducerViewMBean mbean) {
		super(parent);
		this.destinationNode = destinationNode;
		this.mbean = mbean;
		setPropertyBean(mbean);
	}

	public Node getDestinationNode() {
		return destinationNode;
	}

	@Override
	public String toString() {
		try {
			return mbean.getClientId() + "/" + mbean.getSessionId();
		} catch (Exception ex) {
			getRefreshableUI().fireRefresh();
			return "";
		}
	}

	@Override
	public List<Node> getConnectedTo() {
		return Collections.singletonList(getDestinationNode());
	}
}
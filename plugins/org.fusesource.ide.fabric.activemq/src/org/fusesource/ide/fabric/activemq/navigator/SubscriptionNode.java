package org.fusesource.ide.fabric.activemq.navigator;

import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.NodeSupport;




public class SubscriptionNode extends NodeSupport {
	private final SubscriptionViewMBean mbean;

	public SubscriptionNode(Node parent, SubscriptionViewMBean mbean) {
		super(parent);
		this.mbean = mbean;
		setPropertyBean(mbean);
	}

	@Override
	public String toString() {
		return mbean.getClientId() + "/" + mbean.getSessionId();
	}
}

package org.fusesource.ide.fabric.activemq.navigator;

import org.apache.activemq.broker.jmx.ConnectionViewMBean;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricPlugin;




public class ConnectionNode extends NodeSupport implements ImageProvider {

	private final ConnectionViewMBean mbean;

	public ConnectionNode(Node parent, ConnectionViewMBean mbean) {
		super(parent);
		this.mbean = mbean;
		setPropertyBean(mbean);
	}

	public ConnectionViewMBean getMbean() {
		return mbean;
	}

	@Override
	public String toString() {
		return mbean.getRemoteAddress();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("connection.png");
	}
}

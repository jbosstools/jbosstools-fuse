package org.fusesource.ide.fabric.servicemix.navigator;


import org.apache.servicemix.nmr.management.ManagedEndpointMBean;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricPlugin;


public class EndpointNode extends NodeSupport implements ImageProvider {
	private final ManagedEndpointMBean mbean;

	public EndpointNode(EndpointsNode endpointsNode, ManagedEndpointMBean mbean) {
		super(endpointsNode);
		this.mbean = mbean;
		setPropertyBean(mbean);
	}

	@Override
	public String toString() {
		return mbean.getName();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("endpoint_node.png");
	}

}

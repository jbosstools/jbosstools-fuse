package org.fusesource.ide.fabric.servicemix.navigator;

import org.eclipse.swt.graphics.Image;
import org.fusesource.fabric.servicemix.facade.ServiceMixFacade;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricPlugin;



public class ServiceMixNode extends NodeSupport implements ImageProvider {

	private final ServiceMixFacade facade;

	public ServiceMixNode(Node parent, ServiceMixFacade facade) {
		super(parent);
		this.facade = facade;
		addChild(new EndpointsNode(this));
	}

	@Override
	public String toString() {
		return "ServiceMix";
	}

	public ServiceMixFacade getFacade() {
		return facade;
	}

	@Override
	public Image getImage() {
		// TODO replace with better ESB icon!!
		return FabricPlugin.getDefault().getImage("smx_server.png");
	}
	
}

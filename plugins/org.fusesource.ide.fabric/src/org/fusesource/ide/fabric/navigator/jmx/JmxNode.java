package org.fusesource.ide.fabric.navigator.jmx;

import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.RefreshableNode;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.ContainerNode;



public class JmxNode extends RefreshableNode implements ImageProvider {
	private final ContainerNode agentNode;

	public JmxNode(ContainerNode agentNode) {
		super(agentNode);
		this.agentNode = agentNode;
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("releng_gears.gif");
	}


	@Override
	protected void loadChildren() {
		FabricConnectionWrapper wrapper = new FabricConnectionWrapper(agentNode);
		wrapper.loadChildren(this);
	}

}
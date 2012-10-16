package org.fusesource.ide.fabric.navigator;

import org.fusesource.ide.commons.tree.NodeSupport;

public class JmxNode extends NodeSupport {

	private final ContainerNode agentNode;

	public JmxNode(ContainerNode agentNode) {
		super(agentNode);
		this.agentNode = agentNode;
	}


	@Override
	public String toString() {
		return "JMX " + agentNode.getContainer().getJmxUrl();
	}
}

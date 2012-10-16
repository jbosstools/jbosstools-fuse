package org.fusesource.ide.fabric.camel.navigator;

import org.fusesource.ide.commons.tree.NodeSupport;

public class TracerNode extends NodeSupport {
	private final CamelContextNode camelContextNode;

	public TracerNode(CamelContextNode camelContextNode) {
		super(camelContextNode);
		this.camelContextNode = camelContextNode;	// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Tracer";
	}
	
}

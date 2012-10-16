package org.fusesource.ide.fabric.camel.navigator;

import org.fusesource.ide.commons.tree.RefreshableCollectionNode;

public class ComponentsNode extends RefreshableCollectionNode {
	private final CamelContextNode camelContextNode;

	public ComponentsNode(CamelContextNode camelContextNode) {
		super(camelContextNode);
		this.camelContextNode = camelContextNode;
	}
	@Override
	public String toString() {
		return "Components";
	}
	
	@Override
	protected void loadChildren() {
		// TODO Auto-generated method stub
		
	}

	
}

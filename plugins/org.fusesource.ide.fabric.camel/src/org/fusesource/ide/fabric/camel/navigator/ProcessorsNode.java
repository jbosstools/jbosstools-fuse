package org.fusesource.ide.fabric.camel.navigator;

import org.fusesource.ide.commons.tree.RefreshableCollectionNode;

public class ProcessorsNode extends RefreshableCollectionNode {
	private final CamelContextNode camelContextNode;

	public ProcessorsNode(CamelContextNode camelContextNode) {
		super(camelContextNode);
		this.camelContextNode = camelContextNode;
	}

	@Override
	public String toString() {
		return "Processors";
	}

	@Override
	protected void loadChildren() {
		// TODO Auto-generated method stub
		
	}


}

package org.fusesource.ide.fabric.navigator.cloud;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


public class NodesTabDescriptor extends PageTabDescriptor {
	private final CloudNode node;

	public NodesTabDescriptor(String label, CloudNode node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new NodeTable(node);
	}
}
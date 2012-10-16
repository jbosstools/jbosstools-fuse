package org.fusesource.ide.fabric.camel.navigator;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


public class ProcessorCallViewTabDescriptor extends PageTabDescriptor {
	private final Node node;

	ProcessorCallViewTabDescriptor(String label, Node node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new ProcessorCallView(node);
	}
}
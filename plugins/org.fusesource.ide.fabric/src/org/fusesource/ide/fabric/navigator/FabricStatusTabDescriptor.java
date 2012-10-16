package org.fusesource.ide.fabric.navigator;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


public class FabricStatusTabDescriptor extends PageTabDescriptor {
	private final Fabric node;

	public FabricStatusTabDescriptor(String label, Fabric node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new FabricStatusTableSheetPage(node);
	}
}
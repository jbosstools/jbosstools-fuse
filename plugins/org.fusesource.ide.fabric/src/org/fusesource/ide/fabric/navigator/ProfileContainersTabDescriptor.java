package org.fusesource.ide.fabric.navigator;

import org.eclipse.ui.part.IPage;
import org.fusesource.ide.commons.ui.views.PageTabDescriptor;


public class ProfileContainersTabDescriptor extends PageTabDescriptor {
	private final ProfileNode node;

	public ProfileContainersTabDescriptor(String label, ProfileNode node) {
		super(label);
		this.node = node;
	}

	@Override
	protected IPage createPage() {
		return new ProfileContainerTableSheetPage(node);
	}
}
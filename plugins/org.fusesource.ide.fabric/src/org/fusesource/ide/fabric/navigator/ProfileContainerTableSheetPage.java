package org.fusesource.ide.fabric.navigator;

import org.fusesource.ide.fabric.actions.CreateChildContainerAction;
import org.fusesource.ide.fabric.actions.CreateSshContainerAction;
import org.fusesource.ide.fabric.actions.jclouds.CreateJCloudsContainerAction;

public class ProfileContainerTableSheetPage extends ContainerTableSheetPage {
	private final ProfileNode node;

	public ProfileContainerTableSheetPage(ProfileNode node) {
		super(node.getFabric());
		this.node = node;

		setCreateChildContainerAction(new CreateChildContainerAction(node));
		setCreateSshContainerAction(new CreateSshContainerAction(node.getVersionNode(), null, node));
		setCreateCloudContainerAction(new CreateJCloudsContainerAction(node.getVersionNode(), null, node));

		updateData();
	}

	@Override
	public void refresh() {
		updateData();
		super.refresh();
	}

	@Override
	protected void updateData() {
		setPropertySources(node.getContainerPropertySourceList());
	}
}

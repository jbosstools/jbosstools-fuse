package org.fusesource.ide.fabric.navigator;

import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.fusesource.ide.commons.ui.views.PropertiesPageTabDescriptor;
import org.fusesource.ide.commons.ui.views.TabFolderSupport2;


public class ProfileTabViewPage extends TabFolderSupport2 {
	private final ProfileNode node;

	public ProfileTabViewPage(ProfileNode node) {
		super(node.getClass().getName(), true);
		this.node = node;
	}

	@Override
	protected ITabDescriptor[] getTabDescriptors() {
		return new ITabDescriptor[] {
				new ProfileDetailsFormTabDescriptor("Details", node),
				new ProfileContainersTabDescriptor("Containers", node),
				new PropertiesPageTabDescriptor(node),
				new ProfileRequirementsFormTabDescriptor("Requirements", node)
		};
	}
}

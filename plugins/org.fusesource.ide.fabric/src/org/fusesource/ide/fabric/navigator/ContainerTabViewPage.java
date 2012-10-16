package org.fusesource.ide.fabric.navigator;

import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.fusesource.ide.commons.ui.views.PropertiesPageTabDescriptor;
import org.fusesource.ide.commons.ui.views.TabFolderSupport2;


public class ContainerTabViewPage extends TabFolderSupport2 {
	private final ContainerNode node;

	public ContainerTabViewPage(ContainerNode node) {
		super(node.getClass().getName(), true);
		this.node = node;
	}

	@Override
	protected ITabDescriptor[] getTabDescriptors() {
		return new ITabDescriptor[] {
				new PropertiesPageTabDescriptor(node),
				new ProfileTreeTabDescriptor("Profiles", node),
				new ProfilesTabDescriptor("Profile Details", node)
		};
	}


}

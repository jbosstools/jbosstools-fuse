package org.fusesource.ide.fabric.navigator.cloud;

import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.fusesource.ide.commons.ui.views.PropertiesPageTabDescriptor;
import org.fusesource.ide.commons.ui.views.TabFolderSupport2;


public class CloudTabViewPage extends TabFolderSupport2 {
	private final CloudNode node;

	public CloudTabViewPage(CloudNode node) {
		super(node.getClass().getName(), true);
		this.node = node;
	}

	@Override
	protected ITabDescriptor[] getTabDescriptors() {
		return new ITabDescriptor[] {
				new PropertiesPageTabDescriptor(node),
				new NodesTabDescriptor("Nodes", node)
		};
	}


}

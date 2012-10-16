package org.fusesource.ide.fabric.navigator;

import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.fusesource.ide.commons.ui.views.PropertiesPageTabDescriptor;
import org.fusesource.ide.commons.ui.views.TabFolderSupport2;


public class FabricTabViewPage extends TabFolderSupport2 {
	private final Fabric node;

	public FabricTabViewPage(Fabric node) {
		super(node.getClass().getName(), true);
		this.node = node;
	}

	@Override
	protected ITabDescriptor[] getTabDescriptors() {
		return new ITabDescriptor[] {
				new FabricStatusTabDescriptor("Status", node),
				new PropertiesPageTabDescriptor(node)
		};
	}


}

package org.fusesource.ide.fabric.camel.navigator;

import java.util.List;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.fusesource.ide.commons.ui.views.PropertiesPageTabDescriptor;
import org.fusesource.ide.commons.ui.views.TabFolderSupport2;


public class ProcessorTabViewPage extends TabFolderSupport2 {
	private final ProcessorNodeSupport node;

	public ProcessorTabViewPage(ProcessorNodeSupport node) {
		super(node.getClass().getName(), true);
		this.node = node;
	}


	@Override
	protected ITabDescriptor[] getTabDescriptors() {
		final List<IPropertySource> propertySourceList = node.getAllProcessorsPropertySourceList();
		return new ITabDescriptor[] {
				new PropertiesPageTabDescriptor(node),
				new ProcessorsPageTabDescriptor("Processors", propertySourceList),
				new ProcessorCallViewTabDescriptor("Profile", node),
		};
	}


}

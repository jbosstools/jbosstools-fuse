package org.fusesource.ide.commons.ui.views;

import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;

public class PropertiesPageTabDescriptor extends PageTabDescriptor {
	private final IPropertySourceProvider propertySourceProvider;

	public PropertiesPageTabDescriptor(IPropertySourceProvider propertySourceProvider) {
		this("Properties", propertySourceProvider);
	}

	public PropertiesPageTabDescriptor(String label, IPropertySourceProvider propertySourceProvider) {
		super(label);
		this.propertySourceProvider = propertySourceProvider;
	}

	@Override
	protected IPage createPage() {
		PropertySheetPage propertySheet = new PropertySheetPage();
		propertySheet.setPropertySourceProvider(propertySourceProvider);
		return propertySheet;
	}
}
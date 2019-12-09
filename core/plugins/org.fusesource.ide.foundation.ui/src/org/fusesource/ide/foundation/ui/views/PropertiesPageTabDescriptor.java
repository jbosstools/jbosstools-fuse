/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.views;

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
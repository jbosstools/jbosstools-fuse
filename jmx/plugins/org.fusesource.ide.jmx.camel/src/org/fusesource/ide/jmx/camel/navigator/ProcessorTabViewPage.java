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

package org.fusesource.ide.jmx.camel.navigator;

import java.util.List;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.tabbed.ITabDescriptor;
import org.fusesource.ide.foundation.ui.views.PropertiesPageTabDescriptor;
import org.fusesource.ide.foundation.ui.views.TabFolderSupport2;


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

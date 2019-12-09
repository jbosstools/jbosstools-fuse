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

import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelProcessorMBean;
import org.fusesource.ide.foundation.ui.propsrc.PropertySourceTableView;
import org.fusesource.ide.foundation.ui.views.PageTabDescriptor;


public class ProcessorsPageTabDescriptor extends PageTabDescriptor {
	private final List<IPropertySource> propertySourceList;

	public ProcessorsPageTabDescriptor(String label, List<IPropertySource> propertySourceList) {
		super(label);
		this.propertySourceList = propertySourceList;
	}

	@Override
	protected IPage createPage() {
		PropertySourceTableView processorsTableView = new PropertySourceTableView(CamelProcessorMBean.class.getName());
		processorsTableView.setPropertySources(propertySourceList);
		return processorsTableView;
	}
}
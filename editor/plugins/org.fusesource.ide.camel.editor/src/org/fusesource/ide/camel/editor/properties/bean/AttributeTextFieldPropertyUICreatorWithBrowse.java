/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.bean;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.TextParameterPropertyUICreator;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author brianf
 *
 */
public class AttributeTextFieldPropertyUICreatorWithBrowse extends TextParameterPropertyUICreator {

		public AttributeTextFieldPropertyUICreatorWithBrowse(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip,
				AbstractCamelModelElement camelModelElement, Parameter parameter, Composite parent,
				TabbedPropertySheetWidgetFactory widgetFactory) {
			super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory);
		}
    	
		@Override
		protected GridData createPropertyFieldLayoutData() {
			return GridDataFactory.fillDefaults().indent(5, 0).span(getColumnSpan(), 1).grab(true, false).create();
		}
}
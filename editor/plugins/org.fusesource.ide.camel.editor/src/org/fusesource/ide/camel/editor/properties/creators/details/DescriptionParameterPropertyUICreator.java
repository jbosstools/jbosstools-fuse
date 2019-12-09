/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.properties.creators.details;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.AbstractTextFieldParameterPropertyUICreator;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public class DescriptionParameterPropertyUICreator extends AbstractTextFieldParameterPropertyUICreator {

	public DescriptionParameterPropertyUICreator(DataBindingContext dbc, IObservableMap modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent,
			TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				camelModelElement.setDescription(((Text) e.getSource()).getText());
			}
		});
	}

	@Override
	public String getInitialValue() {
		String description = camelModelElement.getDescription();
		if (description == null) {
			description = eip.getParameter(parameter.getName()).getDefaultValue();
		}
		return description;
	}

}

/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.properties.creators.advanced;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.AbstractNumberParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.number.NumberModifyListenerForAdvanced;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

/**
 * @author Aurelien Pupier
 *
 */
public class NumberParameterPropertyUICreatorForAdvanced extends AbstractNumberParameterPropertyUICreator {

	public NumberParameterPropertyUICreatorForAdvanced(DataBindingContext dbc, IObservableMap<?,?> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, new NumberModifyListenerForAdvanced(camelModelElement, parameter, modelMap));
	}

	@Override
	public String getInitialValue() {
		return (String) PropertiesUtils.getTypedPropertyFromUri(camelModelElement, parameter, component);
	}

}

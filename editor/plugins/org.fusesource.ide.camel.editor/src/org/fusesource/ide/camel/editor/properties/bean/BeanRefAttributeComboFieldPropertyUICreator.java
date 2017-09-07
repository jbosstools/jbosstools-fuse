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

import java.util.ArrayList;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.ComboParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.ComboParameterPropertyModifyListener;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;

/**
 * @author brianf
 *
 */
public class BeanRefAttributeComboFieldPropertyUICreator extends ComboParameterPropertyUICreator {
	
	public BeanRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, 
				new ComboParameterPropertyModifyListener(camelModelElement, parameter.getName()));
		setValues();
	}

	public BeanRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ComboParameterPropertyModifyListener listener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener);
		setValues();
	}
	
	public BeanRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ComboParameterPropertyModifyListener listener, IValidator extraValidator) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener, extraValidator);
		setValues();
	}

	private void setValues() {
        String[] beanRefs = CamelComponentUtils.getRefs(this.camelModelElement.getCamelFile());
        String[] cleanedBeanRefs = removeStringFromStringArray(beanRefs, this.camelModelElement.getId());
        super.setValues(cleanedBeanRefs);
	}

	private String[] removeStringFromStringArray(String[] input, String deleteMe) {
	    ArrayList<String> result = new ArrayList<>();
	    for(String item : input)
	        if(!deleteMe.equals(item)) {
	            result.add(item);
	        }
	    return result.toArray(new String[0]);
	}
}
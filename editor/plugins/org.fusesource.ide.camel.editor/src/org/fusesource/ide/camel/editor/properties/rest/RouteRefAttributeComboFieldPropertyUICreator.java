/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.rest;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.ComboParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.ComboParameterPropertyModifyListener;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigUtil;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author brianf
 *
 */
public class RouteRefAttributeComboFieldPropertyUICreator extends ComboParameterPropertyUICreator {
	
	private RestConfigUtil util = new RestConfigUtil();

	public RouteRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, 
				new ComboParameterPropertyModifyListener(camelModelElement, parameter.getName()));
		setValues();
	}

	public RouteRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ComboParameterPropertyModifyListener listener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener);
		setValues();
	}

	public RouteRefAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ComboParameterPropertyModifyListener listener, IValidator extraValidator) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener, extraValidator);
		setValues();
	}

	private void setValues() {
		String[] routeRefs = util.getRoutes(this.camelModelElement.getCamelFile(), ""); //$NON-NLS-1$
		super.setValues(routeRefs);
	}

	@Override
	public String getInitialValue() {
		final String parameterName = parameter.getName();
		final Object parameterValue = camelModelElement.getParameter(parameterName);
		final Parameter param = eip.getParameter(parameterName);
		String defaultValue = ""; //$NON-NLS-1$
		if (param != null && param.getDefaultValue() != null) {
			defaultValue = param.getDefaultValue();
		}
		return findInitialValue(parameterValue, defaultValue);
	}
}

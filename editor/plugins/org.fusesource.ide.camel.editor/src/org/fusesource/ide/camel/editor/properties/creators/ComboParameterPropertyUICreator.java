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
package org.fusesource.ide.camel.editor.properties.creators;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.bean.AttributeComboParameterPropertyModifyListenerForAdvanced;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.AbstractComboParameterPropertyModifyListener;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author brianf
 *
 */
public class ComboParameterPropertyUICreator extends AbstractComboFieldParameterPropertyUICreator {

	public ComboParameterPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, 
				new AttributeComboParameterPropertyModifyListenerForAdvanced(camelModelElement, parameter));
	}

	public ComboParameterPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, AbstractComboParameterPropertyModifyListener listener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener);
	}

	protected String findInitialValue(Object value, String defaultValue) {
		if (value != null && value instanceof String) {
			return (String) value;
		} else if (defaultValue != null) {
			return defaultValue;
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public String getInitialValue() {
		final String parameterName = parameter.getName();
		final Object parameterValue = camelModelElement.getParameter(parameterName);
		final Parameter param = eip.getParameter(parameterName);
		String defaultValue = null;
		if (param != null && param.getDefaultValue() != null) {
			defaultValue = param.getDefaultValue();
		}
		return findInitialValue(parameterValue, defaultValue);
	}

	/* (non-Javadoc)
	 * Made visible for testing
	 * @see org.fusesource.ide.camel.editor.properties.creators.AbstractComboFieldParameterPropertyUICreator#setValues(java.lang.String[])
	 */
	public void setValues(String[] values) {
		super.setValues(values); 
	}
}

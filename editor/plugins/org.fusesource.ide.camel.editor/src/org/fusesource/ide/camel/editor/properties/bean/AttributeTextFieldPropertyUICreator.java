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
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.properties.creators.AbstractTextFieldParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.AbstractTextParameterPropertyModifyListener;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.w3c.dom.Node;

/**
 * @author brianf
 *
 */
public class AttributeTextFieldPropertyUICreator extends AbstractTextFieldParameterPropertyUICreator {

	public AttributeTextFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, 
				new AttributeTextParameterPropertyModifyListenerForAdvanced(camelModelElement, parameter));
	}

	public AttributeTextFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, AbstractTextParameterPropertyModifyListener listener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener);
	}

	private String findInitialValue(Object value, String defaultValue) {
		if (value != null && value instanceof String) {
			return (String) value;
		} else if (defaultValue != null) {
			return defaultValue;
		}
		return "";
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
		if (camelModelElement instanceof CamelBean && parameterValue == null) {
			final Object attrValue = getAttributeValue(parameterName);
			return findInitialValue(attrValue, defaultValue);
		}
		return findInitialValue(parameterValue, defaultValue);
	}

	protected Object getAttributeValue(String attrName) {
		Node camelNode = camelModelElement.getXmlNode();
		if (camelNode != null && camelNode.hasAttributes()) {
			Node attrNode = camelNode.getAttributes().getNamedItem(attrName);
			if (attrNode != null) {
				return attrNode.getNodeValue();
			}
		}
		return null;
	}
	
	@Override
	protected void init(Composite parent) {
		final Text txtField = getWidgetFactory().createText(parent, getInitialValue(), createTextStyle());
		txtField.addModifyListener(modifyListener);
		txtField.setLayoutData(createPropertyFieldLayoutData());
		setControl(txtField);

		setUiObservable(WidgetProperties.text(SWT.Modify).observeDelayed(500, txtField));
		setValidator(createValidator());
	}
}
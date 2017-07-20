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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.properties.creators.ComboParameterPropertyUICreator;
import org.fusesource.ide.camel.editor.properties.creators.modifylisteners.text.ComboParameterPropertyModifyListener;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.w3c.dom.Node;

/**
 * @author brianf
 *
 */
public class ScopeAttributeComboFieldPropertyUICreator extends ComboParameterPropertyUICreator {
	
	private static final String SINGLETON_SCOPE = "singleton"; //$NON-NLS-1$
	private static final String PROTOTYPE_SCOPE = "prototype";  //$NON-NLS-1$
	private static final String DEFAULT_SCOPE = SINGLETON_SCOPE;
	private static final String[] blueprintScope = new String[] {SINGLETON_SCOPE, PROTOTYPE_SCOPE};
	private static final String[] springScope = new String[] {SINGLETON_SCOPE, PROTOTYPE_SCOPE};
	
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();

	public ScopeAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, 
				new ComboParameterPropertyModifyListener(camelModelElement, parameter.getName()));
		if (beanConfigUtil.isBlueprintConfig(camelModelElement.getXmlNode())) {
			setValues(blueprintScope);
		} else {
			setValues(springScope);
		}
	}

	public ScopeAttributeComboFieldPropertyUICreator(DataBindingContext dbc, IObservableMap<String, String> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ComboParameterPropertyModifyListener listener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory, listener);
		if (beanConfigUtil.isBlueprintConfig(camelModelElement.getXmlNode())) {
			setValues(blueprintScope);
		} else {
			setValues(springScope);
		}
	}

	@Override
	public String getInitialValue() {
		final String parameterName = parameter.getName();
		final Object parameterValue = camelModelElement.getParameter(parameterName);
		final Parameter param = eip.getParameter(parameterName);
		String defaultValue = DEFAULT_SCOPE;
		if (param != null && param.getDefaultValue() != null) {
			defaultValue = param.getDefaultValue();
		}
		if (camelModelElement instanceof CamelBean && parameterValue == null) {
			final Object attrValue = getAttributeValue(parameterName);
			return findInitialValue(attrValue, defaultValue);
		}
		return findInitialValue(parameterValue, defaultValue);
	}

	private Object getAttributeValue(String attrName) {
		Node camelNode = camelModelElement.getXmlNode();
		if (camelNode != null && camelNode.hasAttributes()) {
			Node attrNode = camelNode.getAttributes().getNamedItem(attrName);
			if (attrNode != null) {
				return attrNode.getNodeValue();
			}
		}
		return null;
	}
}
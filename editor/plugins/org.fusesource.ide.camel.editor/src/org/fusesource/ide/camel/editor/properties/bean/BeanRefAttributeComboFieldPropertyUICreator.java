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
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
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

	protected BeanConfigUtil beanConfigUtil = new BeanConfigUtil();

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
		String[] classRefs = beanConfigUtil.removeRefsWithNoClassFromArray(beanRefs, this.camelModelElement);
		String[] cleanedBeanRefs = beanConfigUtil.removeStringFromStringArray(classRefs, this.camelModelElement.getId());
		super.setValues(cleanedBeanRefs);
	}
}

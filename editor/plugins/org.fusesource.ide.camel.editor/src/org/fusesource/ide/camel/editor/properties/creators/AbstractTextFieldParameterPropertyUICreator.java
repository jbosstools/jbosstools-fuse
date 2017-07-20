/*******************************************************************************
 * Copyright (c) 2016-2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.properties.creators;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.validation.BeanValidationUtil;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author Aurelien Pupier
 *
 */
public abstract class AbstractTextFieldParameterPropertyUICreator extends AbstractParameterPropertyUICreator {

	protected ModifyListener modifyListener;

	public AbstractTextFieldParameterPropertyUICreator(DataBindingContext dbc, IObservableMap<?, ?> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ModifyListener modifyListener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory);
		this.modifyListener = modifyListener;
	}

	@Override
	protected void init(Composite parent) {
		final Text txtField = getWidgetFactory().createText(parent, getInitialValue(), createTextStyle());
		txtField.addModifyListener(modifyListener);
		txtField.setLayoutData(createPropertyFieldLayoutData());
		setControl(txtField);

		setUiObservable(WidgetProperties.text(SWT.Modify).observeDelayed(300, txtField));
		setValidator(createValidator());
	}

	protected int createTextStyle() {
		return SWT.SINGLE | SWT.BORDER | SWT.LEFT;
	}

	/**
	 * @return A Validator checking non-emptiness of mandatory field
	 */
	protected IValidator createValidator() {
		return value -> BeanValidationUtil.validateRequiredParameter(parameter, value);
	}

	@Override
	public String getInitialValue() {
		final String parameterName = parameter.getName();
		final Object parameterValue = camelModelElement.getParameter(parameterName);
		final Object valueFromEIP = eip.getParameter(parameterName) != null ? eip.getParameter(parameterName).getDefaultValue() : "";
		return (String) (parameterValue != null ? parameterValue : valueFromEIP);
	}

	@Override
	public Text getControl() {
		return (Text) super.getControl();
	}

}

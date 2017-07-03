/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
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
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

/**
 * @author brianf
 *
 */
public abstract class AbstractComboFieldParameterPropertyUICreator extends AbstractParameterPropertyUICreator {

	protected ModifyListener modifyListener;
	private String[] values;

	public AbstractComboFieldParameterPropertyUICreator(DataBindingContext dbc, IObservableMap<?, ?> modelMap, Eip eip, AbstractCamelModelElement camelModelElement, Parameter parameter,
			Composite parent, TabbedPropertySheetWidgetFactory widgetFactory, ModifyListener modifyListener) {
		super(dbc, modelMap, eip, camelModelElement, parameter, parent, widgetFactory);
		this.modifyListener = modifyListener;
	}

	@Override
	protected void init(Composite parent) {
		final Combo comboField = new Combo(parent, createComboStyle());
		for (int i = 0; i < values.length; i++) {
			comboField.add(values[i]);
		}
		comboField.addModifyListener(modifyListener);
		comboField.setLayoutData(createPropertyFieldLayoutData());
		setControl(comboField);

		setUiObservable(WidgetProperties.selection().observe(comboField));
		setValidator(createValidator());
	}

	protected int createComboStyle() {
		return SWT.BORDER | SWT.READ_ONLY;
	}

	/**
	 * @return A Validator checking non-emptiness of mandatory field
	 */
	protected IValidator createValidator() {
		return value -> {
			if (PropertiesUtils.isRequired(parameter) && (value == null || value.toString().trim().length() < 1)) {
				return ValidationStatus.error("Parameter " + parameter.getName() + " is a mandatory field and cannot be empty.");
			}
			return ValidationStatus.ok();
		};
	}
	
	protected void setValues(String[] values) {
		this.values = values; 
	}

	@Override
	public String getInitialValue() {
		final String parameterName = parameter.getName();
		final Object parameterValue = camelModelElement.getParameter(parameterName);
		final Object valueFromEIP = eip.getParameter(parameterName) != null ? eip.getParameter(parameterName).getDefaultValue() : "";
		return (String) (parameterValue != null ? parameterValue : valueFromEIP);
	}

	@Override
	public Combo getControl() {
		return (Combo) super.getControl();
	}

}

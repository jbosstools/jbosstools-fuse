/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.ArgumentXMLStyleChildTableControl;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.PropertyXMLStyleChildTableControl;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.w3c.dom.Element;

/**
 * @author brianf
 *
 */
public class GlobalBeanEditWizardPage extends GlobalBeanBaseWizardPage {

	private Element selectedElement;
	private ArgumentXMLStyleChildTableControl beanArgsTable;
	private PropertyXMLStyleChildTableControl beanPropsTable;

	/**
	 * @param pageName
	 */
	public GlobalBeanEditWizardPage(DataBindingContext dbc, String title, String description, AbstractCamelModelElement parent) {
		super(UIMessages.globalBeanEditWizardPageDefaultName);
		setTitle(title);
		setDescription(description);
		this.element = parent;
		this.dbc = dbc;
		this.project = parent.getCamelFile().getResource().getProject();
	}

	@Override
	protected void createArgumentsControls(Composite parent, int cols) {
		beanArgsTable = new ArgumentXMLStyleChildTableControl(parent, SWT.NULL);
		beanArgsTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(cols, 2).create());
		beanArgsTable.setInput(selectedElement);
	}

	@Override
	protected void createPropsControls(Composite parent, int cols) {
		beanPropsTable = new PropertyXMLStyleChildTableControl(parent, SWT.NULL);
		beanPropsTable.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(cols, 2).create());
		beanPropsTable.setInput(selectedElement);
	}

	public void setElement(Element element) {
		this.selectedElement = element;
		if (this.beanPropsTable != null && !this.beanPropsTable.isDisposed()) {
			this.beanPropsTable.setInput(this.selectedElement);
		}
		if (this.beanArgsTable != null && !this.beanArgsTable.isDisposed()) {
			this.beanArgsTable.setInput(this.selectedElement);
		}
	}

	@Override
	protected Binding createClassBinding(UpdateValueStrategy strategy) {
		ComputedValue<String> classValue = new ComputedValue<String>() {

			@Override
			protected String calculate() {
				Object value = beanConfigUtil.getAttributeValue(selectedElement, GlobalBeanEIP.PROP_CLASS);
				if (value instanceof String) {
					return (String) value;
				}
				return null;
			}

			@Override
			protected void doSetValue(String value) {
				final String strValue = value;
				final String oldValue = (String) beanConfigUtil.getAttributeValue(selectedElement, GlobalBeanEIP.PROP_CLASS);
				if ((strValue != null && oldValue == null) || (oldValue != null && !oldValue.contentEquals(strValue))) {
					beanConfigUtil.setAttributeValue(selectedElement, GlobalBeanEIP.PROP_CLASS, strValue);
				}
				getValue();
			}
		};
		IObservableValue<?> observable = WidgetProperties.text(SWT.Modify).observe(classText);
		setClassUiObservable((ISWTObservableValue) observable);
		classObservable = classValue;
		Binding binding = dbc.bindValue(observable, classValue, strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
		return binding;
	}

	@Override
	protected Binding createIdBinding(UpdateValueStrategy strategy) {
		return null;
	}

	@Override
	protected void createIdLine(Composite composite) {
		// do not add id line for edit page
	}

	@Override
	protected String getEditedBeanId() {
		if (selectedElement != null) {
			return (String) beanConfigUtil.getAttributeValue(selectedElement, GlobalBeanEIP.PROP_ID);
		}
		return null;
	}
	
	@Override
	protected Binding createBeanRefBinding(UpdateValueStrategy strategy) {
		ComputedValue<String> refIdValue = new ComputedValue<String>() {

			@Override
			protected String calculate() {
				final String beanTag =
						beanConfigUtil.getFactoryBeanTag(selectedElement);
				final Object value = beanConfigUtil.getAttributeValue(selectedElement, beanTag);
				if (value instanceof String) {
					return (String) value;
				}
				return null;
			}

			@Override
			protected void doSetValue(String value) {
				final String beanTag =
						beanConfigUtil.getFactoryBeanTag(selectedElement);
				final String strValue = value;
				final String oldValue = (String) beanConfigUtil.getAttributeValue(selectedElement, beanTag);
				if ((strValue != null && oldValue == null) || (oldValue != null && !oldValue.contentEquals(strValue))) {
					beanConfigUtil.setAttributeValue(selectedElement, beanTag, strValue);
				}
				getValue();
			}
		};
		IObservableValue<?> observable = WidgetProperties.comboSelection().observe(beanRefIdCombo);
		setRefUiObservable((ISWTObservableValue) observable);
		Binding binding = dbc.bindValue(observable, refIdValue, strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
		return binding;
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		classText.setEditable(false);
		classText.setEnabled(false);
		browseBeanButton.setEnabled(false);
		newBeanButton.setEnabled(false);
		beanRefIdCombo.setEnabled(false);
	}
}

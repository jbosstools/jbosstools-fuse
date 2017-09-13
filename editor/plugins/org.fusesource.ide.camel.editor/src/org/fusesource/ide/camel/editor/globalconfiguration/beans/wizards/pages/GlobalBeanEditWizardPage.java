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
package org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
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
		ComputedValue<?> classValue = new ComputedValue<Object>() {

			@Override
			protected Object calculate() {
				return beanConfigUtil.getAttributeValue(selectedElement, GlobalBeanEIP.PROP_CLASS);
			}

			@Override
			protected void doSetValue(Object value) {
				final String strValue = (String) value;
				final String oldValue = (String) beanConfigUtil.getAttributeValue(selectedElement, GlobalBeanEIP.PROP_CLASS);
				if (!oldValue.contentEquals(strValue)) {
					beanConfigUtil.setAttributeValue(selectedElement, GlobalBeanEIP.PROP_CLASS, strValue);
				}
				getValue();
			}
		};
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(classText), classValue, strategy, null);
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
	protected Binding createBeanRefBinding(UpdateValueStrategy strategy) {
		ComputedValue<?> refIdValue = new ComputedValue<Object>() {

			@Override
			protected Object calculate() {
				final String beanTag =
						beanConfigUtil.getFactoryBeanTag(selectedElement);
				return beanConfigUtil.getAttributeValue(selectedElement, beanTag);
			}

			@Override
			protected void doSetValue(Object value) {
				final String beanTag =
						beanConfigUtil.getFactoryBeanTag(selectedElement);
				final String strValue = (String) value;
				final String oldValue = (String) beanConfigUtil.getAttributeValue(selectedElement, beanTag);
				if (!oldValue.contentEquals(strValue)) {
					beanConfigUtil.setAttributeValue(selectedElement, beanTag, strValue);
				}
				getValue();
			}
		};
		Binding binding = dbc.bindValue(WidgetProperties.selection().observe(beanRefIdCombo), refIdValue, strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
		return binding;
	}
}

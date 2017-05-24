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
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.ArgumentXMLStyleChildTableControl;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.PropertyXMLStyleChildTableControl;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.w3c.dom.Element;

/**
 * @author bfitzpat
 *
 */
public class GlobalBeanEditWizardPage extends WizardPage {

	private DataBindingContext dbc;

	private String id;
	private String classname;
	private IObservableValue<String> classObservable;
	private Element selectedElement;
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private IProject project = null;
	private ArgumentXMLStyleChildTableControl beanArgsTable;
	private PropertyXMLStyleChildTableControl beanPropsTable;

	/**
	 * @param pageName
	 */
	public GlobalBeanEditWizardPage(DataBindingContext dbc, String title, String description, AbstractCamelModelElement parent) {
		super("Bean Details page");
		setTitle(title);
		setDescription(description);
		this.dbc = dbc;
		this.project = parent.getCamelFile().getResource().getProject();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
		createClassLine(composite);
		createBrowseButton(composite);
		createClassNewButton(composite);

		Group argsPropsGroup = new Group(composite, SWT.NONE);
		argsPropsGroup.setText("Constructor Arguments");
		argsPropsGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		argsPropsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(4, 1).create());

		beanArgsTable = new ArgumentXMLStyleChildTableControl(argsPropsGroup, SWT.NULL);
		beanArgsTable.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).span(4, 2).create());
		beanArgsTable.setInput(selectedElement);

		Group beanPropsGroup = new Group(composite, SWT.NONE);
		beanPropsGroup.setText("Bean Properties");
		beanPropsGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		beanPropsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(4, 1).create());

		beanPropsTable = new PropertyXMLStyleChildTableControl(beanPropsGroup, SWT.NULL);
		beanPropsTable.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).span(4, 2).create());
		beanPropsTable.setInput(selectedElement);

		setControl(composite);
		WizardPageSupport.create(this, dbc);
	}

	/**
	 * @param composite
	 */
	private void createClassLine(Composite composite) {
		Label classLabel = new Label(composite, SWT.NONE);
		classLabel.setText("Class");
		Text classText = new Text(composite, SWT.BORDER);
		classText.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).create());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new BeanClassExistsValidator(project));

		ComputedValue<?> classValue = new ComputedValue<Object>() {

			@Override
			protected Object calculate() {
				return beanConfigUtil.getAttributeValue(selectedElement, CamelBean.PROP_CLASS);
			}

			@Override
			protected void doSetValue(Object value) {
				final String strValue = (String) value;
				final String oldValue = (String) beanConfigUtil.getAttributeValue(selectedElement, CamelBean.PROP_ID);
				if (!oldValue.contentEquals(strValue)) {
					beanConfigUtil.setAttributeValue(selectedElement, CamelBean.PROP_CLASS, strValue);
				}
				getValue();
			}
		};
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(classText), classValue, strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
	}

	private void createBrowseButton(Composite composite) {
		Button browseBeanButton = new Button(composite, SWT.PUSH);
		browseBeanButton.setText("...");
		browseBeanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String value = beanConfigUtil.handleClassBrowse(project, getShell());
				if (value != null) {
					classObservable.setValue(value);
				}
			}
		});
	}

	private void createClassNewButton(Composite composite) {
		Button newBeanButton = new Button(composite, SWT.PUSH);
		newBeanButton.setText("+");
		newBeanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String value = beanConfigUtil.handleNewClassWizard(project, getShell());
				if (value != null) {
					classObservable.setValue(value);
				}
			}
		});
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the class
	 */
	public String getClassname() {
		return classname;
	}

	/**
	 * @param classname
	 *            the class name to set
	 */
	public void setClassname(String classname) {
		this.classname = classname;
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
}

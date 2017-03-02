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

import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
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
import org.fusesource.ide.camel.editor.globalconfiguration.beans.ArgumentStyleChildTableControl;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.PropertyStyleChildTableControl;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author bfitzpat
 *
 */
public class GlobalBeanWizardPage extends WizardPage {

	private DataBindingContext dbc;

	private String id;
	private String classname;
	private AbstractCamelModelElement parent;
	private IObservableValue<String> classObservable;
	private ArgumentStyleChildTableControl beanArgsTable;
	private PropertyStyleChildTableControl beanPropsTable;
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();

	/**
	 * @param pageName
	 */
	public GlobalBeanWizardPage(DataBindingContext dbc, String title, String description, AbstractCamelModelElement parent) {
		super("Bean Details page");
		setTitle(title);
		setDescription(description);
		this.dbc = dbc;
		this.parent = parent;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).create());
		createIdLine(composite);
		createClassLine(composite);
		createBrowseButton(composite);
		createClassNewButton(composite);

		Group argsPropsGroup = new Group(composite, SWT.NONE);
		argsPropsGroup.setText("Constructor arguments");
		argsPropsGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		argsPropsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(4, 1).create());

		beanArgsTable = new ArgumentStyleChildTableControl(argsPropsGroup, SWT.NULL);
		beanArgsTable.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).span(4, 2).create());
		beanArgsTable.setInput(this.parent);

		Group beanPropsGroup = new Group(composite, SWT.NONE);
		beanPropsGroup.setText("Bean Properties");
		beanPropsGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		beanPropsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(4, 1).create());

		beanPropsTable = new PropertyStyleChildTableControl(beanPropsGroup, SWT.NULL);
		beanPropsTable.setLayoutData(GridDataFactory.fillDefaults().grab(false, false).span(4, 2).create());
		beanPropsTable.setInput(this.parent);

		setControl(composite);
		WizardPageSupport.create(this, dbc);
	}

	/**
	 * @param composite
	 */
	private void createIdLine(Composite composite) {
		Label idLabel = new Label(composite, SWT.NONE);
		idLabel.setText(UIMessages.GlobalEndpointWizardPage_idFieldLabel);
		Text idText = new Text(composite, SWT.BORDER);
		idText.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).span(3, 1).create());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new NewBeanIdValidator(parent));

		final IObservableValue<?> idObservable = PojoProperties.value(GlobalBeanWizardPage.class, "id", String.class).observe(this); //$NON-NLS-1$
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(idText), idObservable, strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
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
		strategy.setBeforeSetValidator(new BeanClassExistsValidator());

		classObservable = PojoProperties.value(GlobalBeanWizardPage.class, "classname", String.class).observe(this); //$NON-NLS-1$
		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(classText), classObservable, strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
	}

	private void createBrowseButton(Composite composite) {
		Button browseBeanButton = new Button(composite, SWT.PUSH);
		browseBeanButton.setText("...");
		browseBeanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final IProject project = CamelUtils.project();
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
				final IProject project = CamelUtils.project();
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

	public List<AbstractCamelModelElement> getPropertyList() {
		return beanPropsTable.getPropertyList();
	}

	public List<AbstractCamelModelElement> getArgumentsList() {
		return beanArgsTable.getArgumentList();
	}

}

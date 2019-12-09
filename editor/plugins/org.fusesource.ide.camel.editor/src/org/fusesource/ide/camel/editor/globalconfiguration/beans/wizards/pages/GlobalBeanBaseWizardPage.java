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
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.properties.BeanRefAndClassCrossValidator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;

/**
 * @author brianf
 *
 */
public abstract class GlobalBeanBaseWizardPage extends WizardPage {

	protected DataBindingContext dbc;
	protected String id;
	protected String classname;
	protected String beanRefId;
	protected AbstractCamelModelElement element;
	protected IObservableValue<String> classObservable;
	protected BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	protected IProject project = null;
	protected Text classText;
	protected Text idText;
	protected Combo beanRefIdCombo;
	private Binding refBinding;
	private Binding classBinding;
	private BeanRefClassExistsValidator refValidator;
	private BeanClassExistsValidator classValidator;
	protected ISWTObservableValue refUiObservable = null;
	protected ISWTObservableValue classUiObservable = null;
	protected Button browseBeanButton;
	protected Button newBeanButton;

	public GlobalBeanBaseWizardPage(String pageName) {
		super(pageName);
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
		createBeanRefIdLine(composite);
		refValidator.setControl(classText);
		classValidator.setControl(beanRefIdCombo);

		Group argsPropsGroup = new Group(composite, SWT.NONE);
		argsPropsGroup.setText(UIMessages.globalBeanWizardPageArgumentsGroupLabel);
		argsPropsGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		argsPropsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(4, 1).create());

		createArgumentsControls(argsPropsGroup, 4);

		Group beanPropsGroup = new Group(composite, SWT.NONE);
		beanPropsGroup.setText(UIMessages.globalBeanWizardPagePropertiesGroupLabel);
		beanPropsGroup.setLayout(GridLayoutFactory.swtDefaults().numColumns(4).create());
		beanPropsGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(4, 1).create());

		createPropsControls(beanPropsGroup, 4);
		
		MultiValidator beanRefAndClassCrossValidator = new BeanRefAndClassCrossValidator(classUiObservable, refUiObservable);
		ControlDecorationSupport.create(beanRefAndClassCrossValidator, SWT.TOP | SWT.LEFT);
		dbc.addValidationStatusProvider(beanRefAndClassCrossValidator);
		
		setControl(composite);
		WizardPageSupport.create(this, dbc);
	}

	protected abstract void createArgumentsControls(Composite parent, int cols);
	protected abstract void createPropsControls(Composite parent, int cols);
	protected abstract Binding createBeanRefBinding(UpdateValueStrategy strategy);
	protected abstract Binding createClassBinding(UpdateValueStrategy strategy);
	protected abstract Binding createIdBinding(UpdateValueStrategy strategy);

	/**
	 * @param composite
	 */
	protected void createIdLine(Composite composite) {
		Label idLabel = new Label(composite, SWT.NONE);
		idLabel.setText(UIMessages.globalEndpointWizardPageIdFieldLabel);
		idText = new Text(composite, SWT.BORDER);
		idText.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).span(3, 1).create());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new NewBeanIdValidator(element));
		createIdBinding(strategy);
	}

	/**
	 * @param composite
	 */
	protected void createClassLine(Composite composite) {
		Label classLabel = new Label(composite, SWT.NONE);
		classLabel.setText(UIMessages.globalBeanWizardPageClassLabel);
		classText = new Text(composite, SWT.BORDER);
		classText.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).create());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		classValidator = new BeanClassExistsValidator(project, element, beanRefIdCombo);
		strategy.setBeforeSetValidator(classValidator);
		classBinding = createClassBinding(strategy);
		classText.addModifyListener(value -> pingBindings());
	}

	private void createBrowseButton(Composite composite) {
		browseBeanButton = new Button(composite, SWT.PUSH);
		browseBeanButton.setText("..."); //$NON-NLS-1$
		browseBeanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String value = beanConfigUtil.handleClassBrowse(project, getShell());
				if (value != null) {
					classObservable.setValue(value);
					classText.setText(value);
				}
			}
		});
	}

	private void createClassNewButton(Composite composite) {
		newBeanButton = new Button(composite, SWT.PUSH);
		newBeanButton.setText("+"); //$NON-NLS-1$
		newBeanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String initialClassName = classText.getText();
				String value = beanConfigUtil.handleNewClassWizard(project, getShell(), initialClassName);
				if (value != null) {
					classObservable.setValue(value);
					classText.setText(value);
				}
			}

		});
	}

	protected String getEditedBeanId() {
		if (element instanceof CamelBean) {
			return ((CamelBean) element).getId();
		}
		return null;
	}

	/**
	 * @param composite
	 */
	protected void createBeanRefIdLine(Composite composite) {
		Label beanRefIdLabel = new Label(composite, SWT.NONE);
		beanRefIdLabel.setText(UIMessages.globalBeanBaseWizardPageFactoryBeanLabel);
		beanRefIdCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY | SWT.SINGLE | SWT.DROP_DOWN);
		beanRefIdCombo.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).span(3, 1).create());
		if (element != null) {
			String[] beanRefs = CamelComponentUtils.getRefs(element.getCamelFile());
			String[] updatedBeanRefs = beanConfigUtil.removeRefsWithNoClassFromArray(beanRefs, element);
			String beanId = getEditedBeanId();
			if (beanId != null) {
				String[] updatedBeanRefsNoId = beanConfigUtil.removeStringFromStringArray(updatedBeanRefs, beanId);
				beanRefIdCombo.setItems(updatedBeanRefsNoId);
			} else {
				beanRefIdCombo.setItems(updatedBeanRefs);
			}
		}
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		refValidator = new BeanRefClassExistsValidator(project, element, classText);
		strategy.setBeforeSetValidator(refValidator);
		beanRefIdCombo.addModifyListener(value -> pingBindings());
		beanRefIdCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pingBindings();
			}
		});
		refBinding = createBeanRefBinding(strategy);
	}

	private void pingBindings() {
		Display.getCurrent().asyncExec( () -> {
			if (classBinding != null) {
				classValidator.setControl(beanRefIdCombo);
				if (!classText.isDisposed()) {
					classBinding.validateTargetToModel();
				}
			}
			if (refBinding != null) {
				refValidator.setControl(classText);
				if (!beanRefIdCombo.isDisposed()) {
					refBinding.validateTargetToModel();
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
	 * @return the bean ref ID
	 */
	public String getBeanRefId() {
		return beanRefId;
	}

	/**
	 * @param beanRefId id to set
	 */
	public void setBeanRefId(String beanRefId) {
		this.beanRefId = beanRefId;
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

	protected void setClassUiObservable(ISWTObservableValue uiObservable) {
		this.classUiObservable = uiObservable;
	}
	protected void setRefUiObservable(ISWTObservableValue uiObservable) {
		this.refUiObservable = uiObservable;
	}
}

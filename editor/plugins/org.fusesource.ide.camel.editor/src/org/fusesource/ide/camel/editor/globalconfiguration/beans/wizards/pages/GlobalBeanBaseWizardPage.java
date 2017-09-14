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

import java.util.ArrayList;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
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
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.foundation.core.util.Strings;

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
	private void createClassLine(Composite composite) {
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
		Button browseBeanButton = new Button(composite, SWT.PUSH);
		browseBeanButton.setText("..."); //$NON-NLS-1$
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
		newBeanButton.setText("+"); //$NON-NLS-1$
		newBeanButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String initialClassName = classText.getText();
				String value = beanConfigUtil.handleNewClassWizard(project, getShell(), initialClassName);
				if (value != null) {
					classObservable.setValue(value);
				}
			}

		});
	}

	/**
	 * @param composite
	 */
	protected void createBeanRefIdLine(Composite composite) {
		Label beanRefIdLabel = new Label(composite, SWT.NONE);
		beanRefIdLabel.setText("Factory Bean");
		beanRefIdCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY | SWT.SINGLE | SWT.DROP_DOWN);
		beanRefIdCombo.setLayoutData(GridDataFactory.fillDefaults().indent(10, 0).grab(true, false).span(3, 1).create());
		if (element != null) {
			String[] beanRefs = CamelComponentUtils.getRefs(element.getCamelFile());
			String[] updatedBeanRefs = removeRefsWithNoClassFromArray(beanRefs);
			if (element instanceof CamelBean) {
				String[] updatedBeanRefsNoId = removeRefsWithID(beanRefs, ((CamelBean) element).getId());
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

	private String[] removeRefsWithNoClassFromArray(String[] input) {
		ArrayList<String> result = new ArrayList<>();
		for(String item : input) {
			String referencedClassName = beanConfigUtil.getClassNameFromReferencedCamelBean(element, item);
			if(!Strings.isEmpty(referencedClassName) || Strings.isEmpty(item)) {
				result.add(item);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	private String[] removeRefsWithID(String[] input, String id) {
		ArrayList<String> result = new ArrayList<>();
		for(String item : input) {
			if (!Strings.isEmpty(id) && !Strings.isEmpty(item) && !id.contentEquals(item)) {
				result.add(item);
			}
		}
		return result.toArray(new String[result.size()]);
	}

	private void pingBindings() {
		Display.getCurrent().asyncExec( () -> {
			if (classBinding != null) {
				classValidator.setControl(beanRefIdCombo);
				classBinding.validateTargetToModel();
			}
			if (refBinding != null) {
				refValidator.setControl(classText);
				refBinding.validateTargetToModel();
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

}

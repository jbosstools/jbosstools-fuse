/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.globalconfiguration.wizards.pages;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.components.ComponentModel;

/**
 * @author Aurelien Pupier
 *
 */
public class GlobalEndpointWizardPage extends WizardPage {

	private ComponentModel componentModel;
	private DataBindingContext dbc;

	private Component componentSelected;
	private String id;
	private String descriptionCreated;

	/**
	 * @param pageName
	 */
	public GlobalEndpointWizardPage(DataBindingContext dbc, ComponentModel componentModel) {
		super(UIMessages.GlobalEndpointWizardPage_globalEndpointTypeSelectionWizardpageTitle);
		setTitle(UIMessages.GlobalEndpointWizardPage_globalEndpointTypeSelectionWizardpageTitle);
		setDescription(UIMessages.GlobalEndpointWizardPage_globalEndpointTypeSelectionWizardpageDescription);
		this.dbc = dbc;
		this.componentModel = componentModel;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		createIdLine(composite);
		createDescriptionLine(composite);

		createCamelComponentListViewer(composite);
		setControl(composite);
		WizardPageSupport.create(this, dbc);
	}

	/**
	 * @param composite
	 */
	private void createDescriptionLine(Composite composite) {
		Label descriptionLabel = new Label(composite, SWT.NONE);
		descriptionLabel.setText(UIMessages.GlobalEndpointWizardPage_descriptionFieldLabel);
		Text descriptionText = new Text(composite, SWT.BORDER);
		descriptionText.setLayoutData(GridDataFactory.fillDefaults().create());
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(descriptionText),
				PojoProperties.value(GlobalEndpointWizardPage.class, "descriptionCreated", String.class).observe(this)); //$NON-NLS-1$
	}

	/**
	 * @param composite
	 */
	private void createIdLine(Composite composite) {
		Label idLabel = new Label(composite, SWT.NONE);
		idLabel.setText(UIMessages.GlobalEndpointWizardPage_idFieldLabel);
		Text idText = new Text(composite, SWT.BORDER);
		idText.setLayoutData(GridDataFactory.fillDefaults().create());
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new IValidator() {

			@Override
			public IStatus validate(Object value) {
				String id = (String) value;
				if (id == null || id.isEmpty()) {
					return ValidationStatus.error(UIMessages.GlobalEndpointWizardPage_idMandatoryMessage);
				}
				// TODO: check unicity of ID
				return ValidationStatus.ok();
			}
		});

		Binding binding = dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(idText), PojoProperties.value(GlobalEndpointWizardPage.class, "id", String.class).observe(this), //$NON-NLS-1$
				strategy, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
	}

	/**
	 * @param parent
	 */
	private void createCamelComponentListViewer(Composite parent) {
		ListViewer list = new ListViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		list.getList().setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).span(2, 1).hint(450, 400).create());
		list.setContentProvider(ArrayContentProvider.getInstance());
		list.setComparator(new ViewerComparator());
		list.setLabelProvider(new ComponentLabelProvider());
		list.setInput(componentModel.getSupportedComponents());
		UpdateValueStrategy strategy = new UpdateValueStrategy() ;
		strategy.setBeforeSetValidator(new IValidator() {
			
			@Override
			public IStatus validate(Object value) {
				if (value instanceof Component) {
					return Status.OK_STATUS ;
				}
				return ValidationStatus.error(UIMessages.GlobalEndpointWizardPage_componentSelectionMandatoryMessage);
			}
		}) ;
		
		dbc.bindValue(ViewerProperties.singleSelection().observe(list), PojoProperties.value(GlobalEndpointWizardPage.class, "componentSelected", Component.class).observe(this), //$NON-NLS-1$
				strategy, null);
	}

	/**
	 * @return the componentSelected
	 */
	public Component getComponentSelected() {
		return componentSelected;
	}

	/**
	 * @param componentSelected
	 *            the componentSelected to set
	 */
	public void setComponentSelected(Component componentSelected) {
		this.componentSelected = componentSelected;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the description
	 */
	public String getDescriptionCreated() {
		return descriptionCreated;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescriptionCreated(String description) {
		this.descriptionCreated = description;
	}

}

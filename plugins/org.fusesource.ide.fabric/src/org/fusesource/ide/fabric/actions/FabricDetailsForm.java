/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.fabric.actions;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.commons.ui.ICanValidate;
import org.fusesource.ide.commons.ui.form.FormSupport;


/**
 * The form for adding or editing {@link FabricDetails}
 */
public class FabricDetailsForm extends FormSupport {
	private Text nameField;
	private FabricDetails details = new FabricDetails();

	public FabricDetailsForm(ICanValidate validator) {
		super(validator);
	}

	@Override
	protected boolean isMandatory(Object bean, String propertyName) {
		return true;
	}

	public FabricDetails getDetails() {
		return details;
	}

	public void setDetails(FabricDetails details) {
		this.details = details;
	}

	@Override
	public void setFocus() {
		nameField.setFocus();
	}

	@Override
	public void createTextFields(Composite parent) {
		Composite inner = createSectionComposite(Messages.fabricDetailsSection, new GridData(GridData.FILL_BOTH));

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		inner.setLayout(layout);

		nameField = createBeanPropertyTextField(inner, details, "name", Messages.fabricNameLabel, Messages.fabricNameTooltip);
		createBeanPropertyTextField(inner, details, "urls", Messages.fabricUrlsLabel, Messages.fabricUrlsTooltip);
		createBeanPropertyTextField(inner, details, "userName", Messages.fabricUserNameLabel, Messages.fabricUserNameTooltip);
		createBeanPropertyTextField(inner, details, "password", Messages.fabricPasswordLabel, Messages.fabricPasswordTooltip, SWT.PASSWORD);
	}

	@Override
	public void okPressed() {
	}


}
/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.ui.wizards.pages;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.ControlDecorationHelper;
import org.fusesource.ide.foundation.ui.util.Widgets;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;

/**
 * @author lheinema
 */
public class SyndesisExtensionProjectWizardExtensionDetailsPage extends WizardPage {

	private Text extensionIdText;
	private Text extensionVersionText;
	private Text extensionNameText;
	private Text extensionDescriptionText;
	private Text extensionTagsText;
	
	public SyndesisExtensionProjectWizardExtensionDetailsPage() {
		super(Messages.newProjectWizardExtensionDetailsPageName);
		setTitle(Messages.newProjectWizardExtensionDetailsPageTitle);
		setDescription(Messages.newProjectWizardExtensionDetailsPageDescription);
		setImageDescriptor(SyndesisExtensionsUIActivator.imageDescriptorFromPlugin(SyndesisExtensionsUIActivator.PLUGIN_ID, SyndesisExtensionsUIActivator.SYNDESIS_EXTENSION_PROJECT_ICON));
		setPageComplete(false);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		this.extensionIdText = createField(container, Messages.newProjectWizardExtensionDetailsPageExtensionIdLabel, null, Messages.newProjectWizardExtensionDetailsPageExtensionIdTooltip);
		this.extensionVersionText = createField(container, Messages.newProjectWizardExtensionDetailsPageVersionLabel, null, Messages.newProjectWizardExtensionDetailsPageVersionTooltip);
		this.extensionNameText = createField(container, Messages.newProjectWizardExtensionDetailsPageNameLabel, null, Messages.newProjectWizardExtensionDetailsPageNameTooltip);
		this.extensionDescriptionText = createField(container, Messages.newProjectWizardExtensionDetailsPageDescriptionLabel, Messages.newProjectWizardExtensionDetailsPageOptionalDescriptionFieldHint, Messages.newProjectWizardExtensionDetailsPageDescriptionTooltip);
		this.extensionTagsText = createField(container, Messages.newProjectWizardExtensionDetailsPageTagsLabel, Messages.newProjectWizardExtensionDetailsPageOptionalTagsFieldHint, Messages.newProjectWizardExtensionDetailsPageTagsTooltip);
		
		setControl(container);
		
		extensionIdText.setFocus();
	}

	private Text createField(Composite container, String label, String message, String toolTip) {
		// create the label
		Label l = new Label(container, SWT.NONE);
		l.setText(label);
		
		GridData gridData = GridDataFactory.fillDefaults().grab(true, false).span(2, 1).indent(8, 0).create();
		
		// create the control
		Text textField = new Text(container, SWT.BORDER);
		textField.setLayoutData(gridData);
		textField.setToolTipText(toolTip);
		
		new ControlDecorationHelper().addInformationOnFocus(textField, toolTip);
		
		if (!Strings.isBlank(message)) {
			textField.setMessage(message);
		}
		textField.addModifyListener( (ModifyEvent e) -> validateFields() );
		
		return textField;
	}
	
	public String getExtensionId() {
		if (!Widgets.isDisposed(extensionIdText) && !Strings.isBlank(extensionIdText.getText())) {
			return extensionIdText.getText();
		}
		return null;
	}
	
	public String getExtensionVersion() {
		if (!Widgets.isDisposed(extensionVersionText) && !Strings.isBlank(extensionVersionText.getText())) {
			return extensionVersionText.getText();
		}
		return null;
	}
	
	public String getExtensionName() {
		if (!Widgets.isDisposed(extensionNameText) && !Strings.isBlank(extensionNameText.getText())) {
			return extensionNameText.getText();
		}
		return null;
	}
	
	public String getExtensionDescription() {
		if (!Widgets.isDisposed(extensionDescriptionText) && !Strings.isBlank(extensionDescriptionText.getText())) {
			return extensionDescriptionText.getText();
		}
		return null;
	}
	
	public List<String> getExtensionTags() {
		return Arrays.asList(extensionTagsText.getText().split(","));
	}
	
	private void validateFields() {
		setErrorMessage(null);
		
		if (Strings.isBlank(extensionIdText.getText())) {
			setErrorMessage(Messages.newProjectWizardExtensionDetailsPageErrorMissingExtensionId);
			setPageComplete(false);
			return;
		} else if (extensionIdText.getText().indexOf(' ') != -1) {
			setErrorMessage(Messages.newProjectWizardExtensionDetailsPageErrorInvalidExtensionId);
			setPageComplete(false);
			return;
		}
		
		if (Strings.isBlank(extensionVersionText.getText())) {
			setErrorMessage(Messages.newProjectWizardExtensionDetailsPageErrorMissingExtensionVersion);
			setPageComplete(false);
			return;
		}
		
		if (Strings.isBlank(extensionNameText.getText())) {
			setErrorMessage(Messages.newProjectWizardExtensionDetailsPageErrorMissingExtensionName);
			setPageComplete(false);
			return;
		}

		setPageComplete(getErrorMessage() == null);
	}
}

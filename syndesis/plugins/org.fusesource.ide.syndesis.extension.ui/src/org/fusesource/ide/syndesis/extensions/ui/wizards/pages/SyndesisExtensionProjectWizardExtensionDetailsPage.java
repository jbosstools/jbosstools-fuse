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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.foundation.core.util.Strings;
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
	private Text extensionIconText;
	private Text extensionTagsText;
	
	private FocusListener focusListener = new FocusListener() {
		
		@Override
		public void focusLost(FocusEvent e) {
			validateFields();
		}
		
		@Override
		public void focusGained(FocusEvent e) {
			// not interested in that event
		}
	};
	
	private ModifyListener modifyListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			validateFields();
		}
	};
	
	public SyndesisExtensionProjectWizardExtensionDetailsPage() {
		super(Messages.newProjectWizardExtensionDetailsPageName);
		setTitle(Messages.newProjectWizardExtensionDetailsPageTitle);
		setDescription(Messages.newProjectWizardExtensionDetailsPageDescription);
		setImageDescriptor(SyndesisExtensionsUIActivator.imageDescriptorFromPlugin(SyndesisExtensionsUIActivator.PLUGIN_ID, SyndesisExtensionsUIActivator.SYNDESIS_EXTENSION_PROJECT_ICON));
		setPageComplete(false);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(3, false));

		Label extensionIdLabel = new Label(container, SWT.NONE);
		extensionIdLabel.setText(Messages.newProjectWizardExtensionDetailsPageExtensionIdLabel);
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		extensionIdText = new Text(container, SWT.BORDER);
		extensionIdText.setLayoutData(gridData);
		extensionIdText.setToolTipText(Messages.newProjectWizardExtensionDetailsPageExtensionIdTooltip);
		extensionIdText.addFocusListener(focusListener);
		extensionIdText.addModifyListener(modifyListener);
		
		Label extensionVersionLabel = new Label(container, SWT.NONE);
		extensionVersionLabel.setText(Messages.newProjectWizardExtensionDetailsPageVersionLabel);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		extensionVersionText = new Text(container, SWT.BORDER);
		extensionVersionText.setLayoutData(gridData);
		extensionVersionText.setToolTipText(Messages.newProjectWizardExtensionDetailsPageVersionTooltip);
		extensionVersionText.addFocusListener(focusListener);
		extensionVersionText.addModifyListener(modifyListener);
		
		Label extensionNameLabel = new Label(container, SWT.NONE);
		extensionNameLabel.setText(Messages.newProjectWizardExtensionDetailsPageNameLabel);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		extensionNameText = new Text(container, SWT.BORDER);
		extensionNameText.setLayoutData(gridData);
		extensionNameText.setToolTipText(Messages.newProjectWizardExtensionDetailsPageNameTooltip);
		extensionNameText.addFocusListener(focusListener);
		extensionNameText.addModifyListener(modifyListener);
		
		Label extensionDescriptionLabel = new Label(container, SWT.NONE);
		extensionDescriptionLabel.setText(Messages.newProjectWizardExtensionDetailsPageDescriptionLabel);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		extensionDescriptionText = new Text(container, SWT.BORDER);
		extensionDescriptionText.setLayoutData(gridData);
		extensionDescriptionText.setToolTipText(Messages.newProjectWizardExtensionDetailsPageDescriptionTooltip);
		extensionDescriptionText.setMessage(Messages.newProjectWizardExtensionDetailsPageOptionalDescriptionFieldHint);
		extensionDescriptionText.addFocusListener(focusListener);
		extensionDescriptionText.addModifyListener(modifyListener);
		
		Label extensionIconLabel = new Label(container, SWT.NONE);
		extensionIconLabel.setText(Messages.newProjectWizardExtensionDetailsPageIconLabel);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		extensionIconText = new Text(container, SWT.BORDER);
		extensionIconText.setLayoutData(gridData);
		extensionIconText.setMessage(Messages.newProjectWizardExtensionDetailsPageOptionalIconFieldHint);
		extensionIconText.setToolTipText(Messages.newProjectWizardExtensionDetailsPageIconTooltip);
		extensionIconText.addFocusListener(focusListener);
		extensionIconText.addModifyListener(modifyListener);
		Button extensionIconBrowseButton = new Button(container, SWT.PUSH | SWT.BORDER);
		extensionIconBrowseButton.setText(Messages.newProjectWizardExtensionDetailsPageIconBrowseLabel);
		extensionIconBrowseButton.addFocusListener(focusListener);
		extensionIconBrowseButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setText(Messages.newProjectWizardExtensionDetailsPageIconSelectionDialogTitle);
				dialog.setFilterPath(".");
				dialog.setFilterExtensions(new String[] { "*.gif;*.png;*.jpg;*.jpeg;*.svg" });
				dialog.setFilterNames(new String[] { Messages.newProjectWizardExtensionDetailsPageIconSelectionDialogFileTypeLabel });
				String selectedIcon = dialog.open();
				if (selectedIcon != null) {
					File icon = new File(selectedIcon);
					if (icon.exists() && icon.isFile() && icon.canRead()) {
						extensionIconText.setText(selectedIcon);						
					} else {
						setErrorMessage(NLS.bind(Messages.newProjectWizardExtensionDetailsPageIconSelectionDialogFileUnavailableError, selectedIcon));
					}
				}
			}
		});
		
		Label extensionTagsLabel = new Label(container, SWT.NONE);
		extensionTagsLabel.setText(Messages.newProjectWizardExtensionDetailsPageTagsLabel);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		extensionTagsText = new Text(container, SWT.BORDER);
		extensionTagsText.setLayoutData(gridData);
		extensionTagsText.setToolTipText(Messages.newProjectWizardExtensionDetailsPageTagsTooltip);
		extensionTagsText.setMessage(Messages.newProjectWizardExtensionDetailsPageOptionalTagsFieldHint);
		extensionTagsText.addFocusListener(focusListener);
		extensionTagsText.addModifyListener(modifyListener);
		
		setControl(container);
		
		extensionIdText.setFocus();
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
	
	public String getExtensionIcon() {
		if (!Widgets.isDisposed(extensionIconText) && !Strings.isBlank(extensionIconText.getText())) {
			return extensionIconText.getText();
		}
		return null;
	}
	
	public List<String> getExtensionTags() {
		if (!Widgets.isDisposed(extensionTagsText) && !Strings.isBlank(extensionTagsText.getText())) {
			List<String> tags = new ArrayList<>();
			tags.addAll(Arrays.asList(extensionTagsText.getText().split(",")));
			return tags;
		}
		return Arrays.asList();
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

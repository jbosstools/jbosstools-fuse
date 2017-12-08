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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.util.Widgets;
import org.fusesource.ide.projecttemplates.util.CamelVersionChecker;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;
import org.fusesource.ide.syndesis.extensions.ui.internal.SyndesisExtensionsUIActivator;

/**
 * @author lheinema
 */
public class SyndesisExtensionProjectWizardVersionsPage extends WizardPage {

    private Combo springBootVersionCombo;
	private Combo camelVersionCombo;
	private Combo syndesisVersionCombo;
	
	private SelectionListener selectionListener = new SelectionAdapter() {
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			validate();
		}
	};

	public SyndesisExtensionProjectWizardVersionsPage() {
		super(Messages.newProjectWizardExtensionVersionsPageName);
		setTitle(Messages.newProjectWizardExtensionVersionsPageTitle);
		setDescription(Messages.newProjectWizardExtensionVersionsPageDescription);
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

		Label springBootVersionLabel = new Label(container, SWT.NONE);
		springBootVersionLabel.setText(Messages.newProjectWizardExtensionVersionsPageSpringBootVersionLabel);
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		springBootVersionCombo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN);
		springBootVersionCombo.setLayoutData(gridData);
		springBootVersionCombo.setToolTipText(Messages.newProjectWizardExtensionVersionsPageSpringBootVersionTooltip);
		fillSpringBootVersions();
		springBootVersionCombo.addSelectionListener(selectionListener);
		springBootVersionCombo.addModifyListener( (ModifyEvent e) -> validate() );
		
		Label camelVersionLabel = new Label(container, SWT.NONE);
		camelVersionLabel.setText(Messages.newProjectWizardExtensionVersionsPageCamelVersionLabel);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		camelVersionCombo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN);
		camelVersionCombo.setLayoutData(gridData);
		camelVersionCombo.setToolTipText(Messages.newProjectWizardExtensionVersionsPageCamelVersionTooltip);
		fillCamelVersions();
		camelVersionCombo.addSelectionListener(selectionListener);
		camelVersionCombo.addModifyListener( (ModifyEvent e) -> validate() );
		
		Button camelVersionValidationBtn = new Button(container, SWT.PUSH);
		GridData camelButtonData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		camelVersionValidationBtn.setLayoutData(camelButtonData);
		camelVersionValidationBtn.setText(Messages.newProjectWizardExtensionVersionsPageCamelVersionValidationLabel);
		camelVersionValidationBtn.setToolTipText(Messages.newProjectWizardExtensionVersionsPageCamelVersionValidationTooltip);
		camelVersionValidationBtn.addSelectionListener(new VersionValidationHandler());
		
		Label syndesisVersionLabel = new Label(container, SWT.NONE);
		syndesisVersionLabel.setText(Messages.newProjectWizardExtensionVersionsPageSyndesisVersionLabel);
		gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		syndesisVersionCombo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN);
		syndesisVersionCombo.setLayoutData(gridData);
		syndesisVersionCombo.setToolTipText(Messages.newProjectWizardExtensionVersionsPageSyndesisVersionTooltip);
		fillSyndesisVersions();
		syndesisVersionCombo.addSelectionListener(selectionListener);
		syndesisVersionCombo.addModifyListener( (ModifyEvent e) -> validate() );
		
		setControl(container);
		
		springBootVersionCombo.setFocus();
		validate();
	}

	private void fillSpringBootVersions() {
		// for the moment we only support 1.5.8
		springBootVersionCombo.add("1.5.8.RELEASE");
		springBootVersionCombo.select(0);
	}
	
	private void fillCamelVersions() {
		// for the moment we only support 2.20.0
		camelVersionCombo.add("2.20.0");
		camelVersionCombo.select(0);
	}
	
	private void fillSyndesisVersions() {
		// for the moment we only support 1.2-SNAPSHOT
		syndesisVersionCombo.add("1.2-SNAPSHOT");
		syndesisVersionCombo.select(0);
	}
	
	public String getSpringBootVersion() {
		if (!Widgets.isDisposed(springBootVersionCombo) && !Strings.isBlank(springBootVersionCombo.getText())) {
			return springBootVersionCombo.getText();
		}
		return null;
	}
	
	public String getCamelVersion() {
		if (!Widgets.isDisposed(camelVersionCombo) && !Strings.isBlank(camelVersionCombo.getText())) {
			return camelVersionCombo.getText();
		}
		return null;
	}
	
	public String getSyndesisVersion() {
		if (!Widgets.isDisposed(syndesisVersionCombo) && !Strings.isBlank(syndesisVersionCombo.getText())) {
			return syndesisVersionCombo.getText();
		}
		return null;
	}
	
	private void validate() {
		setErrorMessage(null);
		
		if (Strings.isBlank(springBootVersionCombo.getText())) {
			setErrorMessage(Messages.newProjectWizardExtensionVersionsPageErrorMissingSpringBootVersion);
			setPageComplete(false);
			return;
		}
		
		if (Strings.isBlank(camelVersionCombo.getText())) {
			setErrorMessage(Messages.newProjectWizardExtensionVersionsPageErrorMissingCamelVersion);
			setPageComplete(false);
			return;
		}
		
		if (Strings.isBlank(syndesisVersionCombo.getText())) {
			setErrorMessage(Messages.newProjectWizardExtensionVersionsPageErrorMissingSyndesisVersion);
			setPageComplete(false);
			return;
		}

		setPageComplete(getErrorMessage() == null);
	}
	
	class VersionValidationHandler extends SelectionAdapter {
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			String camelVersion = getCamelVersion();
			CamelVersionChecker versionChecker = new CamelVersionChecker(camelVersion);
			try {
				getWizard().getContainer().run(true, true, versionChecker);
			} catch (InterruptedException iex) {
				versionChecker.cancel();
				Thread.currentThread().interrupt();
			} catch (Exception ex) {
				SyndesisExtensionsUIActivator.pluginLog().logError(ex);
			}
			while (!versionChecker.isDone()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
			updateCamelValidation(camelVersion, versionChecker.isValid());
		}
		
		private void updateCamelValidation(String camelVersion, boolean valid) {
			if (!valid) {
				setMessage(null);
				setErrorMessage(NLS.bind(Messages.newProjectWizardExtensionVersionsPageErrorInvalidCamelVersion, camelVersion));
			} else {
				setErrorMessage(null);
				setMessage(Messages.newProjectWizardExtensionVersionsPageCamelVersionValid, INFORMATION);
			}
			setPageComplete(valid);
		}
	}
}

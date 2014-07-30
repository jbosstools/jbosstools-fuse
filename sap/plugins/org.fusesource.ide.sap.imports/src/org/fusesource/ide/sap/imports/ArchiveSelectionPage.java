/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.imports;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.sap.imports.JCo3Archive.JCoArchiveType;

public class ArchiveSelectionPage extends WizardPage {
	
	private static final String[] FILE_EXTS = new String[] { "*.zip", "*.tgz", ".tar.gz", ".tar", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	
	private class ArchiveNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isJCo3ArchiveValid = false;
						return ValidationStatus.error(Messages.ArchiveSelectionPage_PleaseSelectJCo3Archive);
					}
				} else {
					throw new RuntimeException(Messages.ArchiveSelectionPage_InvalidJCo3ArchiveNameValue);
				}
				isJCo3ArchiveValid = true;
				return Status.OK_STATUS;
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}
	
	private class BundleNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isBundleNameValid = false;
						return ValidationStatus.error(Messages.ArchiveSelectionPage_PleaseEnterBundleName);
					}
				} else {
					throw new RuntimeException(Messages.ArchiveSelectionPage_InvalidBundleNameValue);
				}
				isBundleNameValid = true;
				return Status.OK_STATUS;
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}
	
	private class BundleVersionValidator implements IValidator {
		private static final String BUNDLE_VERSION_REGEX = "\\d+\\.\\d+\\.\\d+(\\..*)?"; //$NON-NLS-1$

		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isBundleVersionValid = false;
						return ValidationStatus.error(Messages.ArchiveSelectionPage_PleaseEnterBundleVersion);
					}
					if (!str.matches(BUNDLE_VERSION_REGEX)) {
						return ValidationStatus.error(Messages.ArchiveSelectionPage_BundlerVersionMustBeInMajorMinorMicroQualifierFormat);
					}
				} else {
					throw new RuntimeException(Messages.ArchiveSelectionPage_InvalidBundleVersionValue);
				}
				isBundleVersionValid = true;
				return Status.OK_STATUS;
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}
	
	private class BundleVendorValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isBundleVendorValid = false;
						return ValidationStatus.error(Messages.ArchiveSelectionPage_BundleVendorMustNotBeEmpty);
					}
				} else {
					throw new RuntimeException(Messages.ArchiveSelectionPage_InvalidBundleVendorValue);
				}
				isBundleVendorValid = true;
				return Status.OK_STATUS;
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}
	
	private class RequiredExecutionEnvironmentValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof Integer) {
					Integer index = (Integer) value;
					if (index == -1) {
						isRequiredExecutionEnvironmentValid = false;
						return ValidationStatus.error(Messages.ArchiveSelectionPage_PleaseSelectRequiredExecutionEnvironment);
					}
				} else {
					throw new RuntimeException(Messages.ArchiveSelectionPage_InvalidRequiredExecutionEnvironmentValue);
				}
				isRequiredExecutionEnvironmentValid = true;
				return Status.OK_STATUS;
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}

	private DataBindingContext context; 

	private JCo3ImportSettings jcoImportSettings;
	
	private Text textSelectJCo3Archive;
	private Button btnSelectJCo3Archive;
	
	private Text textEnterBundleName;
	
	private Text textEnterBundleVersion;
	
	private Text textEnterBundleVendor;
	
	private Label lblSelectRequiredExecution;
	private Combo comboSelectRequiredExectionEnvironment;
	private Label lblArchiveOsPlatform;
	private Text textArchiveOs;
	private Label lblArchiveVersion;
	private Text textArchiveVersion;
	
	private boolean isJCo3ArchiveValid;
	private boolean isBundleNameValid;
	private boolean isBundleVersionValid;
	private boolean isBundleVendorValid;
	private boolean isRequiredExecutionEnvironmentValid;

	protected ArchiveSelectionPage(DataBindingContext context, JCo3ImportSettings importSettings) {
		super(Messages.ArchiveSelectionPage_PageName);
		setDescription(Messages.ArchiveSelectionPage_this_description);
		setTitle(Messages.ArchiveSelectionPage_PageTitle);
		this.context = context;
		this.jcoImportSettings = importSettings;
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Binding binding;
		Composite top = new Composite(parent, SWT.NONE);
		GridData topData = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL);
		top.setLayoutData(topData);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(top);
		
		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(top);
		
		GridLayout gl_top = new GridLayout(3, false);
		gl_top.marginHeight = 0;
		gl_top.marginWidth = 0;
		top.setLayout(gl_top);
		
		Label lblSelectJCo3Archive = new Label(top, SWT.NONE);
		lblSelectJCo3Archive.setText(Messages.ArchiveSelectionPage_JCo3SelectLable);
		
		textSelectJCo3Archive = new Text(top, SWT.BORDER | SWT.READ_ONLY);
		textSelectJCo3Archive.setMessage(Messages.ArchiveSelectionPage_JCo3ArchivePath_text_message);
		textSelectJCo3Archive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		binding = context.bindValue(SWTObservables.observeText(textSelectJCo3Archive, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, "jco3ArchiveFilename"), new UpdateValueStrategy().setAfterConvertValidator(new ArchiveNameValidator())	, new UpdateValueStrategy()); //$NON-NLS-1$
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		
		
		btnSelectJCo3Archive = new Button(top, SWT.NONE);
		btnSelectJCo3Archive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getJCo3ArchiveFile();
			}
		});
		btnSelectJCo3Archive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSelectJCo3Archive.setText(Messages.ArchiveSelectionPage_Browse_text);
		
		lblArchiveOsPlatform = new Label(top, SWT.NONE);
		GridData gd_lblArchiveOsPlatform = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblArchiveOsPlatform.horizontalIndent = 10;
		lblArchiveOsPlatform.setLayoutData(gd_lblArchiveOsPlatform);
		lblArchiveOsPlatform.setText(Messages.ArchiveSelectionPage_lblArchiveOsPlatform_text);
		
		textArchiveOs = new Text(top, SWT.BORDER | SWT.READ_ONLY);
		textArchiveOs.setEnabled(false);
		textArchiveOs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		context.bindValue(SWTObservables.observeText(textArchiveOs, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, "archiveOs"), new UpdateValueStrategy(), new UpdateValueStrategy()); //$NON-NLS-1$
		
		lblArchiveVersion = new Label(top, SWT.NONE);
		GridData gd_lblArchiveVersion = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblArchiveVersion.horizontalIndent = 10;
		lblArchiveVersion.setLayoutData(gd_lblArchiveVersion);
		lblArchiveVersion.setText(Messages.ArchiveSelectionPage_lblArchiveVersion_text);
		
		textArchiveVersion = new Text(top, SWT.BORDER | SWT.READ_ONLY);
		textArchiveVersion.setEnabled(false);
		textArchiveVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		context.bindValue(SWTObservables.observeText(textArchiveVersion, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, "archiveVersion"), new UpdateValueStrategy(), new UpdateValueStrategy()); //$NON-NLS-1$
		
		Label lblEnterBundleName = new Label(top, SWT.NONE);
		lblEnterBundleName.setText(Messages.ArchiveSelectionPage_lblEnterBundleName_text);
		
		textEnterBundleName = new Text(top, SWT.BORDER);
		textEnterBundleName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		binding = context.bindValue(SWTObservables.observeText(textEnterBundleName, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, "bundleName"), new UpdateValueStrategy().setAfterConvertValidator(new BundleNameValidator()), new UpdateValueStrategy()); //$NON-NLS-1$
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		textEnterBundleName.setText(Messages.ArchiveSelectionPage_BundleNameDefault);
		
		Label lblEnterBundleVersion = new Label(top, SWT.NONE);
		lblEnterBundleVersion.setText(Messages.ArchiveSelectionPage_lblEnterBundleVersion_text);
		
		textEnterBundleVersion = new Text(top, SWT.BORDER);
		textEnterBundleVersion.setMessage(Messages.ArchiveSelectionPage_textEnterBundleVersion_message);
		textEnterBundleVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		binding = context.bindValue(SWTObservables.observeText(textEnterBundleVersion, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, "bundleVersion"), new UpdateValueStrategy().setAfterConvertValidator(new BundleVersionValidator()), new UpdateValueStrategy().setAfterConvertValidator(new BundleVersionValidator())); //$NON-NLS-1$
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		
		Label lblEnterBundleVendor = new Label(top, SWT.NONE);
		lblEnterBundleVendor.setText(Messages.ArchiveSelectionPage_lblEnterVendorName_text);
		
		textEnterBundleVendor = new Text(top, SWT.BORDER);
		textEnterBundleVendor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		binding = context.bindValue(SWTObservables.observeText(textEnterBundleVendor, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, "bundleVendor"), new UpdateValueStrategy().setAfterConvertValidator(new BundleVendorValidator()), new UpdateValueStrategy()); //$NON-NLS-1$
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		textEnterBundleVendor.setText(Messages.ArchiveSelectionPage_BundleVendorDefault);
		
		lblSelectRequiredExecution = new Label(top, SWT.NONE);
		lblSelectRequiredExecution.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSelectRequiredExecution.setText(Messages.ArchiveSelectionPage_lblSelectRequiredExecution_text);
		
		comboSelectRequiredExectionEnvironment = new Combo(top, SWT.READ_ONLY);
		comboSelectRequiredExectionEnvironment.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		comboSelectRequiredExectionEnvironment.setItems(ImportUtils.getExecutionEnvironments());
		binding = context.bindValue(SWTObservables.observeSingleSelectionIndex(comboSelectRequiredExectionEnvironment), BeansObservables.observeValue(jcoImportSettings, "requiredExecutionEnvironment"), new UpdateValueStrategy().setAfterConvertValidator(new RequiredExecutionEnvironmentValidator()),  new UpdateValueStrategy()); //$NON-NLS-1$
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
	}
	
	public boolean checkPageComplete() {
		return isJCo3ArchiveValid && isBundleNameValid && isBundleVendorValid && isBundleVersionValid && isRequiredExecutionEnvironmentValid;
	}
	
	protected void getJCo3ArchiveFile() {
		String filename = getFile(textSelectJCo3Archive.getText());
        if (filename != null) {
            filename = filename.trim();
            if (filename.length() > 0) {
            	try {
					JCo3Archive jcoArchive = new JCo3Archive(filename);
					if (jcoArchive.getType() == JCoArchiveType.JCO_INVALID_ARCHIVE) {
						textSelectJCo3Archive.setText(""); //$NON-NLS-1$
						textArchiveOs.setText(""); //$NON-NLS-1$
						textArchiveVersion.setText(""); //$NON-NLS-1$
						setErrorMessage(Messages.ArchiveSelectionPage_ArchiveFileIsNotASupportedType);
						return;
					}
					jcoImportSettings.setJco3Archive(jcoArchive);
					textSelectJCo3Archive.setText(filename);
				} catch (IOException e) {
					textSelectJCo3Archive.setText(""); //$NON-NLS-1$
					textArchiveOs.setText(""); //$NON-NLS-1$
					textArchiveVersion.setText(""); //$NON-NLS-1$
					setErrorMessage(Messages.ArchiveSelectionPage_ArchiveFileIsNotASupportedTypeColon + e.getMessage());
				}
			}
        }
	}
	
	protected String getFile(String startingFilename) {
		setErrorMessage(null);
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
		
        File startingFile = new File(startingFilename);
		if (startingFile.exists()) {
			dialog.setFileName(startingFile.getPath());
		}
	
        dialog.setFilterExtensions(FILE_EXTS);
        String file = dialog.open();
        if (file != null) {
            file = file.trim();
            if (file.length() > 0) {
				return file;
			}
        }
        return null;
	}
}

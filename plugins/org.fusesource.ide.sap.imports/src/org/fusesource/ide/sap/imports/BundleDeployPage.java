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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class BundleDeployPage extends WizardPage {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	public class BundleLocationValidator implements IValidator {
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
		 */
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isJCo3BundleExportLocationValid = false;
						return ValidationStatus.error(Messages.BundleDeployPage_PleaseSelectJC03BundleExportLocation);
					}
				} else {
					throw new RuntimeException(Messages.BundleDeployPage_InvalidJCo3BundleExportLocationValue);
				}
				isJCo3BundleExportLocationValid = true;
				return Status.OK_STATUS;
			} finally {
				setPageComplete(checkPageComplete());
			}
		}

	}

	private DataBindingContext context;
	private SAPImportSettings jcoImportSettings;
	private Text textSelectJCo3BundleExportLocation;
	private Button btnSelectJCo3Archive;
	private boolean isJCo3BundleExportLocationValid;

	/**
	 * 
	 * @param context
	 * @param importSettings
	 */
	protected BundleDeployPage(DataBindingContext context, SAPImportSettings importSettings) {
		super(Messages.BundleDeployPage_SelectLocationToExportJCo3Bundles);
		setTitle(Messages.BundleDeployPage_ExportJCo3Bundles);
		this.context = context;
		this.jcoImportSettings = importSettings;
		setPageComplete(false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
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
		lblSelectJCo3Archive.setText(Messages.BundleDeployPage_SelectJCo3BundlesExportLocationColon);
		
		this.textSelectJCo3BundleExportLocation = new Text(top, SWT.BORDER | SWT.READ_ONLY);
		this.textSelectJCo3BundleExportLocation.setMessage(Messages.ArchiveSelectionPage_JCo3ArchivePath_text_message);
		this.textSelectJCo3BundleExportLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		binding = context.bindValue(SWTObservables.observeText(this.textSelectJCo3BundleExportLocation, SWT.Modify), BeansObservables.observeValue(this.jcoImportSettings, "jco3BundlesExportLocation"), new UpdateValueStrategy().setAfterConvertValidator(new BundleLocationValidator())	, new UpdateValueStrategy());
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		this.textSelectJCo3BundleExportLocation.setText(ImportUtils.getDefaultDeployLocation());
		
		this.btnSelectJCo3Archive = new Button(top, SWT.NONE);
		this.btnSelectJCo3Archive.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				getJCo3BundlesExportLocation();
			}
		});
		this.btnSelectJCo3Archive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		this.btnSelectJCo3Archive.setText(Messages.ArchiveSelectionPage_Browse_text);

	}

	/**
	 * 
	 * @return
	 */
	private boolean checkPageComplete() {
		return isJCo3BundleExportLocationValid;
	}
	
	/**
	 * 
	 */
	protected void getJCo3BundlesExportLocation() {
		String directory = getDirectory(this.textSelectJCo3BundleExportLocation.getText());
		if (directory != null) {
			directory = directory.trim();
			if (directory.length() > 0) {
				File dir = new File(directory);
				if (!dir.exists()) {
					setErrorMessage(Messages.BundleDeployPage_DirectoryDoesNotExist);
					this.textSelectJCo3BundleExportLocation.setText(EMPTY_STRING);
					return;
				}
				if (!dir.canWrite()) {
					setErrorMessage(Messages.BundleDeployPage_CanNotWriteDirectory);
					this.textSelectJCo3BundleExportLocation.setText(EMPTY_STRING);
					return;
				}
				this.textSelectJCo3BundleExportLocation.setText(directory);
			}
		}
	}

	/**
	 * 
	 * @param startingDirectory
	 * @return
	 */
	protected String getDirectory(String startingDirectory) {
		setErrorMessage(null);
        DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
		
        File startingFile = new File(startingDirectory);
		if (startingFile.exists()) {
			dialog.setFilterPath(startingFile.getPath());
		}
	
        String directory = dialog.open();
        if (directory != null) {
            directory = directory.trim();
            if (directory.length() > 0) {
				return directory;
			}
        }
        return null;
	}
}

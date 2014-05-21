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
import java.text.MessageFormat;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.sap.imports.ImportUtils.UnsupportedVersionException;
import org.fusesource.ide.sap.imports.JCo3Archive.JCoArchiveType;

public class JCo3ArchiveSelectionPage extends WizardPage {

	private static final String BLANK_STRING = ""; //$NON-NLS-1$

	private static final String[] FILE_EXTS = new String[] { "*.zip", "*.tgz", ".tar.gz", ".tar", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	
	private class ArchiveNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isJCo3ArchiveValid = false;
						return ValidationStatus.error(Messages.JCo3ArchiveSelectionPage_PleaseSelectJCo3Archive);
					}
				} else {
					throw new RuntimeException(Messages.JCo3ArchiveSelectionPage_InvalidJCo3ArchiveNameValue);
				}
				isJCo3ArchiveValid = true;
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
	
	private Label lblArchiveOsPlatform;
	private Text textArchiveOs;
	private Label lblArchiveVersion;
	private Text textArchiveVersion;
	
	private boolean isJCo3ArchiveValid;

	protected JCo3ArchiveSelectionPage(DataBindingContext context, JCo3ImportSettings importSettings) {
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
		binding = context.bindValue(SWTObservables.observeText(textSelectJCo3Archive, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, JCo3ImportSettings.JCO3_ARCHIVE_FILENAME), new UpdateValueStrategy().setAfterConvertValidator(new ArchiveNameValidator())	, new UpdateValueStrategy());
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
		context.bindValue(SWTObservables.observeText(textArchiveOs, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, JCo3ImportSettings.ARCHIVE_OS), new UpdateValueStrategy(), new UpdateValueStrategy());
		
		lblArchiveVersion = new Label(top, SWT.NONE);
		GridData gd_lblArchiveVersion = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblArchiveVersion.horizontalIndent = 10;
		lblArchiveVersion.setLayoutData(gd_lblArchiveVersion);
		lblArchiveVersion.setText(Messages.ArchiveSelectionPage_lblArchiveVersion_text);
		
		textArchiveVersion = new Text(top, SWT.BORDER | SWT.READ_ONLY);
		textArchiveVersion.setEnabled(false);
		textArchiveVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		context.bindValue(SWTObservables.observeText(textArchiveVersion, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, JCo3ImportSettings.ARCHIVE_VERSION), new UpdateValueStrategy(), new UpdateValueStrategy());
		
	}
	
	public boolean checkPageComplete() {
		return isJCo3ArchiveValid;
	}
	
	protected void getJCo3ArchiveFile() {
		String filename = getFile(textSelectJCo3Archive.getText());
        if (filename != null) {
            filename = filename.trim();
            if (filename.length() > 0) {
            	try {
					JCo3Archive jcoArchive = new JCo3Archive(filename);
					if (jcoArchive.getType() == JCoArchiveType.JCO_INVALID_ARCHIVE) {
						clearInput();
						setErrorMessage(Messages.JCo3ArchiveSelectionPage_UnsupportedJC03ArchiveFile);
						return;
					} 
					if (!jcoArchive.supportsCurrentPlatform()) {
						clearInput();
						setErrorMessage(MessageFormat.format(Messages.JCo3ArchiveSelectionPage_IncompatibleJC03ArchiveFileType, jcoArchive.getType().getDescription()));
						return;
					}
					try {
						ImportUtils.isJCoArchiveVersionSupported(jcoArchive.getVersion());
					} catch (UnsupportedVersionException e) {
						clearInput();
						setErrorMessage(MessageFormat.format(Messages.JCo3ArchiveSelectionPage_IncompatibleJCo3ArchiveFileVersion, e.getMessage()));
						return;
					}
					jcoImportSettings.setJco3Archive(jcoArchive);
					textSelectJCo3Archive.setText(filename);
				} catch (IOException e) {
					clearInput();
					setErrorMessage(MessageFormat.format(Messages.JCo3ArchiveSelectionPage_UnsupportedJCo3ArchiveFileFilename, e.getMessage()));
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
	
	protected void clearInput() {
		textSelectJCo3Archive.setText(BLANK_STRING);
		textArchiveOs.setText(BLANK_STRING);
		textArchiveVersion.setText(BLANK_STRING);
	}
	
}

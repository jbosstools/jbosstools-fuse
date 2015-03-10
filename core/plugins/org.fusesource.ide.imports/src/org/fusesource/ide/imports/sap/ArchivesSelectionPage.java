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
package org.fusesource.ide.imports.sap;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.imports.sap.ImportUtils.UnsupportedVersionException;
import org.fusesource.ide.imports.sap.JCo3Archive.JCoArchiveType;

public class ArchivesSelectionPage extends WizardPage {

	private static final String BLANK_STRING = ""; //$NON-NLS-1$

	private static final String[] FILE_EXTS = new String[] { "*.zip", "*.tgz", ".tar.gz", ".tar", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	
	private class JCo3ArchiveNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isJCo3ArchiveValid = false;
						return ValidationStatus.error(Messages.ArchivesSelectionPage_PleaseSelectJCo3Archive);
					}
				} else {
					throw new RuntimeException(Messages.ArchivesSelectionPage_InvalidJCo3ArchiveNameValue);
				}
				isJCo3ArchiveValid = true;
				return Status.OK_STATUS;
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}
	
	private class IDoc3ArchiveNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isIDoc3ArchiveValid = false;
						return ValidationStatus.error(Messages.ArchivesSelectionPage_PleaseSelectIDoc3Archive);
					}
				} else {
					throw new RuntimeException(Messages.ArchivesSelectionPage_InvalidIDoc3ArchiveNameValue);
				}
				isIDoc3ArchiveValid = true;
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
	
	private Label lblJCo3ArchiveOsPlatform;
	private Text textJCo3ArchiveOs;
	private Label lblJCo3ArchiveVersion;
	private Text textJCo3ArchiveVersion;
	
	private boolean isJCo3ArchiveValid;

	private IDoc3ImportSettings idoc3ImportSettings;
	
	private Text textSelectIDoc3Archive;
	private Button btnSelectIDoc3Archive;
	
	private Label lblIDoc3ArchiveVersion;
	private Text textIDoc3ArchiveVersion;
	
	private boolean isIDoc3ArchiveValid;
	private Label label;
	private Group grpSelectIDocArchive;
	private Group grpSelectJcoArchive;
	
	protected ArchivesSelectionPage(DataBindingContext context, JCo3ImportSettings jcoImportSettings, IDoc3ImportSettings idocImportSettings) {
		super(Messages.ArchivesSelectionPage_PageName, Messages.ArchivesSelectionPage_PageName, Activator.getDefault().getImageRegistry().getDescriptor(Activator.WALDO48_IMAGE));
		setDescription(Messages.ArchivesSelectionPage_SelectArchiveFilesContainingJCo3IDoc3Libs);
		setTitle(Messages.ArchivesSelectionPage_SelectJCo3IDoc3ArchiveToImport);
		this.context = context;
		this.jcoImportSettings = jcoImportSettings;
		this.idoc3ImportSettings = idocImportSettings;
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
		
		grpSelectJcoArchive = new Group(top, SWT.BORDER);
		grpSelectJcoArchive.setLayout(new GridLayout(3, false));
		grpSelectJcoArchive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 3, 1));
		grpSelectJcoArchive.setText(Messages.ArchivesSelectionPage_SelectJCo3ArchiveFile);
		
		Label lblSelectJCo3Archive = new Label(grpSelectJcoArchive, SWT.NONE);
		lblSelectJCo3Archive.setText(Messages.ArchivesSelectionPage_JCo3ArchiveFile);
		
		textSelectJCo3Archive = new Text(grpSelectJcoArchive, SWT.BORDER | SWT.READ_ONLY);
		textSelectJCo3Archive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textSelectJCo3Archive.setMessage(Messages.ArchivesSelectionPage_JCo3ArchivePath_text_message);
		binding = context.bindValue(SWTObservables.observeText(textSelectJCo3Archive, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, JCo3ImportSettings.JCO3_ARCHIVE_FILENAME), new UpdateValueStrategy().setAfterConvertValidator(new JCo3ArchiveNameValidator())	, new UpdateValueStrategy());
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		
		
		btnSelectJCo3Archive = new Button(grpSelectJcoArchive, SWT.NONE);
		btnSelectJCo3Archive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getJCo3ArchiveFile();
			}
		});
		btnSelectJCo3Archive.setText(Messages.ArchivesSelectionPage_Browse);
		
		lblJCo3ArchiveVersion = new Label(grpSelectJcoArchive, SWT.NONE);
		lblJCo3ArchiveVersion.setText(Messages.ArchivesSelectionPage_ArchiveVersion);
		
		textJCo3ArchiveVersion = new Text(grpSelectJcoArchive, SWT.BORDER | SWT.READ_ONLY);
		textJCo3ArchiveVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		textJCo3ArchiveVersion.setEnabled(false);
		context.bindValue(SWTObservables.observeText(textJCo3ArchiveVersion, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, JCo3ImportSettings.ARCHIVE_VERSION), new UpdateValueStrategy(), new UpdateValueStrategy());
		
		lblJCo3ArchiveOsPlatform = new Label(grpSelectJcoArchive, SWT.NONE);
		lblJCo3ArchiveOsPlatform.setText(Messages.ArchivesSelectionPage_ArchiveOSPlatform);
		
		textJCo3ArchiveOs = new Text(grpSelectJcoArchive, SWT.BORDER | SWT.READ_ONLY);
		textJCo3ArchiveOs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		textJCo3ArchiveOs.setEnabled(false);
		context.bindValue(SWTObservables.observeText(textJCo3ArchiveOs, SWT.Modify), BeansObservables.observeValue(jcoImportSettings, JCo3ImportSettings.ARCHIVE_OS), new UpdateValueStrategy(), new UpdateValueStrategy());
		
		label = new Label(top, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		
		grpSelectIDocArchive = new Group(top, SWT.BORDER);
		grpSelectIDocArchive.setLayout(new GridLayout(3, false));
		grpSelectIDocArchive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 3, 1));
		grpSelectIDocArchive.setText(Messages.ArchivesSelectionPage_SelectIDoc3ArchiveFile);
		
		Label lblSelectIDoc3Archive = new Label(grpSelectIDocArchive, SWT.NONE);
		lblSelectIDoc3Archive.setText(Messages.ArchivesSelectionPage_IDoc3ArchiveFile);
		
		textSelectIDoc3Archive = new Text(grpSelectIDocArchive, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_textSelectIDoc3Archive = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textSelectIDoc3Archive.widthHint = 230;
		textSelectIDoc3Archive.setLayoutData(gd_textSelectIDoc3Archive);
		textSelectIDoc3Archive.setMessage(Messages.ArchivesSelectionPage_IDoc3ArchivePath);
		binding = context.bindValue(SWTObservables.observeText(textSelectIDoc3Archive, SWT.Modify), BeansObservables.observeValue(idoc3ImportSettings, IDoc3ImportSettings.IDOC3_ARCHIVE_FILENAME), new UpdateValueStrategy().setAfterConvertValidator(new IDoc3ArchiveNameValidator())	, new UpdateValueStrategy());
		
		
		btnSelectIDoc3Archive = new Button(grpSelectIDocArchive, SWT.NONE);
		btnSelectIDoc3Archive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getIDoc3ArchiveFile();
			}
		});
		btnSelectIDoc3Archive.setText(Messages.ArchivesSelectionPage_Browse);
		
		lblIDoc3ArchiveVersion = new Label(grpSelectIDocArchive, SWT.NONE);
		lblIDoc3ArchiveVersion.setText(Messages.ArchivesSelectionPage_ArchiveVersion);
		
		textIDoc3ArchiveVersion = new Text(grpSelectIDocArchive, SWT.BORDER | SWT.READ_ONLY);
		textIDoc3ArchiveVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		textIDoc3ArchiveVersion.setEnabled(false);
		context.bindValue(SWTObservables.observeText(textIDoc3ArchiveVersion, SWT.Modify), BeansObservables.observeValue(idoc3ImportSettings, IDoc3ImportSettings.ARCHIVE_VERSION), new UpdateValueStrategy(), new UpdateValueStrategy());
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		
	}
	
	public boolean checkPageComplete() {
		return isJCo3ArchiveValid && isIDoc3ArchiveValid;
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
						setErrorMessage(Messages.ArchivesSelectionPage_UnsupportedJC03ArchiveFile);
						return;
					} 
					if (!jcoArchive.supportsCurrentPlatform()) {
						clearInput();
						setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_IncompatibleJC03ArchiveFileType, jcoArchive.getType().getDescription()));
						return;
					}
					try {
						ImportUtils.isJCoArchiveVersionSupported(jcoArchive.getVersion());
					} catch (UnsupportedVersionException e) {
						clearInput();
						setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_IncompatibleJCo3ArchiveFileVersion, e.getMessage()));
						return;
					}
					jcoImportSettings.setJco3Archive(jcoArchive);
					textSelectJCo3Archive.setText(filename);
				} catch (IOException e) {
					clearInput();
					setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_UnsupportedJCo3ArchiveFileFilename, e.getMessage()));
				}
			}
        }
	}
	
	protected void getIDoc3ArchiveFile() {
		String filename = getFile(textSelectIDoc3Archive.getText());
        if (filename != null) {
            filename = filename.trim();
            if (filename.length() > 0) {
            	try {
					IDoc3Archive idocArchive = new IDoc3Archive(filename);
					if (!idocArchive.isValid()) {
						clearInput();
						setErrorMessage(Messages.ArchivesSelectionPage_UnsupportedIDoc3ArchiveFile);
						return;
					}
					try {
						ImportUtils.isIDocArchiveVersionSupported(idocArchive.getVersion());
					} catch (UnsupportedVersionException e) {
						clearInput();
						setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_IncompatibleIDoc3ArchibeFileVersion, e.getMessage()));
						return;
					}
					idoc3ImportSettings.setIdoc3Archive(idocArchive);
					textSelectIDoc3Archive.setText(filename);
				} catch (IOException e) {
					clearInput();
					setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_UnsupportedIDoc3ArchiveFileFilename, e.getMessage()));
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
        try {
			String file = dialog.open();
			if (file != null) {
			    file = file.trim();
			    if (file.length() > 0) {
					return file;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        dialog = null;
        return null;
	}
	
	protected void clearInput() {
		textSelectJCo3Archive.setText(BLANK_STRING);
		textJCo3ArchiveOs.setText(BLANK_STRING);
		textJCo3ArchiveVersion.setText(BLANK_STRING);
		textSelectIDoc3Archive.setText(BLANK_STRING);
		textIDoc3ArchiveVersion.setText(BLANK_STRING);
	}
}

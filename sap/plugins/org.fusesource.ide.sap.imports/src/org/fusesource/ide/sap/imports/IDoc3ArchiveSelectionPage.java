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

public class IDoc3ArchiveSelectionPage extends WizardPage {
	
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final String[] FILE_EXTS = new String[] { "*.zip", "*.tgz", ".tar.gz", ".tar", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	
	private class ArchiveNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			try {
				if (value instanceof String) {
					String str = (String) value;
					if (str == null || str.length() == 0) {
						isIDoc3ArchiveValid = false;
						return ValidationStatus.error(Messages.IDoc3ArchiveSelectionPage_PleaseSelectIDoc3Archive);
					}
				} else {
					throw new RuntimeException(Messages.IDoc3ArchiveSelectionPage_InvalidIDoc3ArchiveNameValue);
				}
				isIDoc3ArchiveValid = true;
				return Status.OK_STATUS;
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}
	
	private DataBindingContext context; 

	private IDoc3ImportSettings idoc3ImportSettings;
	
	private Text textSelectIDoc3Archive;
	private Button btnSelectIDoc3Archive;
	
	private Label lblArchiveVersion;
	private Text textArchiveVersion;
	
	private boolean isIDoc3ArchiveValid;

	protected IDoc3ArchiveSelectionPage(DataBindingContext context, IDoc3ImportSettings importSettings) {
		super(Messages.IDoc3ArchiveSelectionPage_SelectIDoc3ArchiveFile, Messages.IDoc3ArchiveSelectionPage_SelectIDoc3ArchiveFile, Activator.getDefault().getImageRegistry().getDescriptor(Activator.FUSE_RS_IMAGE));
		setDescription(Messages.IDoc3ArchiveSelectionPage_SelectTheIDoc3ArchiveToImport);
		setTitle(Messages.IDoc3ArchiveSelectionPage_SelectIDoc3ArchiveToImport);
		this.context = context;
		this.idoc3ImportSettings = importSettings;
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
		lblSelectJCo3Archive.setText(Messages.IDoc3ArchiveSelectionPage_SelectIDoc3Archive);
		
		textSelectIDoc3Archive = new Text(top, SWT.BORDER | SWT.READ_ONLY);
		textSelectIDoc3Archive.setMessage(Messages.IDoc3ArchiveSelectionPage_IDoc3ArchivePath);
		textSelectIDoc3Archive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		binding = context.bindValue(SWTObservables.observeText(textSelectIDoc3Archive, SWT.Modify), BeansObservables.observeValue(idoc3ImportSettings, IDoc3ImportSettings.IDOC3_ARCHIVE_FILENAME), new UpdateValueStrategy().setAfterConvertValidator(new ArchiveNameValidator())	, new UpdateValueStrategy());
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		
		
		btnSelectIDoc3Archive = new Button(top, SWT.NONE);
		btnSelectIDoc3Archive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getJCo3ArchiveFile();
			}
		});
		btnSelectIDoc3Archive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSelectIDoc3Archive.setText(Messages.ArchiveSelectionPage_Browse_text);
		
		lblArchiveVersion = new Label(top, SWT.NONE);
		GridData gd_lblArchiveVersion = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblArchiveVersion.horizontalIndent = 10;
		lblArchiveVersion.setLayoutData(gd_lblArchiveVersion);
		lblArchiveVersion.setText(Messages.ArchiveSelectionPage_lblArchiveVersion_text);
		
		textArchiveVersion = new Text(top, SWT.BORDER | SWT.READ_ONLY);
		textArchiveVersion.setEnabled(false);
		textArchiveVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		context.bindValue(SWTObservables.observeText(textArchiveVersion, SWT.Modify), BeansObservables.observeValue(idoc3ImportSettings, IDoc3ImportSettings.ARCHIVE_VERSION), new UpdateValueStrategy(), new UpdateValueStrategy());
		
	}
	
	public boolean checkPageComplete() {
		return isIDoc3ArchiveValid;
	}
	
	protected void getJCo3ArchiveFile() {
		String filename = getFile(textSelectIDoc3Archive.getText());
        if (filename != null) {
            filename = filename.trim();
            if (filename.length() > 0) {
            	try {
					IDoc3Archive idocArchive = new IDoc3Archive(filename);
					if (!idocArchive.isValid()) {
						clearInput();
						setErrorMessage(Messages.IDoc3ArchiveSelectionPage_UnsupportedIDoc3ArchiveFile);
						return;
					}
					try {
						ImportUtils.isIDocArchiveVersionSupported(idocArchive.getVersion());
					} catch (UnsupportedVersionException e) {
						clearInput();
						setErrorMessage(MessageFormat.format(Messages.IDoc3ArchiveSelectionPage_IncompatibleIDoc3ArchibeFileVersion, e.getMessage()));
						return;
					}
					idoc3ImportSettings.setIdoc3Archive(idocArchive);
					textSelectIDoc3Archive.setText(filename);
				} catch (IOException e) {
					clearInput();
					setErrorMessage(MessageFormat.format(Messages.IDoc3ArchiveSelectionPage_UnsupportedIDoc3ArchiveFileFilename, e.getMessage()));
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
		textSelectIDoc3Archive.setText(EMPTY_STRING);
		textArchiveVersion.setText(EMPTY_STRING);
	}
}

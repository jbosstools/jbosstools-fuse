/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at https://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.fusesource.ide.imports.sap.ImportUtils.UnsupportedVersionException;
import org.fusesource.ide.imports.sap.JCo3Archive.JCoArchiveType;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;

public class ArchivesSelectionPage extends WizardPage {

	private static final String BLANK_STRING = ""; //$NON-NLS-1$

	private static final String[] FILE_EXTS = new String[] { "*.zip", "*.tgz", ".tar.gz", ".tar", "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	private class JCo3ArchiveNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			isJCo3ArchiveValid = false;
			try {
				if (value instanceof String) {
					String filename = (String) value;
					filename = filename.trim();
		            if (filename.length() > 0) {
		            	try {
							JCo3Archive jcoArchive = new JCo3Archive(filename);
							if (jcoArchive.getType() == JCoArchiveType.JCO_INVALID_ARCHIVE) {
								clearJCo3Inputs();
								setErrorMessage(Messages.ArchivesSelectionPage_UnsupportedJC03ArchiveFile);
								return ValidationStatus.error(getErrorMessage());
							}
							if (!jcoArchive.supportsCurrentPlatform()) {
								clearJCo3Inputs();
								setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_IncompatibleJC03ArchiveFileType, jcoArchive.getType().getDescription()));
								return ValidationStatus.error(getErrorMessage());
							}
							try {
								ImportUtils.isJCoArchiveVersionSupported(jcoArchive.getVersion());
							} catch (UnsupportedVersionException e) {
								clearJCo3Inputs();
								setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_IncompatibleJCo3ArchiveFileVersion, e.getMessage()));
								return ValidationStatus.error(getErrorMessage());
							}
							jcoImportSettings.setJco3Archive(jcoArchive);
							isJCo3ArchiveValid = true;
							setErrorMessage(null);
							setMessage(null);
							return ValidationStatus.ok();
						} catch (IOException e) {
							clearJCo3Inputs();
							setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_UnsupportedJCo3ArchiveFileFilename, e.getMessage()));
							return ValidationStatus.error(getErrorMessage());
						} finally {
							textSelectJCo3Archive.setText(filename);
						}
					} else {
						setMessage(Messages.ArchivesSelectionPage_PleaseSelectJCo3Archive, WizardPage.INFORMATION);
						return ValidationStatus.info(getMessage());
					}
				} else {
					setErrorMessage(Messages.ArchivesSelectionPage_InvalidJCo3ArchiveNameValue);
					return ValidationStatus.error(getErrorMessage());
				}
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}

	private class IDoc3ArchiveNameValidator implements IValidator {
		@Override
		public IStatus validate(Object value) {
			isIDoc3ArchiveValid = false;
			try {
				if (value instanceof String) {
					String filename = (String) value;
					filename = filename.trim();
		            if (filename.length() > 0) {
		            	try {
							IDoc3Archive idocArchive = new IDoc3Archive(filename);
							if (!idocArchive.isValid()) {
								clearIDoc3Inputs();
								setErrorMessage(Messages.ArchivesSelectionPage_UnsupportedIDoc3ArchiveFile);
								return ValidationStatus.error(getErrorMessage());
							}
							try {
								ImportUtils.isIDocArchiveVersionSupported(idocArchive.getVersion());
							} catch (UnsupportedVersionException e) {
								clearIDoc3Inputs();
								setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_IncompatibleIDoc3ArchibeFileVersion, e.getMessage()));
								return ValidationStatus.error(getErrorMessage());
							}
							idoc3ImportSettings.setIdoc3Archive(idocArchive);
							isIDoc3ArchiveValid = true;
							setMessage(null);
							setErrorMessage(null);
							return ValidationStatus.ok();
						} catch (IOException e) {
							clearIDoc3Inputs();
							setErrorMessage(MessageFormat.format(Messages.ArchivesSelectionPage_UnsupportedIDoc3ArchiveFileFilename, e.getMessage()));
							return ValidationStatus.error(getErrorMessage());
						} finally {
							textSelectIDoc3Archive.setText(filename);
						}
					} else {
						setMessage(Messages.ArchivesSelectionPage_PleaseSelectIDoc3Archive, WizardPage.INFORMATION);
						return ValidationStatus.info(getMessage());
					}
				} else {
					setErrorMessage(Messages.ArchivesSelectionPage_InvalidIDoc3ArchiveNameValue);
					return ValidationStatus.error(getErrorMessage());
				}
			} finally {
				setPageComplete(checkPageComplete());
			}
		}
	}

	private DataBindingContext context;

	private JCo3ImportSettings jcoImportSettings;

	private Text textSelectJCo3Archive;

	private Text textJCo3ArchiveOs;
	private Text textJCo3ArchiveVersion;

	private boolean isJCo3ArchiveValid;

	private IDoc3ImportSettings idoc3ImportSettings;

	private Text textSelectIDoc3Archive;

	private Text textIDoc3ArchiveVersion;

	private boolean isIDoc3ArchiveValid;

	protected ArchivesSelectionPage(DataBindingContext context, JCo3ImportSettings jcoImportSettings, IDoc3ImportSettings idocImportSettings) {
		super(Messages.ArchivesSelectionPage_PageName, Messages.ArchivesSelectionPage_PageName, Activator.getDefault().getImageRegistry().getDescriptor(Activator.SAP_TOOL_SUITE_48_IMAGE));
		setDescription(Messages.ArchivesSelectionPage_SelectArchiveFilesContainingJCo3IDoc3Libs);
		setTitle(Messages.ArchivesSelectionPage_SelectJCo3IDoc3ArchiveToImport);
		this.context = context;
		this.jcoImportSettings = jcoImportSettings;
		this.idoc3ImportSettings = idocImportSettings;
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout(3, false);
	    container.setLayout(layout);

	    Label lblJCo3Header = new Label(container, SWT.NONE);
	    lblJCo3Header.setText(Messages.ArchivesSelectionPage_SelectJCo3ArchiveFile);
	    GridDataFactory.fillDefaults().align(SWT.FILL,  SWT.TOP).span(3, 1).applyTo(lblJCo3Header);

		Label lblSelectJCo3Archive = new Label(container, SWT.NONE);
		lblSelectJCo3Archive.setText(Messages.ArchivesSelectionPage_JCo3ArchiveFile);

		textSelectJCo3Archive = new Text(container, SWT.BORDER);
		textSelectJCo3Archive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textSelectJCo3Archive.setMessage(Messages.ArchivesSelectionPage_JCo3ArchivePath_text_message);

		ISWTObservableValue uiObservable = WidgetProperties.text(SWT.Modify).observe(textSelectJCo3Archive);
		IObservableValue modelObservable = BeanProperties.value(JCo3ImportSettings.JCO3_ARCHIVE_FILENAME).observe(jcoImportSettings);

		// create UpdateValueStrategy and assign to the binding
        UpdateValueStrategy strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new JCo3ArchiveNameValidator());

        Binding bindingJCCo3 = context.bindValue(uiObservable, modelObservable, strategy, null);
		ControlDecorationSupport.create(bindingJCCo3, SWT.TOP | SWT.LEFT);

		Button btnSelectJCo3Archive = new Button(container, SWT.PUSH);
		btnSelectJCo3Archive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getJCo3ArchiveFile();
			}
		});
		btnSelectJCo3Archive.setText(Messages.ArchivesSelectionPage_Browse);

		Label lblJCo3ArchiveVersion = new Label(container, SWT.NONE);
		lblJCo3ArchiveVersion.setText(Messages.ArchivesSelectionPage_ArchiveVersion);

		textJCo3ArchiveVersion = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		textJCo3ArchiveVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		textJCo3ArchiveVersion.setEnabled(false);
		context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(300, textJCo3ArchiveVersion),
				BeanProperties.value(JCo3ImportSettings.ARCHIVE_VERSION).observe(jcoImportSettings), new UpdateValueStrategy(), new UpdateValueStrategy());

		Label lblJCo3ArchiveOsPlatform = new Label(container, SWT.NONE);
		lblJCo3ArchiveOsPlatform.setText(Messages.ArchivesSelectionPage_ArchiveOSPlatform);

		textJCo3ArchiveOs = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		textJCo3ArchiveOs.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		textJCo3ArchiveOs.setEnabled(false);
		context.bindValue(WidgetProperties.text(SWT.Modify).observe(textJCo3ArchiveOs), BeanProperties.value(JCo3ImportSettings.ARCHIVE_OS).observe(jcoImportSettings),
				new UpdateValueStrategy(), new UpdateValueStrategy());

		Label label = new Label(container, SWT.NONE);
		label.setText("");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 3));

		Label lblIDoc3Header = new Label(container, SWT.NONE);
		lblIDoc3Header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		lblIDoc3Header.setText(Messages.ArchivesSelectionPage_SelectIDoc3ArchiveFile);

		Label lblSelectIDoc3Archive = new Label(container, SWT.NONE);
		lblSelectIDoc3Archive.setText(Messages.ArchivesSelectionPage_IDoc3ArchiveFile);

		textSelectIDoc3Archive = new Text(container, SWT.BORDER);
		GridData gdTextSelectIDoc3Archive = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gdTextSelectIDoc3Archive.widthHint = 230;
		textSelectIDoc3Archive.setLayoutData(gdTextSelectIDoc3Archive);
		textSelectIDoc3Archive.setMessage(Messages.ArchivesSelectionPage_IDoc3ArchivePath);

		uiObservable = WidgetProperties.text(SWT.Modify).observe(textSelectIDoc3Archive);
		modelObservable = BeanProperties.value(IDoc3ImportSettings.IDOC3_ARCHIVE_FILENAME).observe(idoc3ImportSettings);

		// create UpdateValueStrategy and assign to the binding
        strategy = new UpdateValueStrategy();
        strategy.setBeforeSetValidator(new IDoc3ArchiveNameValidator());

		Binding bindingIDoc3 = context.bindValue(uiObservable, modelObservable, strategy, null);
		ControlDecorationSupport.create(bindingIDoc3, SWT.TOP | SWT.LEFT);

		Button btnSelectIDoc3Archive = new Button(container, SWT.NONE);
		btnSelectIDoc3Archive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getIDoc3ArchiveFile();
			}
		});
		btnSelectIDoc3Archive.setText(Messages.ArchivesSelectionPage_Browse);

		Label lblIDoc3ArchiveVersion = new Label(container, SWT.NONE);
		lblIDoc3ArchiveVersion.setText(Messages.ArchivesSelectionPage_ArchiveVersion);

		textIDoc3ArchiveVersion = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		textIDoc3ArchiveVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		textIDoc3ArchiveVersion.setEnabled(false);
		context.bindValue(WidgetProperties.text(SWT.Modify).observe(textIDoc3ArchiveVersion),
				BeanProperties.value(IDoc3ImportSettings.ARCHIVE_VERSION).observe(idoc3ImportSettings),
				new UpdateValueStrategy(), new UpdateValueStrategy());

	    setControl(container);

		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
	}

	public boolean checkPageComplete() {
		return isJCo3ArchiveValid && isIDoc3ArchiveValid;
	}

	protected void getJCo3ArchiveFile() {
		String filename = getFile(textSelectJCo3Archive.getText());
        if (filename != null) {
            textSelectJCo3Archive.setText(filename);
        }
	}

	protected void getIDoc3ArchiveFile() {
		String filename = getFile(textSelectIDoc3Archive.getText());
        if (filename != null) {
            textSelectIDoc3Archive.setText(filename);
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
			Activator.getDefault().getLog().log(new StatusFactory(Activator.PLUGIN_ID).errorStatus(e));
		}
        return null;
	}

	protected void clearJCo3Inputs() {
// we should keep what the user selected, its confusing to select something and end up having a blank text field but an error msg in top
//		textSelectJCo3Archive.setText(BLANK_STRING);
		textJCo3ArchiveOs.setText(BLANK_STRING);
		textJCo3ArchiveVersion.setText(BLANK_STRING);
	}

	protected void clearIDoc3Inputs() {
// we should keep what the user selected, its confusing to select something and end up having a blank text field but an error msg in top
//		textSelectIDoc3Archive.setText(BLANK_STRING);
		textIDoc3ArchiveVersion.setText(BLANK_STRING);
	}

	protected void clearInput() {
		clearJCo3Inputs();
		clearIDoc3Inputs();
	}
}

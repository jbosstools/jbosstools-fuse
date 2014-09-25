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
package org.fusesource.ide.sap.ui.export;

import java.io.File;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.converter.ExportFileType2ExportFileTypeComboSelectionConverter;
import org.fusesource.ide.sap.ui.converter.ExportFileTypeComboSelection2ExportFileTypeConverter;
import org.fusesource.ide.sap.ui.export.SapConnectionConfigurationExportSettings.ExportFileType;

public class SapConnectionConfigurationExportPage extends WizardPage {
	
	private static final String BLANK_STRING = ""; //$NON-NLS-1$

	private SapConnectionConfigurationExportSettings exportSettings;

	private DataBindingContext context; 

	private Text textSelectExportLocation;
	private Button btnSelectJCo3Archive;

	private CCombo exportFileTypeCombo;

	public SapConnectionConfigurationExportPage(DataBindingContext context, SapConnectionConfigurationExportSettings exportSettings) {
		super(Messages.SapConnectionConfigurationExportPage_SelectExportLocation, Messages.SapConnectionConfigurationExportPage_SelectExportLocationAndFileType, Activator.getDefault().getImageRegistry().getDescriptor(Activator.FUSE_RS_IMAGE));
		setDescription(Messages.SapConnectionConfigurationExportPage_BrowseToExportLocationAndSelectExportFileType);
		this.context = context;
		this.exportSettings = exportSettings;
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
		
		Label lblSelectExportLocation = new Label(top, SWT.NONE);
		GridData gd_lblSelectExportLocation = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectExportLocation.horizontalIndent = 10;
		lblSelectExportLocation.setLayoutData(gd_lblSelectExportLocation);
		lblSelectExportLocation.setText(Messages.SapConnectionConfigurationExportPage_ExportLocation);
		
		textSelectExportLocation = new Text(top, SWT.BORDER | SWT.READ_ONLY);
		textSelectExportLocation.setMessage(Messages.SapConnectionConfigurationExportPage_ExportLocation);
		textSelectExportLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		binding = context.bindValue(SWTObservables.observeText(textSelectExportLocation, SWT.Modify), BeansObservables.observeValue(exportSettings, "exportLocation"), new UpdateValueStrategy()	, new UpdateValueStrategy()); //$NON-NLS-1$
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		
		
		btnSelectJCo3Archive = new Button(top, SWT.NONE);
		btnSelectJCo3Archive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getExportDirectory();
			}
		});
		btnSelectJCo3Archive.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnSelectJCo3Archive.setText(Messages.SapConnectionConfigurationExportPage_Browse);
		
		Label lblSelectExportType = new Label(top, SWT.NONE);
		GridData gd_lblSelectExportType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblSelectExportType.horizontalIndent = 10;
		lblSelectExportType.setLayoutData(gd_lblSelectExportType);
		lblSelectExportType.setText(Messages.SapConnectionConfigurationExportPage_ExportFileType);

		exportFileTypeCombo = new CCombo(top, SWT.READ_ONLY);
		exportFileTypeCombo.setToolTipText(Messages.SapConnectionConfigurationExportPage_ExportFileType);
		exportFileTypeCombo.setItems(new String[] {ExportFileType.BLUEPRINT.getDisplay(), ExportFileType.SPRING.getDisplay()});
		exportFileTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		exportFileTypeCombo.select(0);

		IObservableValue observeSingleSelectionExportFileTypeComboObserveWidget = WidgetProperties.singleSelectionIndex().observe(exportFileTypeCombo);
		IObservableValue exportFileTypeObserveValue = BeansObservables.observeValue(exportSettings, "exportFileType"); //$NON-NLS-1$
		UpdateValueStrategy combo2ExportFileTypeStrategy = new UpdateValueStrategy();
		combo2ExportFileTypeStrategy.setConverter(new ExportFileTypeComboSelection2ExportFileTypeConverter());
		UpdateValueStrategy exportFileType2ComboStrategy = new UpdateValueStrategy();
		exportFileType2ComboStrategy.setConverter(new ExportFileType2ExportFileTypeComboSelectionConverter());
		context.bindValue(observeSingleSelectionExportFileTypeComboObserveWidget, exportFileTypeObserveValue, combo2ExportFileTypeStrategy, exportFileType2ComboStrategy);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
	}

	protected void getExportDirectory() {
		String directoryName = getDirectory(textSelectExportLocation.getText());
		if (directoryName != null) {
			directoryName.trim();
			
			// Check that directory is writable
			File exportDirectory = new File(directoryName);
			if (exportDirectory.canWrite()) {
				exportSettings.setExportLocation(directoryName);
				textSelectExportLocation.setText(directoryName);
				setPageComplete(true);
			} else {
				setPageComplete(false);
				clearInput();
				setErrorMessage(Messages.SapConnectionConfigurationExportPage_CanNotWriteToThisLocation);
			}
		}
	}
	
	protected String getDirectory(String startingDirectoryName) {
		setErrorMessage(null);
		
		DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN | SWT.SHEET);
		
        File startingDirectory = new File(startingDirectoryName);
		if (startingDirectory.exists()) {
			dialog.setFilterPath(startingDirectory.getPath());
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
	
	protected void clearInput() {
		textSelectExportLocation.setText(BLANK_STRING);
	}

	
}

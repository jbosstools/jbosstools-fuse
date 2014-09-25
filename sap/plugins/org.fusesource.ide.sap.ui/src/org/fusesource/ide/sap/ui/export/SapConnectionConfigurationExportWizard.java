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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.sap.ui.Messages;
import org.fusesource.ide.sap.ui.jaxb.SapConnectionConfiguration;
import org.fusesource.ide.sap.ui.jaxb.SapConnectionConfigurationBuilder;
import org.fusesource.ide.sap.ui.jaxb.blueprint.BlueprintFile;
import org.fusesource.ide.sap.ui.jaxb.spring.SpringFile;
import org.fusesource.ide.sap.ui.util.ModelUtil;
import org.fusesource.ide.sap.ui.view.SapConnectionsView;

public class SapConnectionConfigurationExportWizard extends Wizard implements IExportWizard {
	
	public static final String ID = "org.fusesource.ide.sap.ui.SapConnectionConfigurationExportWizard"; //$NON-NLS-1$
	
	private static final String EXPORT_FILENAME = Messages.SapConnectionConfigurationExportWizard_ExportFilename;
	
	private static final String FILENAME_EXTENSION = ".xml"; //$NON-NLS-1$
	
	private DataBindingContext context;
	private SapConnectionConfigurationExportSettings exportSettings;
	private SapConnectionConfigurationExportPage exportPage;


	public SapConnectionConfigurationExportWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.SapConnectionConfigurationExportWizard_WindowTitle);
		setNeedsProgressMonitor(true);
		
		exportSettings = new SapConnectionConfigurationExportSettings();
		
		context = new DataBindingContext();
		exportPage = new SapConnectionConfigurationExportPage(context, exportSettings);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(exportPage);
	}
	
	@Override
	public boolean canFinish() {
		return super.canFinish();
	}

	@Override
	public boolean performFinish() {
		try {
			switch (exportSettings.getExportFileType()) {
			case BLUEPRINT:
				exportBlueprintFile();
				return true;
			case SPRING:
				exportSpringFile();
				return true;
			default:
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	protected FileOutputStream getFilename() throws FileNotFoundException {
		File directory = new File(exportSettings.getExportLocation());
		
		// Search directory for unused version of export filename
		String exportFilename;
		for (int i = 0; ; i++) {
			final String filename = exportFilename = EXPORT_FILENAME + (i == 0 ? "" : i) + FILENAME_EXTENSION; //$NON-NLS-1$
			// Look for the 'filename' in directory
			String[] files = directory.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.equals(filename)) {
						return true;
					}
					return false;
				}
			});
			if (files.length == 0) {
				// 'filename' is not in directory: found one we can use.
				break;
			}
		}
		
		FileOutputStream fos = new FileOutputStream(exportSettings.getExportLocation() + File.separator + exportFilename);
		
		return fos;
	}
	
	protected void exportBlueprintFile() throws Exception {
		SapConnectionConfiguration sapConnectionConfiguration = new SapConnectionConfiguration();
		SapConnectionConfigurationBuilder.populateSapConnectionConfiguration(getSapConnectionConfigurationModel(), sapConnectionConfiguration);
		BlueprintFile blueprintFile = new BlueprintFile();
		blueprintFile.setSapConnectionConfiguration(sapConnectionConfiguration);
		FileOutputStream fos = getFilename();
		blueprintFile.marshal(fos);
	}
	
	protected void exportSpringFile() throws Exception {
		SapConnectionConfiguration sapConnectionConfiguration = new SapConnectionConfiguration();
		SapConnectionConfigurationBuilder.populateSapConnectionConfiguration(getSapConnectionConfigurationModel(), sapConnectionConfiguration);
		SpringFile springFile = new SpringFile();
		springFile.setSapConnectionConfiguration(sapConnectionConfiguration);
		FileOutputStream fos = getFilename();
		springFile.marshal(fos);
	}
	
	protected org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration getSapConnectionConfigurationModel() {
		SapConnectionsView view = (SapConnectionsView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(SapConnectionsView.ID);
		if (view != null) {
			return view.getSapConnectionConfiguration();
		}
		// SapConnectionsView is not active: retrieve stored configuration instead.
		return ModelUtil.getModel(new ResourceSetImpl());
	}
	
}

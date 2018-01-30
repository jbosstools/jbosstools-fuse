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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * Install Wizard for SAP Tool Suite
 * 
 * @author William Collins punkhornsw@gmail.com
 *
 */
public class SAPToolSuiteImportWizard extends Wizard implements IImportWizard {
	
	private static final String SAP_I_DOC_LIBRARY_VERSION_3 = "SAP IDoc Library version 3"; //$NON-NLS-1$
	
	private static final String SAP_JAVA_CONNECTOR_VERSION_3 = "SAP Java Connector version 3"; //$NON-NLS-1$
	
	private static final String RED_HAT_INC = "Red Hat, Inc."; //$NON-NLS-1$
	
	private DataBindingContext context;
	private JCo3ImportSettings jco3ImportSettings;
	private IDoc3ImportSettings idoc3ImportSettings;
	private static SapLibrariesFeatureArchive sapLibrariesFeatureArchive;

	private InstallOverviewPage downloadPage;
	private ArchivesSelectionPage archivesSelectionPage;

	public SAPToolSuiteImportWizard() {
		super();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.SAPToolSuiteImportWizard_WindowTitle);
		setNeedsProgressMonitor(true);
		
		int executionEnvironmentIndex = ImportUtils.getExecutionEnvironmentIndex(ImportUtils.DEFAULT_EXECUTION_ENVIRONMENT);
		
		jco3ImportSettings = new JCo3ImportSettings();
		jco3ImportSettings.setBundleDeployLocation(ImportUtils.getPluginsFolder());
		jco3ImportSettings.setBundleName(SAP_JAVA_CONNECTOR_VERSION_3);
		jco3ImportSettings.setBundleVendor(RED_HAT_INC);
		jco3ImportSettings.setRequiredExecutionEnvironmentIndex(executionEnvironmentIndex);
		
		idoc3ImportSettings = new IDoc3ImportSettings();
		idoc3ImportSettings.setBundleDeployLocation(ImportUtils.getPluginsFolder());
		idoc3ImportSettings.setBundleName(SAP_I_DOC_LIBRARY_VERSION_3);
		idoc3ImportSettings.setBundleVendor(RED_HAT_INC);
		idoc3ImportSettings.setRequiredExecutionEnvironmentIndex(executionEnvironmentIndex);
		
		sapLibrariesFeatureArchive = new SapLibrariesFeatureArchive();
		sapLibrariesFeatureArchive.setJco3ImportSettings(jco3ImportSettings);
		sapLibrariesFeatureArchive.setIdoc3ImportSettings(idoc3ImportSettings);

		context = new DataBindingContext();
		downloadPage = new InstallOverviewPage();
		archivesSelectionPage = new ArchivesSelectionPage(context, jco3ImportSettings, idoc3ImportSettings);
	
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(downloadPage);
		addPage(archivesSelectionPage);
	}
	
	@Override
	public boolean performFinish() {
		context.updateModels();
		try {
			
			getContainer().run(false, true, new SapToolSuiteInstaller(sapLibrariesFeatureArchive, jco3ImportSettings, idoc3ImportSettings));

			return true;

		} catch (InvocationTargetException e) {
			ErrorDialog.openError(getShell(), Messages.SAPToolSuiteImportWizard_SAPImportErrorTitle, e.getLocalizedMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.SAPToolSuiteImportWizard_SAPImportErrorMessage, e));
			return false;
		} catch (InterruptedException e) {
			MessageDialog.openWarning(getShell(), Messages.SAPToolSuiteImportWizard_SAPImportCancelledTitle, Messages.SAPToolSuiteImportWizard_SAPImportCancelledMessage);
			Thread.currentThread().interrupt();
			return false;
		} 
	}
	
	@Override
	public boolean performCancel() {
		ImportUtils.deleteTemporarySapLibrariesRepository();
		return true;
	}

}

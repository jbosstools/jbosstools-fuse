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
import org.eclipse.ui.PlatformUI;

public class SAPImportWizard extends Wizard implements IImportWizard {
	
	private static final String SAP_I_DOC_LIBRARY_VERSION_3 = "SAP IDoc Library version 3"; //$NON-NLS-1$
	
	private static final String SAP_JAVA_CONNECTOR_VERSION_3 = "SAP Java Connector version 3"; //$NON-NLS-1$
	
	private static final String RED_HAT_INC = "Red Hat, Inc."; //$NON-NLS-1$
	
	private DataBindingContext context;
	private JCo3ImportSettings jco3ImportSettings;
	private IDoc3ImportSettings idoc3ImportSettings;
	private DownloadPage downloadPage;
	private JCo3ArchiveSelectionPage jco3ArchiveSelectionPage;
	private IDoc3ArchiveSelectionPage idoc3ArchiveSelectionPage;

	public SAPImportWizard() {
		super();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(Messages.JCoImportWizard_WindowTitle);
		setNeedsProgressMonitor(true);
		
		int executionEnvironmentIndex = ImportUtils.getExecutionEnvironmentIndex(ImportUtils.DEFAULT_EXECUTION_ENVIRONMENT);
		
		jco3ImportSettings = new JCo3ImportSettings();
		jco3ImportSettings.setBundleDeployLocation(ImportUtils.getDefaultDeployLocation());
		jco3ImportSettings.setBundleName(SAP_JAVA_CONNECTOR_VERSION_3);
		jco3ImportSettings.setBundleVendor(RED_HAT_INC);
		jco3ImportSettings.setRequiredExecutionEnvironmentIndex(executionEnvironmentIndex);
		
		idoc3ImportSettings = new IDoc3ImportSettings();
		idoc3ImportSettings.setBundleDeployLocation(ImportUtils.getDefaultDeployLocation());
		idoc3ImportSettings.setBundleName(SAP_I_DOC_LIBRARY_VERSION_3);
		idoc3ImportSettings.setBundleVendor(RED_HAT_INC);
		idoc3ImportSettings.setRequiredExecutionEnvironmentIndex(executionEnvironmentIndex);
		
		context = new DataBindingContext();
		downloadPage = new DownloadPage();
		jco3ArchiveSelectionPage = new JCo3ArchiveSelectionPage(context, jco3ImportSettings);
		idoc3ArchiveSelectionPage = new IDoc3ArchiveSelectionPage(context, idoc3ImportSettings);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(downloadPage);
		addPage(jco3ArchiveSelectionPage);
		addPage(idoc3ArchiveSelectionPage);
	}

	@Override
	public boolean canFinish() {
		return super.canFinish();
	}
	
	@Override
	public boolean performFinish() {
		context.updateModels();
		try {
			
			// Check if deploy location needs to be created
			File file = new File(jco3ImportSettings.getBundleDeployLocation());
			if (!file.exists()) {
				boolean ok = MessageDialog.openConfirm(getShell(), Messages.SAPImportWizard_DeployLocationDoesNotExistTitle, Messages.SAPImportWizard_DeployLocationDoesNotExistMessage);
				if (!ok) {
					return false;
				}
				file.mkdir();
			}
			
			// Check for overwrite of existing bundles
			File jcoBundle = new File(jco3ImportSettings.getBundleFilename());
			File nativeBundle = new File(jco3ImportSettings.getFragmentFilename());
			File idoc3Bundle = new File(idoc3ImportSettings.getBundleFilename());
			if (jcoBundle.exists() || nativeBundle.exists() || idoc3Bundle.exists()) {
				boolean ok = MessageDialog.openConfirm(getShell(), Messages.SAPImportWizard_OverwriteExistingBundlesTitle, Messages.SAPImportWizard_OverwriteExistingBundlesMessage);
				if (!ok) {
					return false;
				}
				jcoBundle.delete();
				nativeBundle.delete();
				idoc3Bundle.delete();
			}
			
			getContainer().run(true, true, new SAPPluginsBuilder(jco3ImportSettings, idoc3ImportSettings));
			if (MessageDialog.openConfirm(getShell(), Messages.SAPImportWizard_RestartEclipseTitle, Messages.SAPImportWizard_RestartEclipseMessage)) {
				PlatformUI.getWorkbench().restart();
			}
			return true;
		} catch (InvocationTargetException e) {
			ErrorDialog.openError(getShell(), Messages.SAPImportWizard_SAPImportErrorTitle, e.getLocalizedMessage(), new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.SAPImportWizard_SAPImportErrorMessage, e));
			return false;
		} catch (InterruptedException e) {
			MessageDialog.openWarning(getShell(), Messages.SAPImportWizard_SAPImportCancelledTitle, Messages.SAPImportWizard_SAPImportCancelledMessage);
			
			// Remove any partially imported bundles
			File jcoBundle = new File(jco3ImportSettings.getBundleFilename());
			File nativeBundle = new File(jco3ImportSettings.getFragmentFilename());
			File idoc3Bundle = new File(idoc3ImportSettings.getBundleFilename());
			jcoBundle.delete();
			nativeBundle.delete();
			idoc3Bundle.delete();
			
			return false;
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.restconfiguration.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.editor.restconfiguration.RestConfigEditor;
import org.fusesource.ide.camel.editor.restconfiguration.wizards.pages.RestVerbDefinitionPage;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;

/**
 * @author brianf
 *
 */
public class AddRestOperationWizard extends Wizard implements INewWizard {

	private String verbTypeToCreate = RestVerbElement.GET_VERB; // default to GET
	private RestConfigEditor editor;
	private RestVerbDefinitionPage verbPage;
	
	/**
	 * Constructor
	 */
	public AddRestOperationWizard(RestConfigEditor editor) {
		super();
		this.editor = editor;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(UIMessages.addRESTOperationWizardWindowTitle);
		getShell().setText(UIMessages.addRESTOperationWizardWindowTitle);
		setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						editor.createRestOperation(
								verbPage.getVerbTypeToCreate(),
								verbPage.getUriValue(),
								verbPage.getId());
					} catch (Exception e) {
						CamelEditorUIActivator.pluginLog().logError(e);
						ErrorDialog.openError(
								getShell(),
								UIMessages.addRESTOperationWizardErrorTitle,
								UIMessages.addRESTOperationWizardErrorMessage,
								new Status(IStatus.ERROR, CamelEditorUIActivator.PLUGIN_ID, e.getMessage(), e));
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException e) {
			CamelEditorUIActivator.pluginLog().logError(e);
			return false;
		} catch (InterruptedException e) {
			CamelEditorUIActivator.pluginLog().logError(e);
			Thread.currentThread().interrupt();
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		verbPage = new RestVerbDefinitionPage("first"); //$NON-NLS-1$
		this.addPage(verbPage);
	}

	public String getVerbTypeToCreate() {
		return verbTypeToCreate;
	}

}

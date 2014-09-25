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
package org.fusesource.ide.sap.ui.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.fusesource.ide.sap.ui.export.SapConnectionConfigurationExportWizard;

public class ExportWizardHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(SapConnectionConfigurationExportWizard.ID);
		if (descriptor != null) {

			try {
				IWorkbenchWizard wizard = descriptor.createWizard();
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				ISelection selection = HandlerUtil.getCurrentSelection(event);
				if (!(selection instanceof IStructuredSelection)) {
					selection = StructuredSelection.EMPTY;
				}
				wizard.init(PlatformUI.getWorkbench(), (StructuredSelection) selection);
				WizardDialog wizardDialog = new WizardDialog(window.getShell(), wizard);
				wizardDialog.setTitle(wizard.getWindowTitle());
				wizardDialog.create();
				wizardDialog.open();
			} catch (CoreException e) {
				// Ignore
			}
			
		}
		return null;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		
	}

}

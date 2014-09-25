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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.dialog.TestDestinationDialog;
import org.fusesource.ide.sap.ui.dialog.TestServerDialog;
import org.fusesource.ide.sap.ui.export.SapConnectionConfigurationExportWizard;

public class TestHandler extends AbstractHandler implements IHandler {

	private boolean isDestination;
	private String name;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getExportWizardRegistry().findWizard(SapConnectionConfigurationExportWizard.ID);
		if (descriptor != null) {

			try {
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				if (isDestination) {
					TestDestinationDialog testDestinationDialog = new TestDestinationDialog(window.getShell(), name);
					testDestinationDialog.open();
				} else {
					TestServerDialog testServerDialog = new TestServerDialog(window.getShell(), name);
					testServerDialog.open();
				}
			} catch (Exception e) {
				// Ignore
			}
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		setBaseEnabled(false);
		Object obj = HandlerUtil.getVariable(evaluationContext, ISources.ACTIVE_CURRENT_SELECTION_NAME);
		if (obj instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) obj;
			if (selection.size() == 1) {
				obj = selection.getFirstElement();
				if (obj instanceof DestinationDataStoreEntryImpl) {
					name = ((DestinationDataStoreEntryImpl) obj).getKey();
					isDestination = true;
					setBaseEnabled(true);
				} else if (obj instanceof ServerDataStoreEntryImpl) {
					name = ((ServerDataStoreEntryImpl) obj).getKey();
					isDestination = false;
					setBaseEnabled(true);
				}
			}
		}
	}

}

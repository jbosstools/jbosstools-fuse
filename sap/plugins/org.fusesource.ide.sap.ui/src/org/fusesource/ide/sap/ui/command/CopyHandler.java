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
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;

public class CopyHandler extends AbstractHandler implements IHandler {

	private EditingDomain editingDomain;
	private Command command;
	private boolean isServerDataStoreEntry;
	private ServerDataStoreEntryImpl serverDataStoreEntry;
	private DestinationDataStoreEntryImpl destinationDataStoreEntry;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		editingDomain.getCommandStack().execute(command);
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
				if (obj instanceof EObject) {
					EObject eObject = (EObject) obj;
					editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(eObject);
					if (editingDomain != null) {
						if (canCopy(selection)) {
							if (isServerDataStoreEntry) {
								ServerDataStore serverDataStore = (ServerDataStore) serverDataStoreEntry.eContainer();
								command = new CopyServerDataStoreEntryToClipboard(editingDomain, serverDataStore, serverDataStoreEntry);
							} else {
								DestinationDataStore destinationDataStore = (DestinationDataStore) destinationDataStoreEntry.eContainer();
								command = new CopyDestinationDataStoreEntryToClipboard(editingDomain, destinationDataStore, destinationDataStoreEntry);
							}
							setBaseEnabled(true);
						}
					}
				}
			}
		}
	}

	protected boolean canCopy(IStructuredSelection selection) {
		if (selection.size() == 1) {
			if (selection.getFirstElement() instanceof ServerDataStoreEntryImpl) {
				for (Object obj: selection.toList()) {
					if (!(obj instanceof ServerDataStoreEntryImpl)) {
						return false;
					}
					serverDataStoreEntry = (ServerDataStoreEntryImpl) obj;
					isServerDataStoreEntry = true;
				}
				return true;
			} else if (selection.getFirstElement() instanceof DestinationDataStoreEntryImpl) {
				for (Object obj: selection.toList()) {
					if (!(obj instanceof DestinationDataStoreEntryImpl)) {
						return false;
					}
					destinationDataStoreEntry = (DestinationDataStoreEntryImpl) obj;
					isServerDataStoreEntry = false;
				}
				return true;
			}
		}
		return false;
	}
}

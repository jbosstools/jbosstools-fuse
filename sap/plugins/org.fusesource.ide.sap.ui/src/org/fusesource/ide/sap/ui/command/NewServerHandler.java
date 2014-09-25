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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.ServerData;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.dialog.ServerDialog;
import org.fusesource.ide.sap.ui.edit.command.TransactionalCommandStack;

public class NewServerHandler extends AbstractHandler implements IHandler {

	private EditingDomain editingDomain;
	private CompoundCommand command;
	private ServerDataStoreEntryImpl serverDataStoreEntry;
	private ServerDataStore serverDataStore;
	private ServerData serverData;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		((TransactionalCommandStack)editingDomain.getCommandStack()).begin();
		ServerDialog newNameDialog = new ServerDialog(window.getShell(), ServerDialog.Type.CREATE, editingDomain, serverDataStore, serverDataStoreEntry);
		int status = newNameDialog.open();
		if (status != Window.OK) {
	    	((TransactionalCommandStack)editingDomain.getCommandStack()).rollback();
	    	return null;
		}
		editingDomain.getCommandStack().execute(command);
	    ((TransactionalCommandStack)editingDomain.getCommandStack()).commit();
		return null;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		Command createServerDataStoreEntryCommand = null;
		Command createServerDataCommand = null;
		setBaseEnabled(false);
		Object obj = HandlerUtil.getVariable(evaluationContext, ISources.ACTIVE_CURRENT_SELECTION_NAME);
		if (obj instanceof IStructuredSelection) {
			obj = ((IStructuredSelection)obj).getFirstElement();
			if (obj instanceof ServerDataStore){
				serverDataStore = (ServerDataStore) obj;
				editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(serverDataStore);
				if (editingDomain != null) {
					Collection<?> descriptors = editingDomain.getNewChildDescriptors(serverDataStore, null);
					for (Object descriptor: descriptors) {
						CommandParameter parameter = (CommandParameter) descriptor;
						if (parameter.getFeature() == RfcPackage.Literals.SERVER_DATA_STORE__ENTRIES) {
							serverDataStoreEntry = (ServerDataStoreEntryImpl) parameter.getValue();
							createServerDataStoreEntryCommand = CreateChildCommand.create(editingDomain, serverDataStore, descriptor, Collections.singletonList(serverDataStore));
							continue;
						} else if (parameter.getFeature() == RfcPackage.Literals.SERVER_DATA_STORE__SERVER_DATA) {
							serverData = (ServerData) parameter.getValue();
							createServerDataCommand = CreateChildCommand.create(editingDomain, serverDataStore, descriptor, Collections.singletonList(serverDataStore));
							continue;
						}

					}
					if(createServerDataStoreEntryCommand != null && createServerDataCommand != null) {
						command = new CompoundCommand();
						command.append(createServerDataCommand);
						command.append(SetCommand.create(editingDomain, serverDataStoreEntry, RfcPackage.Literals.SERVER_DATA_STORE_ENTRY__VALUE, serverData));
						command.append(createServerDataStoreEntryCommand);
						setBaseEnabled(true);
					}
				}
				
			}
		}		
	}
	
}

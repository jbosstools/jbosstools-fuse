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

import java.util.Collections;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.CutToClipboardCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;

public class CutHandler extends AbstractHandler implements IHandler {

	private EditingDomain editingDomain;
	private CompoundCommand command;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		editingDomain.getCommandStack().execute(command);
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		Command cutToClipboardCommand = null;
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
						cutToClipboardCommand = CutToClipboardCommand.create(editingDomain, Collections.singletonList(eObject));
						if (canCut(selection)) {
							command = new CompoundCommand();
							command.append(cutToClipboardCommand);
							command.append(createRemoveValueCommand(obj));
							setBaseEnabled(true);
						}
					}
				}
			}
		}
	}
	
	protected Command createRemoveValueCommand(Object value) {
		if (value instanceof ServerDataStoreEntryImpl) {
			ServerDataStoreEntryImpl entry = (ServerDataStoreEntryImpl) value;
			return RemoveCommand.create(editingDomain, entry.eContainer(), RfcPackage.Literals.SERVER_DATA_STORE__SERVER_DATA, entry.getValue()); 
		} else if (value instanceof DestinationDataStoreEntryImpl) {
			DestinationDataStoreEntryImpl entry = (DestinationDataStoreEntryImpl) value;
			return RemoveCommand.create(editingDomain, entry.eContainer(), RfcPackage.Literals.DESTINATION_DATA_STORE__DESTINATION_DATA, entry.getValue()); 
		}
		return null;
	}

	protected boolean canCut(IStructuredSelection selection) {
		if (selection.size() == 1) {
			if (selection.getFirstElement() instanceof ServerDataStoreEntryImpl) {
				for (Object obj: selection.toList()) {
					if (!(obj instanceof ServerDataStoreEntryImpl)) {
						return false;
					}
				}
				return true;
			} else if (selection.getFirstElement() instanceof DestinationDataStoreEntryImpl) {
				for (Object obj: selection.toList()) {
					if (!(obj instanceof DestinationDataStoreEntryImpl)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
}

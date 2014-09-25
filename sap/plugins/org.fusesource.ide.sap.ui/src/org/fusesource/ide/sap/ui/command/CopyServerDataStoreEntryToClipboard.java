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

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.RfcFactoryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;

public class CopyServerDataStoreEntryToClipboard extends ChangeCommand {
	
	protected EditingDomain domain;
	protected ServerDataStore serverDataStore;
	protected ServerDataStoreEntryImpl serverDataStoreEntry;
	protected ServerDataStoreEntryImpl copyServerDataStoreEntry;
	protected Collection<Object> oldClipboard;

	public CopyServerDataStoreEntryToClipboard(EditingDomain domain, ServerDataStore serverDataStore, ServerDataStoreEntryImpl serverDataStoreEntry) {
		super(serverDataStore);
		this.domain = domain;
		this.serverDataStore = serverDataStore;
		this.serverDataStoreEntry = serverDataStoreEntry;
	}


	@Override
	protected void doExecute() {
		copyServerDataStoreEntry = (ServerDataStoreEntryImpl) ((RfcFactoryImpl)RfcFactory.eINSTANCE).createServerDataStoreEntry();
		copyServerDataStoreEntry.setKey(serverDataStoreEntry.getKey());
		copyServerDataStoreEntry.setValue(EcoreUtil.copy(serverDataStoreEntry.getValue()));
		oldClipboard = domain.getClipboard();
		domain.setClipboard(Collections.singleton((Object) copyServerDataStoreEntry));
	}
	
	@Override
	public void undo() {
		super.undo();
		domain.setClipboard(oldClipboard);		
	}
	
	@Override
	public void redo() {
		super.redo();
		oldClipboard = domain.getClipboard();
		domain.setClipboard(Collections.singleton((Object) copyServerDataStoreEntry));
	}

}

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
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.RfcFactoryImpl;

public class CopyDestinationDataStoreEntryToClipboard extends ChangeCommand {
	
	protected EditingDomain domain;
	protected DestinationDataStore destinationDataStore;
	protected DestinationDataStoreEntryImpl destinationDataStoreEntry;
	protected DestinationDataStoreEntryImpl copyDestinationDataStoreEntry;
	protected Collection<Object> oldClipboard;

	public CopyDestinationDataStoreEntryToClipboard(EditingDomain domain, DestinationDataStore destinationDataStore, DestinationDataStoreEntryImpl destinationDataStoreEntry) {
		super(destinationDataStore);
		this.domain = domain;
		this.destinationDataStore = destinationDataStore;
		this.destinationDataStoreEntry = destinationDataStoreEntry;
	}


	@Override
	protected void doExecute() {
		copyDestinationDataStoreEntry = (DestinationDataStoreEntryImpl) ((RfcFactoryImpl)RfcFactory.eINSTANCE).createDestinationDataStoreEntry();
		copyDestinationDataStoreEntry.setKey(destinationDataStoreEntry.getKey());
		copyDestinationDataStoreEntry.setValue(EcoreUtil.copy(destinationDataStoreEntry.getValue()));
		oldClipboard = domain.getClipboard();
		domain.setClipboard(Collections.singleton((Object) copyDestinationDataStoreEntry));
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
		domain.setClipboard(Collections.singleton((Object) copyDestinationDataStoreEntry));
	}

}

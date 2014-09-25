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
package org.fusesource.ide.sap.ui.properties;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.fusesource.camel.component.sap.model.rfc.DestinationDataStore;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.camel.component.sap.model.rfc.ServerDataStore;
import org.fusesource.camel.component.sap.model.rfc.impl.DestinationDataStoreEntryImpl;
import org.fusesource.camel.component.sap.model.rfc.impl.ServerDataStoreEntryImpl;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;

public class PropertyLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof TreeSelection) {
			element = ((TreeSelection) element).getFirstElement();
			if (element instanceof SapConnectionConfiguration) {
				return Activator.getDefault().getImageRegistry().get(Activator.SAP_CONNECTION_CONFIGURATION);
			} else if (element instanceof DestinationDataStore) {
				return Activator.getDefault().getImageRegistry().get(Activator.DESTINATION_DATA_STORE_IMAGE);
			} else if (element instanceof DestinationDataStoreEntryImpl) {
				return Activator.getDefault().getImageRegistry().get(Activator.DESTINATION_DATA_STORE_ENTRY_IMAGE);
			} else if (element instanceof ServerDataStore) {
				return Activator.getDefault().getImageRegistry().get(Activator.SERVER_DATA_STORE_IMAGE);
			} else if (element instanceof ServerDataStoreEntryImpl) {
				return Activator.getDefault().getImageRegistry().get(Activator.SERVER_DATA_STORE_ENTRY_IMAGE);
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof TreeSelection) {
			element = ((TreeSelection) element).getFirstElement();
			if (element instanceof SapConnectionConfiguration) {
				return Messages.PropertyLabelProvider_SapConnectionConfiguration;
			} else if (element instanceof DestinationDataStore) {
				return Messages.PropertyLabelProvider_DestinationDataStore;
			} else if (element instanceof DestinationDataStoreEntryImpl) {
				return NLS.bind(Messages.PropertyLabelProvider_DestinationData, ((DestinationDataStoreEntryImpl)element).getKey());
			} else if (element instanceof ServerDataStore) {
				return Messages.PropertyLabelProvider_ServerDataStore;
			} else if (element instanceof ServerDataStoreEntryImpl) {
				return NLS.bind(Messages.PropertyLabelProvider_ServerData, ((ServerDataStoreEntryImpl)element).getKey());
			}
		}
		return null;
	}

}

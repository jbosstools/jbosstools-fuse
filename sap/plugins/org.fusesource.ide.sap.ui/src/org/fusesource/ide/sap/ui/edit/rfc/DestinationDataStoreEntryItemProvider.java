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
package org.fusesource.ide.sap.ui.edit.rfc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.fusesource.camel.component.sap.model.rfc.DestinationData;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.edit.DelegatingItemPropertyDescriptor;

public class DestinationDataStoreEntryItemProvider extends org.fusesource.camel.component.sap.model.rfc.provider.DestinationDataStoreEntryItemProvider {

	public DestinationDataStoreEntryItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}
	
	@Override
	public String getText(Object object) {
		Map.Entry<?, ?> destinationDataStoreEntry = (Map.Entry<?, ?>)object;
		return "" + destinationDataStoreEntry.getKey();
	}

	@Override
	public Object getImage(Object object) {
		return Activator.getDefault().getImageRegistry().get(Activator.DESTINATION_DATA_STORE_ENTRY_IMAGE);
	}

	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
	    if (itemPropertyDescriptors == null)
	    {
	      itemPropertyDescriptors = new ArrayList<IItemPropertyDescriptor>();

	      DestinationData destinationData = RfcFactory.eINSTANCE.createDestinationData();
	      DestinationDataItemProvider destinationDataItemProvider = (DestinationDataItemProvider) getRootAdapterFactory().adapt(destinationData, IItemPropertySource.class);
	      List<IItemPropertyDescriptor> descriptors = destinationDataItemProvider.getPropertyDescriptors(object);
	      for(IItemPropertyDescriptor descriptor: descriptors) {
	    	  itemPropertyDescriptors.add(new DelegatingItemPropertyDescriptor(descriptor, RfcPackage.Literals.DESTINATION_DATA_STORE_ENTRY__VALUE));
	      }
	      
	    }
	    return itemPropertyDescriptors;
	}
}

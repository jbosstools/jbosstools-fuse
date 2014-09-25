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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.fusesource.camel.component.sap.model.rfc.RfcPackage;
import org.fusesource.ide.sap.ui.Activator;

public class DestinationDataStoreItemProvider extends org.fusesource.camel.component.sap.model.rfc.provider.DestinationDataStoreItemProvider {

	public DestinationDataStoreItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public Object getImage(Object object) {
		return Activator.getDefault().getImageRegistry().get(Activator.DESTINATION_DATA_STORE_IMAGE);
	}

	public java.util.Collection<? extends org.eclipse.emf.ecore.EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			childrenFeatures = new ArrayList<EStructuralFeature>();
			childrenFeatures.add(RfcPackage.Literals.DESTINATION_DATA_STORE__ENTRIES);
		}
		return childrenFeatures;
	}
}

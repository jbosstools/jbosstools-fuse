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

import org.eclipse.emf.common.notify.Adapter;

public class RfcItemProviderAdapterFactory extends org.fusesource.camel.component.sap.model.rfc.provider.RfcItemProviderAdapterFactory {
	
	public RfcItemProviderAdapterFactory() {
		super();
	}

	@Override
	public Adapter createDestinationAdapter() {
		if (destinationItemProvider == null) {
			destinationItemProvider = new DestinationItemProvider(this);
		}

		return destinationItemProvider;
	}
	
	@Override
	public Adapter createRFCAdapter() {
		if (rfcItemProvider == null) {
			rfcItemProvider = new RFCItemProvider(this);
		}

		return rfcItemProvider;
	}
	
	@Override
	public Adapter createTableAdapter() {
		if (tableItemProvider == null) {
			tableItemProvider = new TableItemProvider(this);
		}

		return tableItemProvider;
	}

	@Override
	public Adapter createStructureAdapter() {
		if (structureItemProvider == null) {
			structureItemProvider = new StructureItemProvider(this);
		}

		return structureItemProvider;
	}

	@Override
	public Adapter createRequestAdapter() {
		if (requestItemProvider == null) {
			requestItemProvider = new RequestItemProvider(this);
		}

		return requestItemProvider;
	}

	@Override
	public Adapter createResponseAdapter() {
		if (responseItemProvider == null) {
			responseItemProvider = new ResponseItemProvider(this);
		}

		return responseItemProvider;
	}

	@Override
	public Adapter createSapConnectionConfigurationAdapter() {
		if (sapConnectionConfigurationItemProvider == null) {
			sapConnectionConfigurationItemProvider = new SapConnectionConfigurationItemProvider(this);
		}

		return sapConnectionConfigurationItemProvider;
	}

	@Override
	public Adapter createDestinationDataEntryAdapter() {
		if (destinationDataEntryItemProvider == null) {
			destinationDataEntryItemProvider = new DestinationDataEntryItemProvider(this);
		}

		return destinationDataEntryItemProvider;
	}

	@Override
	public Adapter createDestinationDataAdapter() {
		if (destinationDataItemProvider == null) {
			destinationDataItemProvider = new DestinationDataItemProvider(this);
		}

		return destinationDataItemProvider;
	}

	@Override
	public Adapter createDestinationDataStoreEntryAdapter() {
		if (destinationDataStoreEntryItemProvider == null) {
			destinationDataStoreEntryItemProvider = new DestinationDataStoreEntryItemProvider(this);
		}

		return destinationDataStoreEntryItemProvider;
	}

	@Override
	public Adapter createDestinationDataStoreAdapter() {
		if (destinationDataStoreItemProvider == null) {
			destinationDataStoreItemProvider = new DestinationDataStoreItemProvider(this);
		}

		return destinationDataStoreItemProvider;
	}

	@Override
	public Adapter createServerAdapter() {
		if (serverItemProvider == null) {
			serverItemProvider = new ServerItemProvider(this);
		}

		return serverItemProvider;
	}

	@Override
	public Adapter createServerDataEntryAdapter() {
		if (serverDataEntryItemProvider == null) {
			serverDataEntryItemProvider = new ServerDataEntryItemProvider(this);
		}

		return serverDataEntryItemProvider;
	}

	@Override
	public Adapter createServerDataAdapter() {
		if (serverDataItemProvider == null) {
			serverDataItemProvider = new ServerDataItemProvider(this);
		}

		return serverDataItemProvider;
	}

	@Override
	public Adapter createServerDataStoreEntryAdapter() {
		if (serverDataStoreEntryItemProvider == null) {
			serverDataStoreEntryItemProvider = new ServerDataStoreEntryItemProvider(this);
		}

		return serverDataStoreEntryItemProvider;
	}

	@Override
	public Adapter createServerDataStoreAdapter() {
		if (serverDataStoreItemProvider == null) {
			serverDataStoreItemProvider = new ServerDataStoreItemProvider(this);
		}

		return serverDataStoreItemProvider;
	}

	@Override
	public Adapter createFunctionTemplateAdapter() {
		if (functionTemplateItemProvider == null) {
			functionTemplateItemProvider = new FunctionTemplateItemProvider(this);
		}

		return functionTemplateItemProvider;
	}

}

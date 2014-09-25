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
package org.fusesource.ide.sap.ui.edit.idoc;

import org.eclipse.emf.common.notify.Adapter;

public class IdocItemProviderAdapterFactory extends
	org.fusesource.camel.component.sap.model.idoc.provider.IdocItemProviderAdapterFactory {
	
	public IdocItemProviderAdapterFactory() {
		super();
	}

	@Override
	public Adapter createDocumentAdapter() {
		if (documentItemProvider == null) {
			documentItemProvider = new DocumentItemProvider(this);
		}
		return documentItemProvider;
	}
	
	@Override
	public Adapter createDocumentListAdapter() {
		if (documentListItemProvider == null) {
			documentListItemProvider = new DocumentListItemProvider(this);
		}
		return documentListItemProvider;
	}
	
	@Override
	public Adapter createSegmentChildrenAdapter() {
		if (segmentChildrenItemProvider == null) {
			segmentChildrenItemProvider = new SegmentChildrenItemProvider(this);
		}
		return segmentChildrenItemProvider;
	}
	
	@Override
	public Adapter createSegmentAdapter() {
		if (segmentItemProvider == null) {
			segmentItemProvider = new SegmentItemProvider(this);
		}
		return segmentItemProvider;
	}
	
	@Override
	public Adapter createSegmentListAdapter() {
		if (segmentListItemProvider == null) {
			segmentListItemProvider = new SegmentListItemProvider(this);
		}
		return segmentListItemProvider;
	}
}

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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.fusesource.ide.sap.ui.Activator;

public class SapConnectionConfigurationItemProvider extends
org.fusesource.camel.component.sap.model.rfc.provider.SapConnectionConfigurationItemProvider {

	public SapConnectionConfigurationItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}
	
	@Override
	public Object getImage(Object object) {
		return Activator.getDefault().getImageRegistry().get(Activator.SAP_CONNECTION_CONFIGURATION);
	}

}

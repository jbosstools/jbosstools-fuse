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
package org.fusesource.ide.sap.ui.util;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.fusesource.camel.component.sap.model.SAPEditPlugin;
import org.fusesource.camel.component.sap.model.rfc.RfcFactory;
import org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration;
import org.fusesource.ide.sap.ui.Activator;
import org.fusesource.ide.sap.ui.Messages;

public class ModelUtil {

	public static SapConnectionConfiguration getModel(ResourceSet resourceSet) {
		SapConnectionConfiguration sapConnectionConfiguration = null;
		String path = SAPEditPlugin.getPlugin().getStateLocation().append("resource.spi").toOSString(); //$NON-NLS-1$
		URI resourceURI = URI.createFileURI(path);
		Resource resource;
		try {
			resource = resourceSet.getResource(resourceURI, true);
		} catch (Exception e) {
			resource = resourceSet.getResource(resourceURI, false);
		}

		// Get resource adapter model from resource or create and add to
		// resource.
		if (resource.getContents().isEmpty()) {
			sapConnectionConfiguration = RfcFactory.eINSTANCE.createSapConnectionConfiguration();
			resource.getContents().add(sapConnectionConfiguration);
		} else {
			EObject root = resource.getContents().get(0);

			// Replace root if it is not a resource adapter model element.
			if (!(root instanceof org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration)) {
				root = RfcFactory.eINSTANCE.createSapConnectionConfiguration();
				resource.getContents().set(0, root);
			}

			// Ensure single root in resource.
			if (resource.getContents().size() > 1) {
				resource.getContents().clear();
				resource.getContents().add(root);
			}
			sapConnectionConfiguration = (org.fusesource.camel.component.sap.model.rfc.SapConnectionConfiguration) root;
		}
		
		try {
			resource.save(null);
		} catch (IOException e) {
			Activator.getLogger().warning(Messages.ModelUtil_ErrorWhenSavingSapConnectionConfiguration, e);
		}

		
		return sapConnectionConfiguration;
	}
}

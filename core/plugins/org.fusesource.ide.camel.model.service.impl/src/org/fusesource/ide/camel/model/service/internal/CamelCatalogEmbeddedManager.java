/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.impl.ICamelCatalogWrapper;

public class CamelCatalogEmbeddedManager {

	private static Set<ICamelCatalogWrapper> camelCatalogsEmbedded;

	private CamelCatalogEmbeddedManager() {
		// access through singleton
	}
	
	public static synchronized Set<ICamelCatalogWrapper> getCamelCatalogsEmbedded() {
		if (camelCatalogsEmbedded == null) {
			camelCatalogsEmbedded = new HashSet<>();
			IExtensionRegistry registry = RegistryFactory.getRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint("org.fusesource.ide.camel.model.service.impl.camelcatalogprovider");
			for (IConfigurationElement configurationElement : extensionPoint.getConfigurationElements()) {
				try {
					registerCamelCatalogWrapper(configurationElement, CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
					registerCamelCatalogWrapper(configurationElement, CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT);
				} catch (CoreException e) {
					CamelModelServiceCoreActivator.pluginLog().logError("Cannot retrieve the Camel Catalog Embedded", e);
				}
			}
		}
		return camelCatalogsEmbedded;
	}

	protected static void registerCamelCatalogWrapper(IConfigurationElement configurationElement, String runtimeProvider) throws CoreException {
		ICamelCatalogWrapper camelCatalogWrapperKaraf = (ICamelCatalogWrapper) configurationElement.createExecutableExtension("class");
		camelCatalogWrapperKaraf.setRuntimeProvider(runtimeProvider);
		camelCatalogsEmbedded.add(camelCatalogWrapperKaraf);
	}
	
}

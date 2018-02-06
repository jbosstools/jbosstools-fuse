/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

public class CamelServiceManagerUtil {
	
	private static ICamelManagerService camelService;

	private CamelServiceManagerUtil() {
		// access through singleton
	}

	public static ICamelManagerService getManagerService() {
		if (camelService == null) {
			IExtensionRegistry registry = RegistryFactory.getRegistry();
			IExtensionPoint extensionPoint = registry.getExtensionPoint("org.fusesource.ide.camel.service");
			IConfigurationElement configurationElement = extensionPoint.getConfigurationElements()[0];
			try {
				camelService = (ICamelManagerService) configurationElement.createExecutableExtension("class");
			} catch (CoreException e) {
				CamelModelServiceCoreActivator.pluginLog().logError("Cannot retrieve the Camel Service", e);
			}
		}
		return camelService;
	}

}

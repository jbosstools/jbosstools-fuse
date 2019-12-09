/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.utils;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.fusesource.ide.camel.editor.globalconfiguration.CamelGlobalConfigEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.provider.ext.ICustomGlobalConfigElementContribution;

/**
 * @author brianf
 *
 */
public class GlobalConfigUtils {
	
	private IConfigurationElement[] globalElementExtensions;
	private boolean isSAPInstalled = false;
	
	public GlobalConfigUtils() {
		globalElementExtensions = getGlobalElementExtensions();
		isSAPInstalled = isSAPGlobalExtensionEnabled();
	}

	public IConfigurationElement[] getGlobalElementExts() {
		return globalElementExtensions;
	}
	
	public boolean isSAPExtInstalled() {
		return isSAPInstalled;
	}
	
	private IConfigurationElement[] getGlobalElementExtensions() {
		return Platform.getExtensionRegistry().getConfigurationElementsFor(CamelGlobalConfigEditor.GLOBAL_ELEMENTS_PROVIDER_EXT_POINT_ID);
	}

	private boolean isSAPGlobalExtensionEnabled() {
		IConfigurationElement[] extensions = getGlobalElementExts();
		for (IConfigurationElement e : extensions) {
			if (e.getName().equals(CamelGlobalConfigEditor.TYPE_ELEMENT)) {
				try {
					Object o = e.createExecutableExtension("class");
					if (o instanceof ICustomGlobalConfigElementContribution) {
						String id = e.getAttribute(CamelGlobalConfigEditor.GLOBAL_ELEMENTS_ID_ATTR);
						if ("org.fusesource.ide.sap.ui.GlobalConfigElementSAPServerContribution".equals(id)) {
							return true;
						}
					}
				} catch (Exception ex) {
					CamelEditorUIActivator.pluginLog().logError(ex);
				}
			}
		}
		return false;
	}

}

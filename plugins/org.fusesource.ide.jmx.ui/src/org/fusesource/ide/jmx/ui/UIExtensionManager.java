/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Extension Manager for UI extensions
 */
public class UIExtensionManager {
	/* Wizard Pages */
	private static final String CONNECTION_PAGES = "org.fusesource.ide.jmx.ui.providerUI"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String ICON = "icon"; //$NON-NLS-1$
	private static final String CLASS = "class";  //$NON-NLS-1$
	public static class ConnectionProviderUI {
		String id, name, icon;
		IConfigurationElement[] wizardPages;
		ImageDescriptor imageDescriptor;
		public ConnectionProviderUI(IConfigurationElement element) {
			id = element.getAttribute(ID);
			name = element.getAttribute(NAME);
			icon = element.getAttribute(ICON);
			wizardPages = element.getChildren();
			String pluginName = element.getDeclaringExtension().getContributor().getName();
			imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginName, icon);
		}
		public String getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public String getIcon() {
			return icon;
		}
		public ImageDescriptor getImageDescriptor() {
			return imageDescriptor;
		}
		public ConnectionWizardPage[] createPages() {
			ArrayList<ConnectionWizardPage> list = new ArrayList<ConnectionWizardPage>();
			for( int i = 0; i < wizardPages.length; i++ ) {
				try {
					ConnectionWizardPage wp = (ConnectionWizardPage)wizardPages[i].createExecutableExtension(CLASS);
					list.add(wp);
				} catch( CoreException ce ) {
					ce.printStackTrace();
					// TODO LOG
				}
			}
			return list.toArray(new ConnectionWizardPage[list.size()]);
		}
	}

	private static HashMap<String, ConnectionProviderUI> connectionUIElements;
	public static HashMap<String, ConnectionProviderUI> getConnectionUIElements() {
		if( connectionUIElements == null )
			loadConnectionUI();
		return connectionUIElements;
	}

	public static ConnectionProviderUI getConnectionProviderUI(String id) {
		if( connectionUIElements == null )
			loadConnectionUI();
		return connectionUIElements.get(id);
	}

	private static void loadConnectionUI() {
		HashMap<String, ConnectionProviderUI> map = new HashMap<String, ConnectionProviderUI>();
		IExtension[] extensions = findExtension(CONNECTION_PAGES);
		ConnectionProviderUI pUI;
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement elements[] = extensions[i].getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				try {
					pUI = new ConnectionProviderUI(elements[j]);
					map.put(pUI.getId(), pUI);
				} catch (InvalidRegistryObjectException e) {
					// TODO document
				}
			}
		}
		connectionUIElements = map;
	}

	public static IWizardPage[] getNewConnectionWizardPages(String typeName) {
		ConnectionProviderUI ui = connectionUIElements.get(typeName);
		if( ui != null ) {
			IWizardPage[] pages = ui.createPages();
			if( pages != null )
				return pages;
		}
		return new IWizardPage[]{};
	}

	private static IExtension[] findExtension(String extensionId) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry
				.getExtensionPoint(extensionId);
		return extensionPoint.getExtensions();
	}
}

/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.IEditorInput;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;

/**
 * @author lhein
 */
public final class CamelDebugRegistry {
	
	private static Map<ILaunchConfiguration, CamelDebugRegistryEntry> entries = new HashMap<>();
	
	private static CamelDebugRegistry instance;
	
	/**
	 * retrieve the registry
	 * 
	 * @return
	 */
	public static synchronized CamelDebugRegistry getInstance() {
		if (instance == null) {
			instance = new CamelDebugRegistry();
		}
		return instance;
	}
	
	public void createEntry(CamelDebugTarget debugTarget, String fileName, IEditorInput editorInput, ILaunchConfiguration launchConfig) {
		entries.put(launchConfig, new CamelDebugRegistryEntry(debugTarget, fileName, editorInput, launchConfig));
	}
	
	public CamelDebugRegistryEntry getEntry(ILaunchConfiguration launchConfig) {
		return entries.get(launchConfig);
	}
	
	public void removeEntry(ILaunchConfiguration launchConfig) {
		entries.remove(launchConfig);
	}
	
	/**
	 * @return the entries
	 */
	public Map<ILaunchConfiguration, CamelDebugRegistryEntry> getEntries() {
		Map<ILaunchConfiguration, CamelDebugRegistryEntry> copy = new HashMap<>();
		copy.putAll(entries);
		return copy;
	}
}

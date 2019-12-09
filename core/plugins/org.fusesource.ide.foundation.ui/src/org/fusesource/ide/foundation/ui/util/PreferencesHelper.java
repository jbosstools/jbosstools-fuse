/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.util;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class PreferencesHelper {

	public static final String KEY_TABLE_COLUMNS = "tableColumns";

	protected static final String FUSE_ROOT_KEY = "org.fusesource.ide.commons";

	public static Preferences defaultNode(String... paths) {
		Preferences node = getDefaultScope();
		return node(node, paths);
	}

	public static Preferences configurationNode(String... paths) {
		Preferences node = getInstanceScope();
		return node(node, paths);
	}

	public static Preferences node(Preferences root, String... paths) {
		for (String path : paths) {
			root = root.node(path);
		}
		return root;
	}

	public static IEclipsePreferences getDefaultScope() {
		IScopeContext instance = null;
		try {
			instance = DefaultScope.INSTANCE;
		} catch (Throwable e) {
			// ignore could be backwards compatibility issue
		}
		return instance.getNode(FUSE_ROOT_KEY);
	}

	public static IEclipsePreferences getInstanceScope() {
		IScopeContext instance = null;
		try {
			instance = InstanceScope.INSTANCE;
		} catch (Throwable e) {
			// ignore could be backwards compatibility issue
		}
		return instance.getNode(FUSE_ROOT_KEY);
	}

	public static void flush(Preferences node) {
		try {
			node.flush();
		} catch (BackingStoreException e) {
			FoundationUIActivator.pluginLog().logWarning("Failed to store settings for " + node + ". " + e, e);
		}
	}
}

package org.fusesource.ide.commons;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
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
		Preferences node = getConfigurationScope();
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
		if (instance == null) {
			instance = new DefaultScope();
		}
		return instance.getNode(FUSE_ROOT_KEY);
	}

	public static IEclipsePreferences getConfigurationScope() {
		IScopeContext instance = null;
		try {
			instance = ConfigurationScope.INSTANCE;
		} catch (Throwable e) {
			// ignore could be backwards compatibility issue
		}
		if (instance == null) {
			instance = new ConfigurationScope();
		}
		return instance.getNode(FUSE_ROOT_KEY);
	}

	public static void flush(Preferences node) {
		try {
			node.flush();
		} catch (BackingStoreException e) {
			Activator.getLogger().warning("Failed to store settings for " + node + ". " + e, e);
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.ui.internal;

import org.eclipse.core.runtime.Platform;
import org.fusesource.ide.preferences.initializer.StagingRepositoriesPreferenceInitializer;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;
import org.jboss.tools.foundation.ui.plugin.BaseUISharedImages;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author lhein
 */
public class SyndesisExtensionsUIActivator extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.fusesource.ide.syndesis.extension.ui";
	public static final String SYNDESIS_EXTENSION_PROJECT_ICON = "icons/syndesis64.png";

	private static final String SYNDESIS_SNAPSHOTS_KEY = "syndesis_snapshots";
	private static final String SYNDESIS_SNAPSHOTS_URI = "https://oss.sonatype.org/content/repositories/snapshots/";

	private static SyndesisExtensionsUIActivator instance;

	/**
	 * default constructor
	 */
	public SyndesisExtensionsUIActivator() {
		instance = this;
	}

	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static SyndesisExtensionsUIActivator getDefault() {
		return instance;
	}

	public static BundleContext getBundleContext() {
		return instance.getBundle().getBundleContext();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		registerDebugOptionsListener(PLUGIN_ID, new Trace(this), context);
		registerSyndesisSnapshotsRepository();
	}

	@Override
	protected BaseUISharedImages createSharedImages() {
		return new SyndesisExtensionSharedImages(getBundle());
	}

	private void registerSyndesisSnapshotsRepository() {
		StagingRepositoriesPreferenceInitializer initializer = new StagingRepositoriesPreferenceInitializer();
		initializer.addStagingRepository(SYNDESIS_SNAPSHOTS_KEY, SYNDESIS_SNAPSHOTS_URI);
	}

	/**
	 * Gets message from plugin.properties
	 * 
	 * @param key
	 * @return
	 */
	public static String getMessage(String key) {
		return Platform.getResourceString(instance.getBundle(), key);
	}

	/**
	 * Get the IPluginLog for this plugin. This method helps to make logging easier,
	 * for example:
	 * 
	 * FoundationCorePlugin.pluginLog().logError(etc)
	 * 
	 * @return IPluginLog object
	 */
	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}

	/**
	 * Get a status factory for this plugin
	 * 
	 * @return status factory
	 */
	public static StatusFactory statusFactory() {
		return getDefault().statusFactoryInternal();
	}

	private static class SyndesisExtensionSharedImages extends BaseUISharedImages {
		public SyndesisExtensionSharedImages(Bundle pluginBundle) {
			super(pluginBundle);
			addImage(SYNDESIS_EXTENSION_PROJECT_ICON, SYNDESIS_EXTENSION_PROJECT_ICON);
		}
	}
}

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

package org.fusesource.ide.fabric.camel;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.camel.navigator.CamelNodeProvider;
import org.fusesource.ide.fabric.camel.navigator.CamelPreferenceInitializer;
import org.osgi.framework.BundleContext;


/**
 * Represents a connector to Fuse Fabric for viewing the agents and features
 * available in a fabric
 */
public class FabricCamelPlugin extends ImagesActivatorSupport {

	private static FabricCamelPlugin plugin;
	private static CamelNodeProvider nodeProvider;
	private static IAdapterFactory adapterFactory;
	private static AtomicBoolean started = new AtomicBoolean(false);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		registerPlugins();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		unregisterPlugins();

		plugin = null;
		super.stop(context);
	}

	public static void registerPlugins() {
		if (started.compareAndSet(false, true)) {
			new CamelPreferenceInitializer().initializeDefaultPreferences();
			nodeProvider = new CamelNodeProvider();
			adapterFactory = new FabricCamelAdapterFactory();
			Class<?>[] classses = adapterFactory.getAdapterList();
			for (Class<?> clazz : classses) {
				Platform.getAdapterManager().registerAdapters(adapterFactory, clazz);
			}
			FabricPlugin.addNodeProvider(nodeProvider);
		}
	}

	public static void unregisterPlugins() {
		if (nodeProvider != null) {
			FabricPlugin.removeNodeProvider(nodeProvider);
		}
		Class<?>[] classses = adapterFactory.getAdapterList();
		for (Class<?> clazz : classses) {
			Platform.getAdapterManager().unregisterAdapters(adapterFactory, clazz);
		}
		started.set(false);
	}

	public static FabricCamelPlugin getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
}

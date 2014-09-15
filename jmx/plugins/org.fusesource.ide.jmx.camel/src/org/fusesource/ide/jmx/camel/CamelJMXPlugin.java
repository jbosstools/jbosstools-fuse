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

package org.fusesource.ide.jmx.camel;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.fusesource.ide.jmx.camel.navigator.CamelNodeProvider;
import org.fusesource.ide.jmx.camel.navigator.CamelPreferenceInitializer;
import org.osgi.framework.BundleContext;


/**
 * Represents a connector to Fuse Fabric for viewing the agents and features
 * available in a fabric
 */
public class CamelJMXPlugin extends ImagesActivatorSupport {

	public static final String PLUGIN_ID = "org.fusesource.ide.jmx.camel";
	private static CamelJMXPlugin plugin;
	private static CamelNodeProvider nodeProvider;
	private static IAdapterFactory adapterFactory;
	
	private static CamelJMXSharedImages sharedImages;
	
	
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
		new CamelPreferenceInitializer().initializeDefaultPreferences();
		nodeProvider = new CamelNodeProvider();
		adapterFactory = new JMXCamelAdapterFactory();
		Class<?>[] classses = adapterFactory.getAdapterList();
		for (Class<?> clazz : classses) {
			Platform.getAdapterManager().registerAdapters(adapterFactory, clazz);
		}
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
		plugin = null;
		Class<?>[] classses = adapterFactory.getAdapterList();
		for (Class<?> clazz : classses) {
			Platform.getAdapterManager().unregisterAdapters(adapterFactory, clazz);
		}
		super.stop(context);
	}

	public static CamelJMXPlugin getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
	
	/**
	 * Display a user error if an operation failed
	 */
	public static void showUserError(String title, String message, Exception e) {
		showUserError(PLUGIN_ID, getLogger(), title, message, e);
	}
	
	public CamelJMXSharedImages getSharedImages() {
		if( sharedImages == null ) {
			if( getBundle() != null ) {
				sharedImages = new CamelJMXSharedImages(getBundle());
			}
		}
		return sharedImages;
	}
}

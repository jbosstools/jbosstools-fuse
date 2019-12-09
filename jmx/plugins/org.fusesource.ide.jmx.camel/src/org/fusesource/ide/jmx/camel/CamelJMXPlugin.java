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

package org.fusesource.ide.jmx.camel;

import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.foundation.ui.util.ImagesActivatorSupport;
import org.fusesource.ide.jmx.camel.navigator.CamelPreferenceInitializer;
import org.osgi.framework.BundleContext;


/**
 * Represents a connector to Fuse Fabric for viewing the agents and features
 * available in a fabric
 */
public class CamelJMXPlugin extends ImagesActivatorSupport {

	public static final String PLUGIN_ID = "org.fusesource.ide.jmx.camel";
	private static CamelJMXPlugin plugin;
	private static CamelJMXSharedImages sharedImages;
	
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		new CamelPreferenceInitializer().initializeDefaultPreferences();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
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
	
	public static synchronized CamelJMXSharedImages getSharedImages() {
		if( sharedImages == null && plugin.getBundle() != null ) {
			sharedImages = new CamelJMXSharedImages(plugin.getBundle());
		}
		return sharedImages;
	}
}

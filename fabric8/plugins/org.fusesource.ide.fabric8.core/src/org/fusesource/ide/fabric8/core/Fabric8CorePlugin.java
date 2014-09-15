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
package org.fusesource.ide.fabric8.core;

import org.eclipse.core.runtime.Plugin;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.preferences.PreferenceManager;
import org.jclouds.osgi.Activator;
import org.osgi.framework.BundleContext;

/**
 * @author lhein
 */
public class Fabric8CorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.fusesource.ide.fabric8.core.core";

	private static Fabric8CorePlugin plugin;
	private static Activator activator;

	/**
	 * 
	 * @return
	 */
	public static Fabric8CorePlugin getPlugin() {
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		activator = new Activator();
		activator.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		activator.stop(context);
		activator = null;
		plugin = null;
		super.stop(context);
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getPlugin().getLog());
	}
	
	/**
	 * Return the install location preference.
	 * 
	 * @param id a runtime type id
	 * @return the install location
	 */
	public static String getPreference(String id) {
		return PreferenceManager.getInstance().loadPreferenceAsString(id);
	}
	
	/**
	 * Set the install location preference.
	 * 
	 * @param id the runtimt type id
	 * @param value the location
	 */
	public static void setPreference(String id, String value) {
		PreferenceManager.getInstance().savePreference(id, value);
	}
}

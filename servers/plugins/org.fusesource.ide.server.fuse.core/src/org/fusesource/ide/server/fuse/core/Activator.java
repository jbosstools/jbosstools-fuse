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

package org.fusesource.ide.server.fuse.core;

import org.eclipse.core.runtime.Plugin;
import org.fusesource.ide.foundation.ui.logging.RiderLogFacade;
import org.fusesource.ide.preferences.PreferenceManager;

/**
 * @author lhein
 */
public class Activator extends Plugin {
	
	protected static Activator instance;

	public static final String PLUGIN_ID = "org.fusesource.ide.server.fuse.core";
		
	/**
	 * constructor
	 */
	public Activator() {
		super();
		instance = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * @return org.eclipse.jst.server.tomcat.internal.TomcatPlugin
	 */
	public static Activator getDefault() {
		return instance;
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
	
	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
}

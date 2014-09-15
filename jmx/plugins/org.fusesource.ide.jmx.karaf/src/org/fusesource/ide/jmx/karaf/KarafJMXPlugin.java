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

package org.fusesource.ide.jmx.karaf;

import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.fusesource.ide.commons.ui.ImagesActivatorSupport;
import org.osgi.framework.BundleContext;

/**
 * Represents a connector to Fuse Fabric for viewing the agents and features available in a fabric
 */
public class KarafJMXPlugin extends ImagesActivatorSupport {

	public static final String PLUGIN_ID = "org.fusesource.ide.jmx.karaf";
	
	private static KarafJMXPlugin plugin;

	private KarafJMXSharedImages sharedImages;
	
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
		new KarafPreferenceInitializer().initializeDefaultPreferences();
	}	

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static KarafJMXPlugin getDefault() {
		return plugin;
	}

	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}
	
	public KarafJMXSharedImages getSharedImages() {
		if( sharedImages == null ) {
			if( getBundle() != null ) {
				sharedImages = new KarafJMXSharedImages(getBundle());
			}
		}
		return sharedImages;
	}
}

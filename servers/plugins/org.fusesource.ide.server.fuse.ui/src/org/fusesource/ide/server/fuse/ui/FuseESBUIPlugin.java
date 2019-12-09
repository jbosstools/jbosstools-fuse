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

package org.fusesource.ide.server.fuse.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class FuseESBUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.fusesource.ide.server.fuse.ui";

	public static final String IMG_JBOSS_BY_RH_LOGO_LARGE = "JBoss_byRH_logo_rgb.png"; //$NON-NLS-1$
	
	// The shared instance
	private static FuseESBUIPlugin plugin;
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static FuseESBUIPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMG_JBOSS_BY_RH_LOGO_LARGE, ImageDescriptor.createFromURL(plugin.getBundle().getEntry("/icons/JBoss_byRH_logo_rgb.png")));  //$NON-NLS-1$
	}

}

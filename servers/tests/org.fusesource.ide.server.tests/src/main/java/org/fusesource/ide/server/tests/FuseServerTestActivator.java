/*******************************************************************************
 * Copyright (c) 2007 - 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.tests;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.jboss.ide.eclipse.as.core.util.FileUtil;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class FuseServerTestActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.fusesource.ide.server.tests"; //$NON-NLS-1$

	// The shared instance
	private static FuseServerTestActivator plugin;
	
	/**
	 * The constructor
	 */
	public FuseServerTestActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
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

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static FuseServerTestActivator getDefault() {
		return plugin;
	}

	/**
	 * clears the location where the mock structures are generated
	 */
	private static void clearStateLocation() {
		IPath state = FuseServerTestActivator.getDefault().getStateLocation();
		if( state.toFile().exists()) {
			File[] children = state.toFile().listFiles();
			for( int i = 0; i < children.length; i++ ) {
				FileUtil.safeDelete(children[i]);
			}
		}
	}
	
	/**
	 * clears the location where the mock structures are generated
	 */
	public static void cleanup() {
		clearStateLocation();
	}
}

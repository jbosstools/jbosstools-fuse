/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.fusesource.ide.commons.logging.RiderLogFacade;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	
	public static final String DESTINATION_DATA_STORE_IMAGE = "icons/full/obj16/DestinationDataStore.gif"; //$NON-NLS-1$
	public static final String DESTINATION_DATA_STORE_ENTRY_IMAGE = "icons/full/obj16/DestinationDataStoreEntry.gif"; //$NON-NLS-1$
	public static final String SAP_CONNECTION_CONFIGURATION = "icons/full/obj16/SapConnectionConfiguration.gif"; //$NON-NLS-1$
	public static final String SERVER_DATA_STORE_IMAGE = "icons/full/obj16/ServerDataStore.gif"; //$NON-NLS-1$
	public static final String SERVER_DATA_STORE_ENTRY_IMAGE = "icons/full/obj16/ServerDataStoreEntry.gif"; //$NON-NLS-1$
	public static final String FUSE_RS_IMAGE = "icons/fuse_rs.jpg"; //$NON-NLS-1$
	public static final String FUSE_ICON_16C_IMAGE = "icons/fuse_icon_16c.jpg"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "org.fusesource.ide.sap.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static RiderLogFacade getLogger() {
		return RiderLogFacade.getLog(getDefault().getLog());
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		ImageDescriptor image;
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(DESTINATION_DATA_STORE_IMAGE), null));
		getImageRegistry().put(DESTINATION_DATA_STORE_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(DESTINATION_DATA_STORE_ENTRY_IMAGE), null));
		getImageRegistry().put(DESTINATION_DATA_STORE_ENTRY_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SAP_CONNECTION_CONFIGURATION), null));
		getImageRegistry().put(SAP_CONNECTION_CONFIGURATION, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SERVER_DATA_STORE_IMAGE), null));
		getImageRegistry().put(SERVER_DATA_STORE_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SERVER_DATA_STORE_ENTRY_IMAGE), null));
		getImageRegistry().put(SERVER_DATA_STORE_ENTRY_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(FUSE_RS_IMAGE), null));
		getImageRegistry().put(FUSE_RS_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(FUSE_ICON_16C_IMAGE), null));
		getImageRegistry().put(FUSE_ICON_16C_IMAGE, image);
	}

}

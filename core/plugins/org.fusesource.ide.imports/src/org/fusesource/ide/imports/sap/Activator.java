/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at https://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.imports.sap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String SAP_TOOL_SUITE_16_IMAGE = "icons/sap16.png"; //$NON-NLS-1$
	public static final String SAP_TOOL_SUITE_48_IMAGE = "icons/sap48.png"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "org.fusesource.ide.imports"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static BundleContext getContext() {
		return plugin.getBundle().getBundleContext();
	}
	
	public static IProvisioningAgent getProvisioningAgent() {
		return (IProvisioningAgent) getService(getContext(), IProvisioningAgent.SERVICE_NAME);
	}

	public static Object getService(BundleContext context, String name) {
		if (context == null)
			return null;
		ServiceReference<?> reference = context.getServiceReference(name);
		if (reference == null)
			return null;
		Object result = context.getService(reference);
		context.ungetService(reference);
		return result;
	}

	/**
	 * The constructor
	 */
	public Activator() {
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

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		ImageDescriptor image;
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SAP_TOOL_SUITE_16_IMAGE), null));
		getImageRegistry().put(SAP_TOOL_SUITE_16_IMAGE, image);
		image = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(SAP_TOOL_SUITE_48_IMAGE), null));
		getImageRegistry().put(SAP_TOOL_SUITE_48_IMAGE, image);
	}

}

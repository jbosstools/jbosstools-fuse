/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.internal;

import org.eclipse.core.runtime.Platform;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;
import org.jboss.tools.foundation.ui.plugin.BaseUISharedImages;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


/**
 * @author rstryker
 */
public class FoundationUIActivator extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.fusesource.ide.foundation.ui";
	public static final String IMAGE_CAMEL_ICON = "icons/camel.png";
	public static final String IMAGE_PROPS_ICON = "icons/prop_ps.gif";
	public static final String IMAGE_CHART_ICON = "icons/chart.gif";

	private static FoundationUIActivator instance;

	/**
	 * default constructor
	 */
	public FoundationUIActivator() {
		instance = this;
	}
	
	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static FoundationUIActivator getDefault() {
		return instance;
	}
	
	public static BundleContext getBundleContext() {
	    return instance.getBundle().getBundleContext();
	}

    @Override
	public void start(BundleContext context) throws Exception {
        super.start(context);
        registerDebugOptionsListener(PLUGIN_ID, new Trace(this), context);
	}

    @Override
    protected BaseUISharedImages createSharedImages() {
    	return new FoundationUISharedImages(getBundle());
    }
	
	/**
	 * Gets message from plugin.properties
	 * @param key
	 * @return
	 */
	public static String getMessage(String key)	{
		return Platform.getResourceString(instance.getBundle(), key);
	}

	/**
	 * Get the IPluginLog for this plugin. This method 
	 * helps to make logging easier, for example:
	 * 
	 *     FoundationCorePlugin.pluginLog().logError(etc)
	 *  
	 * @return IPluginLog object
	 */
	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}

	/**
	 * Get a status factory for this plugin
	 * @return status factory
	 */
	public static StatusFactory statusFactory() {
		return getDefault().statusFactoryInternal();
	}
		
	private static class FoundationUISharedImages extends BaseUISharedImages {
		public FoundationUISharedImages(Bundle pluginBundle) {
			super(pluginBundle);
			addImage(IMAGE_CAMEL_ICON, IMAGE_CAMEL_ICON);
			addImage(IMAGE_PROPS_ICON, IMAGE_PROPS_ICON);
			addImage(IMAGE_CHART_ICON, IMAGE_CHART_ICON);
		}
	}
}

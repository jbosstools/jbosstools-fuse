/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.model.service.core.internal;

import org.eclipse.core.runtime.Platform;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * @author rstryker
 */
public class CamelModelServiceCoreActivator extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.fusesource.ide.camel.model.service.core";

	private static CamelModelServiceCoreActivator instance;
	private static BundleContext myContext;

	/**
	 * default constructor
	 */
	public CamelModelServiceCoreActivator() {
		instance = this;
	}

	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static CamelModelServiceCoreActivator getDefault() {
		return instance;
	}
	
	public static BundleContext getBundleContext() {
	    return myContext;
	}

    public void start(BundleContext context) throws Exception {
        super.start(context);
        myContext = context;
        registerDebugOptionsListener(PLUGIN_ID, new Trace(this), context);
	}
    
    @Override
    public void stop(BundleContext context) throws Exception {
    	myContext = null;
    	super.stop(context);
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
	
}

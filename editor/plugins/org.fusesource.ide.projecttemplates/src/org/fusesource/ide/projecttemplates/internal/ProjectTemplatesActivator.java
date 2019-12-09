/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.projecttemplates.internal;

import org.eclipse.core.runtime.Platform;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;
import org.jboss.tools.foundation.ui.plugin.BaseUISharedImages;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author lhein
 */
public class ProjectTemplatesActivator extends BaseUIPlugin {

	public static final String PLUGIN_ID = "org.fusesource.ide.projecttemplates";
	public static final String IMAGE_CAMEL_CONTEXT_ICON = "icons/camel_context_icon.png";
	public static final String IMAGE_CAMEL_PROJECT_ICON = "icons/camel_project_64x64.png";
	public static final String IMAGE_CAMEL_ROUTE_FOLDER_ICON = "icons/camel_route_folder.png";
	public static final String IMAGE_FUSE_ICON = "icons/fuse_icon_16c.png";
	
	private static ProjectTemplatesActivator instance;

	/**
	 * default constructor
	 */
	public ProjectTemplatesActivator() {
		instance = this;
	}	
	
	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static ProjectTemplatesActivator getDefault() {
		return instance;
	}
	
	public static BundleContext getBundleContext() {
		return instance.getBundle().getBundleContext();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        registerDebugOptionsListener(PLUGIN_ID, new Trace(this), context);
	}
    
    @Override
    protected BaseUISharedImages createSharedImages() {
    	return new ProjectTemplatesSharedImages(getBundle());
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
	
	private static class ProjectTemplatesSharedImages extends BaseUISharedImages {
		public ProjectTemplatesSharedImages(Bundle pluginBundle) {
			super(pluginBundle);
			addImage(IMAGE_CAMEL_CONTEXT_ICON, IMAGE_CAMEL_CONTEXT_ICON);
			addImage(IMAGE_CAMEL_PROJECT_ICON, IMAGE_CAMEL_PROJECT_ICON);
			addImage(IMAGE_CAMEL_ROUTE_FOLDER_ICON, IMAGE_CAMEL_ROUTE_FOLDER_ICON);
			addImage(IMAGE_FUSE_ICON, IMAGE_FUSE_ICON);
		}
	}
}

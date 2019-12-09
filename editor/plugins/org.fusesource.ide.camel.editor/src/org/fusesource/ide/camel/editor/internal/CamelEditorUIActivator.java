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
package org.fusesource.ide.camel.editor.internal;

import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.core.plugin.log.StatusFactory;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author lhein
 */
public class CamelEditorUIActivator extends BaseUIPlugin {
	public static final String PLUGIN_ID = "org.fusesource.ide.camel.editor";
	
	private static CamelEditorUIActivator instance;
	private static BundleContext myContext;
	
	private PreferredPerspectivePartListener perspectiveListener;
	
	/**
	 * default constructor
	 */
	public CamelEditorUIActivator() {
		instance = this;
	}

	/**
	 * returns the instance
	 * 
	 * @return
	 */
	public static CamelEditorUIActivator getDefault() {
		return instance;
	}
	
	public static BundleContext getBundleContext() {
	    return myContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        myContext = context;
        registerDebugOptionsListener(PLUGIN_ID, new Trace(this), context);
		perspectiveListener = new PreferredPerspectivePartListener();
		perspectiveListener.earlyStartup();
	}
    
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
    @Override
    public void stop(BundleContext context) throws Exception {
    	myContext = null;
    	super.stop(context);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    @Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		String prefix = "/icons/";
		Enumeration<URL> enu = getBundle().findEntries(prefix, "*", true);
		while (enu.hasMoreElements()) {
			URL u = enu.nextElement();
			String file = u.getFile();
			String fileName = file;
			if (!file.startsWith(prefix)) {
				CamelEditorUIActivator.pluginLog().logWarning("Warning: image: " + fileName + " does not start with prefix: " + prefix);
			}
			fileName = fileName.substring(fileName.lastIndexOf('/')+1);
			registerImage(reg, fileName, file);
		}
	}

	/**
	 * registers the given file under the given key
	 * @param reg
	 * @param key		the key to register under
	 * @param fileName	the file name
	 */
	private void registerImage(ImageRegistry reg, String key, String filePath) {
		reg.put(key, imageDescriptorFromPlugin(getBundle().getSymbolicName(), filePath));
	}

	/**
	 * returns the image stored under the given key
	 * 
	 * @param key	the key to lookup the image
	 * @return	the image or null if not found
	 */
	public Image getImage(String key) {
		return getImageRegistry().get(key);
	}

	/**
	 * returns the image descriptor stored under the given key
	 * 
	 * @param key	the key to lookup the image descriptor
	 * @return	the image descriptor or null if not found
	 */
	public ImageDescriptor getImageDescriptor(String key) {
		return getImageRegistry().getDescriptor(key);
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

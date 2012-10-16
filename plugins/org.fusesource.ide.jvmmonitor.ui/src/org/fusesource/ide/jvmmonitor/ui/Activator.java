/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator that controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "org.fusesource.ide.jvmmonitor.ui"; //$NON-NLS-1$

    /** The shared instance. */
    private static Activator plugin;

    /*
     * @see AbstractUIPlugin#start(BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * @see AbstractUIPlugin#stop(BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     * 
     * @return The shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path
     * 
     * @param path
     *            The path
     * @return The image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    /**
     * Logs the exception with status and message.
     * 
     * @param message
     *            The message
     * @param e
     *            The exception
     */
    public static void log(String message, CoreException e) {
        log(e.getStatus().getSeverity(), message, e);
    }

    /**
     * Logs the exception with status and message.
     * 
     * @param severity
     *            The severity that can be info, warning or error
     * @param message
     *            The message
     * @param t
     *            The exception
     */
    public static void log(int severity, String message, Throwable t) {
        if (plugin == null || !plugin.isDebugging()
                && (severity == IStatus.WARNING || severity == IStatus.INFO)) {
            return;
        }
        IStatus status = new Status(severity, PLUGIN_ID, IStatus.OK, message, t);
        plugin.getLog().log(status);
    }

    /**
     * Gets the dialog settings.
     * 
     * @param sectionName
     *            The sectionName
     * 
     * @return The dialog settings
     */
    public IDialogSettings getDialogSettings(String sectionName) {
        IDialogSettings settings = getDialogSettings();
        IDialogSettings section = settings.getSection(sectionName);
        if (section == null) {
            section = settings.addNewSection(sectionName);
        }
        return section;
    }
}

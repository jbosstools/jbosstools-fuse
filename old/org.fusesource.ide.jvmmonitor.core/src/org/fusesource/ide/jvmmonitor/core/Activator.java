/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator that controls the plug-in life cycle.
 */
public class Activator extends Plugin {

    /** The plug-in ID. */
    public static final String PLUGIN_ID = "org.fusesource.ide.jvmmonitor.core"; //$NON-NLS-1$

    /** The shared instance. */
    private static Activator plugin;

    /*
     * @see Plugin#start(BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * @see Plugin#stop(BundleContext)
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
     * Logs the exception with status and message.
     * 
     * @param severity
     *            The status which can be info, warning or error
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
}

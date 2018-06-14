/******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Red Hat - Initial implementation.
 *****************************************************************************/
package org.jboss.tools.fuse.transformation.editor;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.fuse.transformation.editor.internal.MappingDetailViewer;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The shared instance
    private static Activator plugin;

    public static final String PLUGIN_ID = "org.jboss.tools.fuse.transformation.editor"; //$NON-NLS-1$
    public static final String TRANSFORMATION_EXTENSION_POINT = PLUGIN_ID + ".function"; //$NON-NLS-1$

    /**
     * @param red
     * @param green
     * @param blue
     * @return the supplied color after it has been registered
     */
    public static Color color(final int red,
                              final int green,
                              final int blue) {
        final RGB rgb = new RGB(red, green, blue);
        Color color = plugin.colorRegistry.get(rgb);
        if (color != null) {
            return color;
        }
        color = new Color(null, rgb);
        plugin.colorRegistry.put(rgb, color);
        return color;
    }

    public static IPreferenceStore preferences() {
        return plugin.getPreferenceStore();
    }

    /**
     * @param error
     */
    public static void error(Throwable error) {
        Status status = new Status(Status.ERROR,
                                   plugin.getBundle().getSymbolicName(),
                                   "Unexpected error: " + error.getMessage(), //$NON-NLS-1$
                                   error);
        ErrorDialog.openError(Display.getCurrent().getActiveShell(),
                              "Error", //$NON-NLS-1$
                              status.getMessage(),
                              status);
        log(status);
    }

    /**
     * @param name
     * @return the image with the supplied name
     */
    public static ImageDescriptor imageDescriptor(final String name) {
        final String key = plugin.getBundle().getSymbolicName() + "." + name; //$NON-NLS-1$
        ImageDescriptor img = plugin.getImageRegistry().getDescriptor(key);
        if (img != null) {
            return img;
        }
        img = ImageDescriptor.createFromURL(plugin.getBundle().getEntry("icons/" + name)); //$NON-NLS-1$
        plugin.getImageRegistry().put(key, img);
        return img;
    }

    public static void log(int status, String message) {
        log(new Status(status, plugin.getBundle().getSymbolicName(), message));
    }

    /**
     * @param status
     */
    public static void log(Status status) {
    	if(plugin != null){
    		plugin.getLog().log(status);
    	}
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator plugin() {
        return plugin;
    }

    private final Map<RGB, Color> colorRegistry = new HashMap<>();

    /**
     * The constructor
     */
    public Activator() {}

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        PreferenceConverter.setDefault(getPreferenceStore(),
                                       MappingDetailViewer.TRANSFORMATION_FOREGROUND_PREFERENCE,
                                       new RGB(0, 0, 0));
        PreferenceConverter.setDefault(getPreferenceStore(),
                                       MappingDetailViewer.TRANSFORMATION_BACKGROUND_PREFERENCE,
                                       new RGB(255, 255, 0));
        preferences().setDefault(MappingDetailViewer.TRANSFORMATION_USER_FRIENDLY_FORMAT_PREFERENCE, true);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(final BundleContext context) throws Exception {
        for (final Color color : colorRegistry.values()) {
            color.dispose();
        }
        plugin = null;
        super.stop(context);
    }
}

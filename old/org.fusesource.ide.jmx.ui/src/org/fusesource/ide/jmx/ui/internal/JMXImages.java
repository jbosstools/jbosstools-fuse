/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/


/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;


import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.osgi.framework.Bundle;


/**
 * Bundle of most images used by the Java plug-in.
 */
public class JMXImages {

    public static final IPath ICONS_PATH = new Path("$nl$/icons/full"); //$NON-NLS-1$

    private static final String NAME_PREFIX = "org.fusesource.ide.jmx.ui."; //$NON-NLS-1$

    private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();

    // The plug-in registry
    private static ImageRegistry fgImageRegistry = null;

    private static HashMap<String, ImageDescriptor> fgAvoidSWTErrorMap = null;

    private static final String T_OBJ = "obj16"; //$NON-NLS-1$

    public static final String IMG_MISC_PUBLIC = NAME_PREFIX
            + "methpub_obj.gif"; //$NON-NLS-1$

    public static final String IMG_FIELD_PUBLIC = NAME_PREFIX
            + "field_public_obj.gif"; //$NON-NLS-1$

    public static final String IMG_OBJS_INTERFACE = NAME_PREFIX + "int_obj.gif"; //$NON-NLS-1$

    public static final String IMG_OBJS_METHOD = NAME_PREFIX
    + "jmeth_obj.gif"; //$NON-NLS-1$

    public static final String IMG_OBJS_PACKAGE = NAME_PREFIX
            + "package_obj.gif"; //$NON-NLS-1$

    public static final String IMG_OBJS_LIBRARY = NAME_PREFIX
            + "library_obj.gif"; //$NON-NLS-1$

    public static final String IMG_OBJS_READ_WRITE = NAME_PREFIX
    + "readwrite_obj.gif"; //$NON-NLS-1$

    public static final String IMG_GEARS = NAME_PREFIX
    + "releng_gears.gif"; //$NON-NLS-1$

    public static final String IMG_OBJS_READ = NAME_PREFIX + "read_obj.gif"; //$NON-NLS-1$

    public static final String IMG_OBJS_WRITE = NAME_PREFIX + "write_obj.gif"; //$NON-NLS-1$

    public static final ImageDescriptor DESC_MISC_PUBLIC = createManagedFromKey(
            T_OBJ, IMG_MISC_PUBLIC);

    public static final ImageDescriptor DESC_FIELD_PUBLIC = createManagedFromKey(
            T_OBJ, IMG_FIELD_PUBLIC);

    public static final ImageDescriptor DESC_OBJS_PACKAGE = createManagedFromKey(
            T_OBJ, IMG_OBJS_PACKAGE);

    public static final ImageDescriptor DESC_OBJS_INTERFACE = createManagedFromKey(
            T_OBJ, IMG_OBJS_INTERFACE);

    public static final ImageDescriptor DESC_OBJS_METHOD = createManagedFromKey(
        T_OBJ, IMG_OBJS_METHOD);

    public static final ImageDescriptor DESC_OBJS_LIBRARY = createManagedFromKey(
            T_OBJ, IMG_OBJS_LIBRARY);

    public static final ImageDescriptor DESC_OBJS_READ_WRITE = createManagedFromKey(
            T_OBJ, IMG_OBJS_READ_WRITE);

    public static final ImageDescriptor DESC_OBJS_READ = createManagedFromKey(
            T_OBJ, IMG_OBJS_READ);

    public static final ImageDescriptor DESC_OBJS_WRITE = createManagedFromKey(
            T_OBJ, IMG_OBJS_WRITE);

    public static Image get(String key) {
        return getImageRegistry().get(key);
    }

    public static void setLocalImageDescriptors(IAction action, String iconName) {
        setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
    }

    private static void setImageDescriptors(IAction action, String type,
            String relPath) {
        ImageDescriptor id = create("d" + type, relPath, false); //$NON-NLS-1$
        if (id != null)
            action.setDisabledImageDescriptor(id);

        ImageDescriptor descriptor = create("e" + type, relPath, true); //$NON-NLS-1$
        action.setHoverImageDescriptor(descriptor);
        action.setImageDescriptor(descriptor);
    }

    private static ImageRegistry getImageRegistry() {
        if (fgImageRegistry == null) {
            fgImageRegistry = new ImageRegistry();
            for (Iterator<String> iter = fgAvoidSWTErrorMap.keySet().iterator(); iter
                    .hasNext();) {
                String key = iter.next();
                fgImageRegistry.put(key, fgAvoidSWTErrorMap.get(key));
            }
            fgAvoidSWTErrorMap = null;
        }
        return fgImageRegistry;
    }

    private static ImageDescriptor createManagedFromKey(String prefix,
            String key) {
        return createManaged(prefix, key.substring(NAME_PREFIX_LENGTH), key);
    }

    private static ImageDescriptor createManaged(String prefix, String name,
            String key) {
        ImageDescriptor result = create(prefix, name, true);

        if (fgAvoidSWTErrorMap == null) {
            fgAvoidSWTErrorMap = new HashMap<String, ImageDescriptor>();
        }
        fgAvoidSWTErrorMap.put(key, result);
        return result;
    }

    private static ImageDescriptor create(String prefix, String name,
            boolean useMissingImageDescriptor) {
        IPath path = ICONS_PATH.append(prefix).append(name);
        return createImageDescriptor(JMXUIActivator.getDefault().getBundle(),
                path, useMissingImageDescriptor);
    }

    private static ImageDescriptor createImageDescriptor(Bundle bundle,
            IPath path, boolean useMissingImageDescriptor) {
        URL url = FileLocator.find(bundle, path, null);
        if (url != null) {
            return ImageDescriptor.createFromURL(url);
        }
        if (useMissingImageDescriptor) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
        return null;
    }
}

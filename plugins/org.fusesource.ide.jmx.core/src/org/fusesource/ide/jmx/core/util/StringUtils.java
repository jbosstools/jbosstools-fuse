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

package org.fusesource.ide.jmx.core.util;

import java.lang.reflect.Array;

import org.eclipse.core.runtime.Assert;

public class StringUtils {

    public static final String NULL = "null"; //$NON-NLS-1$

    public static String toString(Object obj, boolean detailed) {
        if (obj == null) {
            return NULL;
        }
        if (!obj.getClass().isArray()) {
            return obj.toString();
        }
        if (detailed) {
            return toDetailedString(obj);
        } else {
            return toSimpleString(obj);
        }
    }

    private static final String toSimpleString(Object arrayObj) {
        Assert.isNotNull(arrayObj);
        Assert.isLegal(arrayObj.getClass().isArray());

        String type = arrayObj.getClass().getComponentType().getName();
        int length = Array.getLength(arrayObj);
        return type + '[' + length + ']';
    }

    private static final String toDetailedString(Object arrayObj) {
        Assert.isNotNull(arrayObj);
        Assert.isLegal(arrayObj.getClass().isArray());

        Object element;
        StringBuffer buff = new StringBuffer();
        int length = Array.getLength(arrayObj);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                buff.append(", "); //$NON-NLS-1$
            }

            element = Array.get(arrayObj, i);
            if (element == null) {
                buff.append(NULL);
            } else {
                buff.append(element.toString());
            }
        }
        return buff.toString();
    }

    public static String toString(String type) {
        return toString(type, true);
    }

    /*
     * this function is be called recursively to display multi-dimensional
     * arrays, e.g. int[][]
     */
    public static String toString(String type, boolean detailed) {
        Assert.isNotNull(type);
        Assert.isLegal(type.length() > 0);
        if (!isArray(type)) {
            return type;
        }

        try {
            Class clazz = StringUtils.class.getClassLoader().loadClass(type);
            if (clazz.isArray()) {
                if (detailed) {
                    return toString(clazz.getComponentType().getName(),
                            detailed)
                            + "[]"; //$NON-NLS-1$
                } else {
                    return toString(clazz.getComponentType().getSimpleName(),
                            detailed)
                            + "[]"; //$NON-NLS-1$
                }
            }
        } catch (ClassNotFoundException e) {
            // we do not know the class but we can still display a user-friendly
            // representation
            // of the array
            if (type.startsWith("[L") && type.endsWith(";")) { //$NON-NLS-1$ //$NON-NLS-2$
                return toString(type.substring(2, type.length() - 1), detailed)
                        + "[]"; //$NON-NLS-1$
            }
        }
        return type;
    }

    private static boolean isArray(String type) {
        return type.startsWith("["); //$NON-NLS-1$
    }

}

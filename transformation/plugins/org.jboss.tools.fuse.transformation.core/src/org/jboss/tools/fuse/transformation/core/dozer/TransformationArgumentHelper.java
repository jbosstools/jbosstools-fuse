/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.core.dozer;

import org.jboss.tools.fuse.transformation.core.dozer.config.Field;

public final class TransformationArgumentHelper {

    static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String emptyForNull(String str) {
        return str == null ? "" : str; //$NON-NLS-1$
    }

    public static String getArgumentPart(Field field,
                                         String separator,
                                         int idx) {
        String part = null;
        if (field.getCustomConverterArgument() != null) {
            String[] parts = field.getCustomConverterArgument().split(separator);
            if (parts.length > idx) {
                part = parts[idx];
            }
        }
        return part;
    }

    public static String[] getArgumentParts(Field field,
                                            String separator) {
        if (field.getCustomConverterArgument() == null) return EMPTY_STRING_ARRAY;
        return field.getCustomConverterArgument().split(separator);
    }
}

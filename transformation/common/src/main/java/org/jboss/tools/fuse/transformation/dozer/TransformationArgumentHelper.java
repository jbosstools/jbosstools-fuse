/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.jboss.tools.fuse.transformation.dozer;

import org.jboss.tools.fuse.transformation.dozer.config.Field;

public final class TransformationArgumentHelper {

    static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static String emptyForNull(String str) {
        return str == null ? "" : str;
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

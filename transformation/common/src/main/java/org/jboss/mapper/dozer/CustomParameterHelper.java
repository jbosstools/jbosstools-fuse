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
package org.jboss.mapper.dozer;

import org.jboss.mapper.dozer.config.Field;

public final class CustomParameterHelper {

    public static String getParameterPart(Field field, String separator, int idx) {
        String part = null;
        if (field.getCustomConverterParam() != null) {
            String[] parts = field.getCustomConverterParam().split(separator);
            if (parts.length > idx) {
                part = parts[idx];
            }
        }
        return part;
    }

    public static String emptyForNull(String str) {
        return str == null ? "" : str;
    }
}

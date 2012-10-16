/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fusesource.ide.zk.jmx.data;

import java.util.Arrays;
import java.util.regex.Pattern;

import javax.management.Descriptor;
import javax.management.JMX;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.openmbean.CompositeData;

public final class JmxUtils {

    private static final String PACKAGE_NAME_JAVA_LANG = "java.lang.";
    private static final Pattern REGEX_PATTERN_PERIOD = Pattern.compile("\\.");

    public static final String stringValueOf(Object object) {
        String valueString = null;

        if (object != null && object.getClass().isArray()) {

            if (object instanceof boolean[]) {
                valueString = Arrays.toString((boolean[]) object);
            }
            else if (object instanceof byte[]) {
                valueString = Arrays.toString((byte[]) object);
            }
            else if (object instanceof char[]) {
                valueString = Arrays.toString((char[]) object);
            }
            else if (object instanceof double[]) {
                valueString = Arrays.toString((double[]) object);
            }
            else if (object instanceof float[]) {
                valueString = Arrays.toString((float[]) object);
            }
            else if (object instanceof int[]) {
                valueString = Arrays.toString((int[]) object);
            }
            else if (object instanceof long[]) {
                valueString = Arrays.toString((long[]) object);
            }
            else if (object instanceof Object[]) {
                valueString = Arrays.toString((Object[]) object);
            }
            else if (object instanceof short[]) {
                valueString = Arrays.toString((short[]) object);
            }
        }

        if (valueString == null) {
            valueString = String.valueOf(object);
        }

        return valueString;
    }

    public static final String getOperationName(MBeanOperationInfo info) {
        StringBuilder name = new StringBuilder(info.getName());
        name.append('(');

        MBeanParameterInfo[] parameterInfos = info.getSignature();

        if (parameterInfos != null) {

            int parameterCount = parameterInfos.length;
            for (int i = 0; i < parameterCount; i++) {
                MBeanParameterInfo parameterInfo = parameterInfos[i];
                String parameterType = getTypeName(parameterInfo.getType(), parameterInfo.getDescriptor());
                name.append(parameterType);

                if (i < parameterCount - 1) {
                    name.append(", ");
                }
            }

        }

        name.append(')');
        return name.toString();
    }

    public static final String getTypeName(String rawTypeName, Descriptor typeDescriptor) {

        String name = rawTypeName;
        if (name == null) {
            return null;
        }

        Class<?> type = null;
        try {
            type = Class.forName(name);
        }
        catch (Throwable t) {
        }

        if (type != null) {
            name = type.getCanonicalName();

            if ((CompositeData.class.equals(type) || (type.isArray() && CompositeData.class.equals(type
                    .getComponentType())))
                    && typeDescriptor != null) {

                String originalTypeName = (String) typeDescriptor.getFieldValue(JMX.ORIGINAL_TYPE_FIELD);
                if (originalTypeName != null && !originalTypeName.isEmpty()) {

                    Class<?> originalType = null;
                    try {
                        originalType = Class.forName(originalTypeName);
                    }
                    catch (Throwable t) {
                    }

                    if (originalType != null) {
                        name = originalType.getCanonicalName();
                    }

                }
            }
        }

        if (name.startsWith(PACKAGE_NAME_JAVA_LANG) && REGEX_PATTERN_PERIOD.split(name).length == 3) {
            name = name.substring(PACKAGE_NAME_JAVA_LANG.length());
        }

        return name;
    }

}

/**
 *  Copyright 2005-2014 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.fusesource.ide.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Reflections {

    public static boolean isStatic(Field f) {
        return Modifier.isStatic(f.getModifiers());
    }

    public static boolean hasAnnotations(AnnotatedElement e, Class[] cs) {
        boolean has = false;
        for (Class c : cs) {
            if (c.isAnnotation()) {
                has = hasAnnotation(e, c);
                if (has) {
                    break;
                }
            } else {
                throw new ClassCastException("Could not convert: " + c.getName() + " into an annotation class");
            }
        }
        return has;
    }

    public static <T extends Annotation> boolean hasAnnotation(AnnotatedElement e, Class<T> c) {
        return e != null && e.getAnnotation(c) != null;
    }

}

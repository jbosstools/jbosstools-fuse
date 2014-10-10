/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
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

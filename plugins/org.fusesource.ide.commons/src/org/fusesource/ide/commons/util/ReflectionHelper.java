
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

package org.fusesource.ide.commons.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionHelper {

	public static Method getDeclaredMethod(Class<?> aClass, String name) throws NoSuchMethodException {
		try {
			return aClass.getDeclaredMethod(name);
		} catch (NoSuchMethodException e) {
			Class<?> superclass = aClass.getSuperclass();
			if (aClass == Object.class || superclass == null) {
				throw e;
			} else {
				return getDeclaredMethod(superclass, name);
			}
		}
	}

	public static Field getDeclaredField(Class<?> aClass, String name) throws NoSuchFieldException {
		try {
			return aClass.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			Class<?> superclass = aClass.getSuperclass();
			if (aClass == Object.class || superclass == null) {
				throw e;
			} else {
				return getDeclaredField(superclass, name);
			}
		}
	}

}


/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

	public static void setField(Object owner, String name, Object newValue, Class<?> definedIn) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getFieldDescriptor(owner,  name, definedIn);
		field.set(owner,  newValue);
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

	/**
	 * Returns an array containing all the elements of the given collection
	 * which are of the given type
	 */
	public static <T> T[] getArrayOf(Collection<?> coll, Class<T> aType) {
		List<T> answer = new ArrayList<T>(coll.size());
		for (Object element : coll) {
			if (aType.isInstance(element)) {
				answer.add(aType.cast(element));
			}
		}
		T[] array = (T[]) Array.newInstance(aType, answer.size());
		return answer.toArray(array);
	}

	public static <T> T getField(Object instance, String fieldName, Class<? extends Object> aClass)
			throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = getFieldDescriptor(instance, fieldName, aClass);
		return (T) field.get(instance);
	}

	public static Field getFieldDescriptor(Object instance, String fieldName) throws NoSuchFieldException {
		Class<? extends Object> aClass = instance.getClass();
		while (true) {
			try {
				return getFieldDescriptor(instance, fieldName, aClass);
			} catch (NoSuchFieldException e) {
				if (aClass != Object.class) {
					Class<?> superclass = aClass.getSuperclass();
					if (superclass != null && superclass != Object.class && superclass != aClass) {
						aClass = superclass;
						continue;
					}
				}
				throw e;
			}
		}
	}

	public static Field getFieldDescriptor(Object instance, String fieldName, Class<? extends Object> aClass)
			throws NoSuchFieldException {
		notNull(instance, "instance");
		Field field = aClass.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}

	public static Method getMethodDescriptor(Object instance, String methodName, Class<? extends Object> aClass,
			Class<?>... parameters) throws NoSuchMethodException {
		notNull(instance, "instance");
		Method method = aClass.getDeclaredMethod(methodName, parameters);
		method.setAccessible(true);
		return method;
	}

	public static <T> T notNull(T value, String message) {
		if (value == null) {
			throw new IllegalArgumentException(message);
		}
		return value;
	}
}

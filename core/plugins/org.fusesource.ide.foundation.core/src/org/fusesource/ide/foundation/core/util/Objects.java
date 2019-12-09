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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import org.fusesource.ide.foundation.core.functions.ReturnType;



public class Objects {

	/**
	 * A helper method for comparing objects for equality while handling nulls
	 */
	public static boolean equal(Object a, Object b) {
		if (a == b) {
			return true;
		}
		return a != null && b != null && a.equals(b);
	}

	/**
	 * A helper method for performing an ordered comparison on the objects
	 * handling nulls and objects which do not handle sorting gracefully
	 *
	 * @param a  the first object
	 * @param b  the second object
	 * @param ignoreCase  ignore case for string comparison
	 */
	@SuppressWarnings("unchecked")
	public static int compare(Object a, Object b) {
		if (a == b) {
			return 0;
		}
		if (a == null) {
			return -1;
		}
		if (b == null) {
			return 1;
		}
		if (a instanceof Comparable) {
			Comparable comparable = (Comparable)a;
			return comparable.compareTo(b);
		}
		int answer = a.getClass().getName().compareTo(b.getClass().getName());
		if (answer == 0) {
			answer = a.hashCode() - b.hashCode();
		}
		return answer;
	}

	public static String typeName(Object value) {
		return value == null ? "null" : value.getClass().getCanonicalName();
	}


	public static Class<?> getReturnType(Object object) {
		if (object instanceof ReturnType) {
			ReturnType rt = (ReturnType) object;
			return rt.getReturnType();
		}
		return null;
	}

	public static boolean isNumberType(Class<?> aType) {
		if (aType != null && aType != boolean.class && aType != Boolean.class) {
			return aType.isPrimitive() || Number.class.isAssignableFrom(aType);
		}
		return false;
	}

	public static void setField(Object owner, String name, Object newValue, Class<?> definedIn) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		ReflectionHelper.setField(owner, name, newValue, definedIn);
	}

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static Integer parseInt(String text) {
		if (!Strings.isBlank(text)) {
			return Integer.parseInt(text);
		}
		return null;
	}

	public static String makeString(String prefix, String separator, String postfix, Object[] values) {
		StringBuilder buffer = new StringBuilder(prefix);
		boolean first = true;
		for (Object object : values) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			buffer.append(object);
		}
		buffer.append(postfix);
		return buffer.toString();
	}

	public static String makeString(String prefix, String separator, String postfix, long[] values) {
		StringBuilder buffer = new StringBuilder(prefix);
		boolean first = true;
		for (long object : values) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			buffer.append(Long.toString(object));
		}
		buffer.append(postfix);
		return buffer.toString();
	}

	public static String makeString(String prefix, String separator, String postfix, Iterable<?> values) {
		StringBuilder buffer = new StringBuilder(prefix);
		boolean first = true;
		for (Object object : values) {
			if (first) {
				first = false;
			} else {
				buffer.append(separator);
			}
			buffer.append(object);
		}
		buffer.append(postfix);
		return buffer.toString();
	}
	
	
	/**
	 * Returns an array containing all the elements of the given collection which are of the given type
	 */
	@Deprecated
	public static <T> T[] getArrayOf(Collection<?> coll, Class<T> aType) {
		return ReflectionHelper.getArrayOf(coll, aType);
	}

	@Deprecated
	public static <T> T getField(Object instance, String fieldName, Class<? extends Object> aClass) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		return ReflectionHelper.getField(instance, fieldName, aClass);
	}

	@Deprecated
	public static Field getFieldDescriptor(Object instance, String fieldName) throws NoSuchFieldException {
		return ReflectionHelper.getFieldDescriptor(instance, fieldName);
	}

	@Deprecated
	public static Field getFieldDescriptor(Object instance, String fieldName, Class<? extends Object> aClass)
			throws NoSuchFieldException {
		return ReflectionHelper.getFieldDescriptor(instance, fieldName, aClass);
	}

	@Deprecated
	public static Method getMethodDescriptor(Object instance, String methodName, Class<? extends Object> aClass, Class<?>... parameters) throws NoSuchMethodException {
		return ReflectionHelper.getMethodDescriptor(instance, methodName, aClass, parameters);
	}

    /**
     * A helper method to return a non null value or the default value if it is null
     *
     * @param value
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T getOrElse(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T> T notNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

}

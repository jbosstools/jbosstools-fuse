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

package org.fusesource.ide.camel.model.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.converter.ObjectConverter;
import org.apache.camel.model.ExpressionSubElementDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.util.ObjectHelper;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.HasValue;
import org.fusesource.ide.foundation.core.util.Strings;


public class Objects extends org.fusesource.ide.foundation.core.util.Objects {

	/**
	 * A helper method for comparing objects for equality while handling nulls
	 */
	public static boolean equal(Object a, Object b) {
		if (a == b) {
			return true;
		}
		return a != null && b != null && a.equals(b);
	}

	public static <T> T convertTo(Object value, Class<T> aClass) {
		if (ExpressionSubElementDefinition.class.isAssignableFrom(aClass) && value instanceof ExpressionDefinition) {
			ExpressionDefinition exp = (ExpressionDefinition) value;
			ExpressionSubElementDefinition answer = new ExpressionSubElementDefinition();
			answer.setExpressionType(exp);
			return (T) answer;
		} else if (ExpressionDefinition.class.isAssignableFrom(aClass)
				&& value instanceof ExpressionSubElementDefinition) {
			ExpressionSubElementDefinition exp = (ExpressionSubElementDefinition) value;
			return (T) exp.getExpressionType();
		} else if (ExpressionDefinition.class.isAssignableFrom(aClass) && value instanceof List) {
			List list = (List) value;
			if (list.size() > 0) {
				Object object = list.get(0);
				if (object instanceof ExpressionDefinition) {
					return (T) object;
				}
			}
			return null;
		} else if (List.class.isAssignableFrom(aClass) && value instanceof ExpressionDefinition) {
			List<ExpressionDefinition> answer = new ArrayList<ExpressionDefinition>();
			answer.add((ExpressionDefinition) value);
			return (T) answer;
		} else if (boolean.class.isAssignableFrom(aClass)) {
			Boolean b = ObjectConverter.toBool(value);
			return (T) b;
		} else if (Boolean.class.isAssignableFrom(aClass)) {
			Boolean b = ObjectConverter.toBoolean(value);
			if (b == null || b.booleanValue() == false) {
				// lets try return null where possible to avoid
				// generating big XML
				return null;
			} else {
				return (T) b;
			}
		} else if (Byte.class.isAssignableFrom(aClass)) {
			return (T) ObjectConverter.toByte(value);
		} else if (Short.class.isAssignableFrom(aClass)) {
			return (T) ObjectConverter.toShort(value);
		} else if (Integer.class.isAssignableFrom(aClass)) {
			return (T) ObjectConverter.toInteger(value);
		} else if (Long.class.isAssignableFrom(aClass)) {
			return (T) ObjectConverter.toLong(value);
		} else if (Float.class.isAssignableFrom(aClass)) {
			return (T) ObjectConverter.toFloat(value);
		} else if (Double.class.isAssignableFrom(aClass)) {
			return (T) ObjectConverter.toDouble(value);
		} else if (String.class.isAssignableFrom(aClass)) {
			if (value != null) {
				String s = value.toString();
				if (s.length() > 0) {
					return (T) s;
				}
			}
			return null;
		} else {
			if (value == null || aClass.isInstance(value)) {
				return aClass.cast(value);
			} else {
				throw new ClassCastException("Cannot convert " + value.getClass().getName() + " to " + aClass.getName());
			}
		}
	}

	public static <T> T getField(Object instance, String fieldName) {
		Exception reason = null;
		try {
			Field field = getFieldDescriptor(instance, fieldName);
			return (T) field.get(instance);
		} catch (NoSuchFieldException e) {
			// lets try use the getter method
			String c = Strings.capitalize(fieldName);
			Class<? extends Object> clazz = instance.getClass();
			Method method = null;
			try {
				method = clazz.getMethod("get" + c);
			} catch (Exception e1) {
				// ignore
			}
			if (method == null) {
				try {
					method = clazz.getMethod("is" + c);
				} catch (Exception e1) {
					// ignore
				}
			}
			if (method != null) {
				try {
					return (T) method.invoke(instance);
				} catch (Exception e1) {
					reason = e1;
				}
			} else {
				reason = e;
			}
		} catch (Exception e) {
			reason = e;
		}
		Activator.getLogger().debug("Could not get field: " + fieldName + " on " + instance + ". Reason: " + reason,
				reason);
		return null;
	}

	public static void setField(Object instance, String fieldName, Object value) {
		if (value instanceof HasValue) {
			// only set field if there is a value
			HasValue has = (HasValue) value;
			if (!has.hasValue()) {
				return;
			}
		}

		try {
			Field field = getFieldDescriptor(instance, fieldName);
			Object convertedValue = convertTo(value, field.getType());
			field.set(instance, convertedValue);
		} catch (Exception e) {
			Activator.getLogger().debug(
					"Could not set field: " + fieldName + " on " + instance + " to value: " + value + ". Reason: " + e,
					e);
		}
	}

	public static Field getFieldDescriptor(Object instance, String fieldName) throws NoSuchFieldException {
		ObjectHelper.notNull(instance, "instance");
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field;
	}

}

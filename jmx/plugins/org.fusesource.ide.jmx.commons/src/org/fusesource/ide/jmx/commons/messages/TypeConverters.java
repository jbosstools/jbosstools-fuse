/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.commons.messages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.jmx.commons.Activator;


public class TypeConverters {
	protected static Map<String, Function1<String, Object>> stringToTypeFunctions = new ConcurrentHashMap<String, Function1<String, Object>>();

	// parse dates like: Sun May 22 10:25:39 BST 2011
	protected static SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd hh:mm:ss zzz yyyy");

	static {
		stringToTypeFunctions.put("java.lang.Boolean",
				new Function1<String, Object>() {
					@Override
					public Object apply(String argument) {
						return Boolean.parseBoolean(argument);
					}
				});
		stringToTypeFunctions.put("java.lang.Byte",
				new Function1<String, Object>() {
					@Override
					public Object apply(String argument) {
						return Byte.parseByte(argument);
					}
				});
		stringToTypeFunctions.put("java.lang.Short",
				new Function1<String, Object>() {
					@Override
					public Object apply(String argument) {
						return Short.parseShort(argument);
					}
				});
		stringToTypeFunctions.put("java.lang.Integer",
				new Function1<String, Object>() {
					@Override
					public Object apply(String argument) {
						return Integer.parseInt(argument);
					}
				});
		stringToTypeFunctions.put("java.lang.Long",
				new Function1<String, Object>() {
					@Override
					public Object apply(String argument) {
						return Long.parseLong(argument);
					}
				});
		stringToTypeFunctions.put("java.lang.Float",
				new Function1<String, Object>() {
					@Override
					public Object apply(String argument) {
						return Float.parseFloat(argument);
					}
				});
		stringToTypeFunctions.put("java.lang.Double",
				new Function1<String, Object>() {
					@Override
					public Object apply(String argument) {
						return Double.parseDouble(argument);
					}
				});
		stringToTypeFunctions.put("java.util.Date",
				new Function1<String, Object>() {
					@Override
					public Object apply(String argument) {
						try {
							return dateFormat.parse(argument);
						} catch (ParseException e) {
							Activator.getLogger().warning("Failed to parse date: " + argument + ". Reason: " + e, e);
							return null;
						}
					}
				});
	}

	public static Object stringToType(String text, String typeName) {
		if (typeName == null) {
			return null;
		}
		Function1<String, Object> fn = stringToTypeFunctions.get(typeName);
		if (fn != null) {
			if (Strings.isBlank(text)) {
				return null;
			} else {
				return fn.apply(text);
			}
		}
		return text;
	}

}

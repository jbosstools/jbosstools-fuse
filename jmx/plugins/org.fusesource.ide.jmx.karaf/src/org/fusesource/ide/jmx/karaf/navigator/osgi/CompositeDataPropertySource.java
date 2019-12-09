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

package org.fusesource.ide.jmx.karaf.navigator.osgi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.properties.BooleanPropertyDescriptor;
import org.fusesource.ide.foundation.ui.properties.ComplexPropertyDescriptor;
import org.fusesource.ide.foundation.ui.properties.ListPropertyDescriptor;
import org.fusesource.ide.foundation.ui.properties.NumberPropertyDescriptor;
import org.fusesource.ide.jmx.karaf.KarafJMXPlugin;

public class CompositeDataPropertySource implements IPropertySource {
	final CompositeData cd;
	private IPropertyDescriptor[] descriptors;
	private Set<String> keyNames = new HashSet<>();

	private static Map<String, Class<?>> nameToClassIndex = new HashMap<>();

	private static void addClasses(Class<?>... classes) {
		for (Class<?> aClass : classes) {
			nameToClassIndex.put(aClass.getName(), aClass);
		}
	}

	static {
		addClasses(String.class, Byte.class, Character.class, Short.class, Integer.class, Long.class, Float.class, Double.class, Date.class);
	}

	public CompositeDataPropertySource(CompositeData cd) {
		this.cd = cd;
		List<IPropertyDescriptor> properties = new ArrayList<>();
		final CompositeType compositeType = cd.getCompositeType();
		Set<?> keys = compositeType.keySet();
		for (Object key : keys) {
			String keyText = key.toString();
			keyNames.add(keyText);
			final OpenType<?> type = compositeType.getType(keyText);
			if (type != null) {
				final String className = type.getClassName();
				IPropertyDescriptor descriptor;
				if (type.isArray()) {
					descriptor = new ListPropertyDescriptor(key, keyText);
				} else if ("java.lang.String".equals(className)) {
					descriptor = new TextPropertyDescriptor(key, keyText);
				} else if ("java.lang.Boolean".equals(className)) {
					descriptor = new BooleanPropertyDescriptor(key, keyText);
				} else if (isNumberProperty(className)) {
					descriptor = new NumberPropertyDescriptor(key, keyText);
				} else {
					Class<?> aClass = nameToClassIndex.get(className);
					if (aClass != null) {
						descriptor = new ComplexPropertyDescriptor(key, keyText, aClass);
					} else {
						descriptor = new PropertyDescriptor(key, keyText);
					}
				}
				properties.add(descriptor);
			}
		}
		descriptors = properties.toArray(new IPropertyDescriptor[properties.size()]);
	}
	
	private boolean isNumberProperty(String className) {
		return 	"java.lang.byte".equalsIgnoreCase(className) ||
				"java.lang.short".equalsIgnoreCase(className) ||
				"java.lang.integer".equalsIgnoreCase(className) ||
				"java.lang.long".equalsIgnoreCase(className) ||
				"java.lang.float".equalsIgnoreCase(className);
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id) {
		final String idText = Strings.getOrElse(id);
		if (!keyNames.contains(idText)) {
			KarafJMXPlugin.getLogger().error("Error: No such key " + idText + " on " + this + " with keys: " + keyNames);
			return null;
		}
		final Object answer = cd.get(idText);
		if (answer instanceof Object[]) {
			return Arrays.asList((Object[]) answer);
		}
		return answer;
	}

	@Override
	public boolean isPropertySet(Object id) {
		Object value = getPropertyValue(id);
		return value != null;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
	}

}

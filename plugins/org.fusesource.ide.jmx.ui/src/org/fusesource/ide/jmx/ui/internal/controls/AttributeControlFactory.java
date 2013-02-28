/*******************************************************************************
 * Copyright (c) 2007 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Benjamin Walstrum (issue #24)
 *******************************************************************************/

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

package org.fusesource.ide.jmx.ui.internal.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jmx.ui.extensions.IAttributeControlFactory;
import org.fusesource.ide.jmx.ui.extensions.IWritableAttributeHandler;


public class AttributeControlFactory {

	private static final Map<String, List<IAttributeControlFactory>> typeFactories;
	private static final Map<String, Map<Pattern, IAttributeControlFactory>> patternFactories;

	private static final IAttributeControlFactory defaultFactory = new TextControlFactory();
	private static final IAttributeControlFactory arrayFactory = new ArrayControlFactory();

	static {
		Map<String, List<IAttributeControlFactory>> typeMap =
				new HashMap<String, List<IAttributeControlFactory>>();
		Map<String, IAttributeControlFactory> idMap =
				new HashMap<String, IAttributeControlFactory>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint epDisplays =
				registry.getExtensionPoint("org.fusesource.ide.jmx.ui.attribute.controls"); //$NON-NLS-1$
				for (IConfigurationElement element : epDisplays.getConfigurationElements()) {
					String id = element.getAttribute("id"); //$NON-NLS-1$
					String type = element.getAttribute("type"); //$NON-NLS-1$
					IAttributeControlFactory factory = null;
					try {
						factory = (IAttributeControlFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						continue;
					}
					if (factory != null) {
						List<IAttributeControlFactory> displays = typeMap.get(type);
						if (displays == null) {
							displays = new ArrayList<IAttributeControlFactory>(1);
							typeMap.put(type, displays);
						}
						displays.add(factory);
					}
					idMap.put(id, factory);
				}

				Map<String, Map<Pattern, IAttributeControlFactory>> patternMap =
						new HashMap<String, Map<Pattern, IAttributeControlFactory>>();

				IExtensionPoint bindings =
						registry.getExtensionPoint("org.fusesource.ide.jmx.ui.attribute.bindings"); //$NON-NLS-1$
				for (IConfigurationElement element : bindings.getConfigurationElements()) {
					IAttributeControlFactory factory = idMap.get(element.getAttribute("controlID")); //$NON-NLS-1$
					String name = element.getAttribute("name"); //$NON-NLS-1$
					Pattern pattern = Pattern.compile(element.getAttribute("objectName")); //$NON-NLS-1$

					Map<Pattern, IAttributeControlFactory> factoryMap = patternMap.get(name);
					if (factoryMap == null) {
						factoryMap = new HashMap<Pattern, IAttributeControlFactory>();
						patternMap.put(name, factoryMap);
					}
					factoryMap.put(pattern, factory);
				}

				typeFactories = Collections.unmodifiableMap(typeMap);
				patternFactories = Collections.unmodifiableMap(patternMap);
	}

	public static Control createControl(final Composite parent, final Object value) {
		return createControl(parent, value, value.getClass().getSimpleName(),
				null, null, false, null, null);
	}

	public static Control createControl(final Composite parent, final Object value,
			final String type, final String objectName, final String attributeName,
			final boolean writable, final IWritableAttributeHandler handler,
			final FormToolkit toolkit) {
		IAttributeControlFactory factory = null;

		if (objectName != null && attributeName != null) {
			Map<Pattern, IAttributeControlFactory> patterns = patternFactories.get(attributeName);
			if (patterns != null) {
				for (Map.Entry<Pattern, IAttributeControlFactory> entry : patterns.entrySet()) {
					if (entry.getKey().matcher(objectName).matches()) {
						factory = entry.getValue();
					}
				}
			}
		}

		if (factory == null) {
			Class<? extends Object> clazz = value == null ? getClass(type) : value.getClass();
			if (clazz == null) {
				factory = defaultFactory;
			} else {
				List<IAttributeControlFactory> factories = findFactories(clazz);
				if (factories == null) {
					factory = (value != null && value.getClass().isArray()) ? arrayFactory : defaultFactory;
				} else {
					factory = factories.get(0);
				}
			}
		}

		return factory.createControl(parent, toolkit, writable, type, value, handler);
	}

	private static List<IAttributeControlFactory> findFactories(final Class<? extends Object> valueClass) {
		for (Map.Entry<String, List<IAttributeControlFactory>> entry : typeFactories.entrySet()) {
			try {
				if (Class.forName(entry.getKey()).isAssignableFrom(valueClass)) {
					return entry.getValue();
				}
			} catch (ClassNotFoundException e) {
				JMXUIActivator.log(IStatus.WARNING, e.getMessage(), e);
			}
		}
		return null;
	}

	private static Class<? extends Object> getClass(final String type) {
		Class<? extends Object> result = null;
		if (type.equals("int")) { //$NON-NLS-1$
			result = Integer.class;
		} else if (type.equals("boolean")) { //$NON-NLS-1$
			result = Boolean.class;
		} else if (type.equals("short")) { //$NON-NLS-1$
			result = Short.class;
		} else if (type.equals("long")) { //$NON-NLS-1$
			result = Long.class;
		} else if (type.equals("char")) { //$NON-NLS-1$
			result = Character.class;
		} else if (type.equals("byte")) { //$NON-NLS-1$
			result = Byte.class;
		} else if (type.equals("float")) { //$NON-NLS-1$
			result = Float.class;
		} else {
			try {
				result = Class.forName(type);
			} catch (ClassNotFoundException e) {}
		}
		return result;
	}

}

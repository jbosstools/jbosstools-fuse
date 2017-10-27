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

package org.fusesource.ide.foundation.ui.propsrc;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.management.AttributeNotFoundException;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.properties.BooleanPropertyDescriptor;
import org.fusesource.ide.foundation.ui.properties.ComplexPropertyDescriptor;
import org.fusesource.ide.foundation.ui.properties.PropertyDescriptors;


public class BeanTypePropertyMetadata {
	private static Map<Class<?>, BeanTypePropertyMetadata> cache = new WeakHashMap<>();

	private Map<String, IPropertyDescriptor> propertyMap = new HashMap<>();
	private Map<String, PropertyDescriptor> descriptorMap = new HashMap<>();
	private TreeMap<String, IPropertyDescriptor> sortedMap = new TreeMap<>();
	private IPropertyDescriptor[] properties;

	public static synchronized BeanTypePropertyMetadata beanMetadata(Class<?> beanType) throws IntrospectionException {
		BeanTypePropertyMetadata answer = cache.get(beanType);
		if (answer == null) {
			answer = new BeanTypePropertyMetadata(beanType);
			cache.put(beanType, answer);
		}
		return answer;
	}

	public BeanTypePropertyMetadata(Class<?> beanType) throws IntrospectionException {
		BeanInfo beanInfo = Introspector.getBeanInfo(beanType);
		PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {
			Class<?> propertyType = descriptor.getPropertyType();
			if (propertyType == null) {
				continue;
			}

			String id = descriptor.getName();
			String displayName = PropertyDescriptors.getReadablePropertyName(descriptor);
			IPropertyDescriptor propertyDest = null;
			if (propertyType == Boolean.class || propertyType == boolean.class) {
				propertyDest = new BooleanPropertyDescriptor(id, displayName);
			} else if (propertyType == String.class) {
				propertyDest = new TextPropertyDescriptor(id, displayName);
			} else if (propertyType.isPrimitive() || Number.class.isAssignableFrom(propertyType) || Date.class.isAssignableFrom(propertyType)) {
				propertyDest = new ComplexPropertyDescriptor(id, displayName, propertyType);
			} else {
				// TODO support other property types??
				FoundationUIActivator.pluginLog().logInfo("Ignoring property for " + beanType.getName() + " of name: " + displayName + " of type" + propertyType.getName());
			}
			descriptorMap.put(id, descriptor);
			if (propertyDest != null) {
				propertyMap.put(id, propertyDest);
				sortedMap.put(displayName, propertyDest);
			}
		}
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (properties == null) {
			Collection<IPropertyDescriptor> values = sortedMap.values();
			properties = values.toArray(new IPropertyDescriptor[values.size()]);
		}
		return properties;
	}

	public Object getPropertyValue(Object bean, Object id) {
		if (bean != null) {
			PropertyDescriptor desc = descriptorMap.get(id);
			if (desc != null) {
				try {
					Method method = desc.getReadMethod();
					if (method != null) {
						return method.invoke(bean);
					}
				} catch (Exception e) {
					if (isAttributeNotFound(e)) {
						FoundationUIActivator.pluginLog().logInfo("Attribute not supported; probably older version of the code? getter: " + id + " on " + bean + ". " + e);
					} else {
						FoundationUIActivator.pluginLog().logWarning("Could not invoke getter " + id + " on " + bean + ". " + e, e);
					}
				}
			}
		}
		return null;
	}

	private boolean isAttributeNotFound(Throwable e) {
		if (e instanceof AttributeNotFoundException) {
			return true;
		}
		Throwable cause = e.getCause();
		if (cause instanceof UndeclaredThrowableException) {
			UndeclaredThrowableException ue = (UndeclaredThrowableException) cause;
			return isAttributeNotFound(ue.getUndeclaredThrowable());
		}
		if (cause instanceof InvocationTargetException) {
			InvocationTargetException ie = (InvocationTargetException) cause;
			return isAttributeNotFound(ie.getTargetException());
		}
		if (cause != null) {
			return isAttributeNotFound(cause);
		}
		return false;
	}

	public boolean isPropertySet(Object bean, Object id) {
		return getPropertyValue(bean, id) != null;
	}

	public void setPropertyValue(Object bean, Object id, Object value) {
		PropertyDescriptor desc = descriptorMap.get(id);
		if (desc != null) {
			Method method = desc.getWriteMethod();
			if (method != null) {
				try {
					method.invoke(bean, value);
				} catch (Exception e) {
					FoundationUIActivator.pluginLog().logWarning("Could not invoke setter " + method + " on " + bean + ". " + e, e);
				}
			}
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.foundation.core.internal.FoundationCoreActivator;
import org.jboss.tools.foundation.core.internal.Trace;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * @author lhein
 */
public abstract class BundleUtils {

	public static void startBundle(BundleContext context, String containsName) {
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			String name = bundle.getSymbolicName();
			if (name.contains(containsName)) {
				Trace.trace(Trace.STRING_FINER, "About to start bundle: " + name);
				try {
					bundle.start();
				} catch (Exception e) {
					FoundationCoreActivator.pluginLog().logError("Failed to start: " + e.getMessage(), e);
				}
			}
		}
	}

	public static void stopBundle(BundleContext context, String containsName) {
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			String name = bundle.getSymbolicName();
			if (name.contains(containsName)) {
				Trace.trace(Trace.STRING_FINER, "About to stop bundle: " + name);
				try {
					bundle.stop();
				} catch (Exception e) {
					FoundationCoreActivator.pluginLog().logError("Failed to start: " + e.getMessage(), e);
				}
			}
		}
	}

	public static <T> T lookupService(BundleContext context, Class<T> aClass) {
		return lookupService(context, aClass.getName(), aClass);
	}

	public static <T> T lookupService(BundleContext context, String name, Class<T> aClass) {
		ServiceReference<?> ref = context.getServiceReference(name);
		return getServiceFromRef(context, name, aClass, ref);
	}

	public static <T> List<T> lookupServices(BundleContext context, Class<T> aClass) throws InvalidSyntaxException {
		return lookupServices(context, aClass.getName(), aClass);
	}

	public static <T> List<T> lookupServices(BundleContext context, String name, Class<T> aClass) throws InvalidSyntaxException {
		String filter = null;
		ServiceReference<?>[] refs = context.getServiceReferences(name, filter);
		List<T> answer = new ArrayList<T>();
		if (refs != null) {
			for (ServiceReference<?> ref: refs) {
				T service = getServiceFromRef(context, name, aClass, ref);
				if (service != null) {
					answer.add(service);
				}
			}
		}
		return answer;
	}

	public static <T> Map<T,Map<String,Object>> lookupServicesMap(BundleContext context, Class<T> aClass) throws InvalidSyntaxException {
		return lookupServicesMap(context,  aClass.getName(), aClass);
	}

	public static <T> Map<T,Map<String,Object>> lookupServicesMap(BundleContext context, String name, Class<T> aClass) throws InvalidSyntaxException {
		String filter = null;
		ServiceReference<?>[] refs = context.getServiceReferences(name, filter);
		Map<T,Map<String,Object>> answer = new HashMap<T,Map<String,Object>>();
		if (refs != null) {
			for (ServiceReference<?> ref: refs) {
				T service = getServiceFromRef(context, name, aClass, ref);
				if (service != null) {
					Map<String,Object> properties = getPropertiesMap(ref);
					answer.put(service, properties);
				}
			}
		}
		return answer;
	}

	
	public static <T> Collection<ServiceReference<T>> findServiceReferences(BundleContext context, Class<T> c) throws InvalidSyntaxException {
		String filter = MessageFormat.format("(&(objectClass={0}))", c.getCanonicalName());
		context.createFilter(filter);
		return context.getServiceReferences(c, filter);
	}
	
	public static Properties getProperties(ServiceReference<?> ref) {
		Properties properties = new Properties();
		String[] keys = ref.getPropertyKeys();
		for (String key : keys) {
			properties.put(key, ref.getProperty(key));
		}
		return properties;
	}
	public static Map<String,Object> getPropertiesMap(ServiceReference<?> ref) {
		Map<String,Object> properties = new HashMap<>();
		String[] keys = ref.getPropertyKeys();
		for (String key : keys) {
			properties.put(key, ref.getProperty(key));
		}
		return properties;
	}


	protected static <T> T getServiceFromRef(BundleContext context, String name, Class<T> aClass, ServiceReference<?> ref) {
		if (ref == null) {
			Trace.trace(Trace.STRING_FINER,"No service for " + name);
		} else {
			Object value = context.getService(ref);
			if (aClass.isInstance(value)) {
				return aClass.cast(value);
			} else {
				FoundationCoreActivator.pluginLog().logWarning("Service for " + name + " is not an instanceof " + aClass.getCanonicalName() + " but was: " + value);
			}
		}
		return null;
	}

	/**
	 * retrieves a resource from the given bundle
	 * 
	 * @param bundleSymbolicName
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	public static File getFileFromBundle(String bundleSymbolicName, String path) throws CoreException {
		Bundle bundle = Platform.getBundle(bundleSymbolicName);
		URL url = null;
		try {
			URL fileUrl = FileLocator.find(bundle, new Path(path), null);
			url = FileLocator.toFileURL(fileUrl);
		} catch (IOException e) {
			String msg = "Cannot find file " + path + " in bundle " + bundle.getSymbolicName();
			IStatus status = new Status(IStatus.ERROR, FoundationCoreActivator.PLUGIN_ID, msg, e);
			throw new CoreException(status);
		}
		String location = url.getFile();
		return new File(location);
	}
}

package org.fusesource.ide.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class Bundles {

	public static void startBundle(BundleContext context, String containsName) {
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			String name = bundle.getSymbolicName();
			if (name.contains(containsName)) {
				Activator.getLogger().debug("About to start bundle: " + name);
				try {
					bundle.start();
				} catch (Exception e) {
					Activator.getLogger().error("Failed to start: " + e.getMessage(), e);
				}
			}
		}
	}

	public static void stopBundle(BundleContext context, String containsName) {
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			String name = bundle.getSymbolicName();
			if (name.contains(containsName)) {
				Activator.getLogger().debug("About to start bundle: " + name);
				try {
					bundle.stop();
				} catch (Exception e) {
					Activator.getLogger().error("Failed to start: " + e.getMessage(), e);
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

	public static Properties getProperties(ServiceReference<?> ref) {
		Properties properties = new Properties();
		String[] keys = ref.getPropertyKeys();
		for (String key : keys) {
			properties.put(key, ref.getProperty(key));
		}
		return properties;
	}
	public static Map<String,Object> getPropertiesMap(ServiceReference<?> ref) {
		Map<String,Object> properties = new HashMap<String,Object>();
		String[] keys = ref.getPropertyKeys();
		for (String key : keys) {
			properties.put(key, ref.getProperty(key));
		}
		return properties;
	}


	protected static <T> T getServiceFromRef(BundleContext context, String name, Class<T> aClass, ServiceReference<?> ref) {
		if (ref == null) {
			Activator.getLogger().debug("No service for " + name);
		} else {
			Object value = context.getService(ref);
			if (aClass.isInstance(value)) {
				return aClass.cast(value);
			} else {
				Activator.getLogger().warning("Service for " + name + " is not an instanceof " + aClass.getCanonicalName() + " but was: " + value);
			}
		}
		return null;
	}

}

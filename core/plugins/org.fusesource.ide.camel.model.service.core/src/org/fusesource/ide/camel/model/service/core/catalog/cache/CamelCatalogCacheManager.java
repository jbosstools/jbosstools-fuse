/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.cache;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.eclipse.core.resources.IProject;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;

/**
 * @author lhein
 */
public class CamelCatalogCacheManager {

	private static Map<CamelCatalogCoordinates, CamelModel> CACHE = new WeakHashMap<>();

	private static final CamelCatalogCacheManager instance = new CamelCatalogCacheManager();

	protected CamelCatalogCacheManager() {
	}

	public static CamelCatalogCacheManager getInstance() {
		return instance;
	}

	/**
	 * flushes the cache
	 */
	public void flush() {
		CACHE.clear();
	}

	/**
	 * checks whether there is a cached catalog for the given coordinates
	 * 
	 * @param coordinates
	 * @return
	 */
	public boolean hasCachedCatalog(CamelCatalogCoordinates coordinates) {
		return CACHE.containsKey(coordinates);
	}

	/**
	 * returns the cached catalog for the given coordinates or a new catalog
	 * cache if not existing
	 * 
	 * @param coordinates
	 *            the coordinates
	 * @return the cached catalog or an empty one if not yet in cache
	 */
	public synchronized CamelModel getCachedCatalog(CamelCatalogCoordinates coordinates) {
		if (!CACHE.containsKey(coordinates)) {
			initializeCatalog(coordinates);
		}
		return CACHE.get(coordinates);
	}

	/**
	 * returns the default camel catalog
	 * 
	 * @return
	 */
	public synchronized CamelModel getDefaultCamelCatalog() {
		if (!CACHE.containsKey(CamelCatalogUtils.DEFAULT_CAMEL_VERSION)) {
			initializeCatalog(CamelCatalogUtils.getDefaultCatalogCoordinates());
		}
		return CACHE.get(CamelCatalogUtils.getDefaultCatalogCoordinates());
	}
	
	/**
	 * removes the cached catalog for the given coordinates
	 * 
	 * @param coordinates
	 *            the coordinates
	 */
	public void clearCachedCatalog(CamelCatalogCoordinates coordinates) {
		CACHE.remove(coordinates);
	}

	/**
	 * returns the model for the given project 
	 * 
	 * @param project
	 * @return	the model or NULL
	 */
	public CamelModel getCamelModelForProject(IProject project) {
		CamelCatalogCoordinates coords = null;
		if (project == null) {
			coords = CamelCatalogUtils.getDefaultCatalogCoordinates();
		} else {
			coords = CamelCatalogUtils.getCatalogCoordinatesForProject(project);
		}
		if (coords != null) {
			return getCachedCatalog(coords);
		}
		return null;
	}
	
	public CamelModel getCamelModelForVersion(String version) {
		return getCamelModelForVersion(version, CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
	}
	
	public CamelModel getCamelModelForVersion(String version, String runtimeProvider) {
		CamelCatalogCoordinates coords = null;
		if (version == null) {
			coords = CamelCatalogUtils.getDefaultCatalogCoordinates();
		} else {
			coords = CamelCatalogUtils.getCatalogCoordinatesFor("org.apache.camel", "camel-catalog", version);
		}
		if (coords != null) {
			return getCachedCatalog(coords);
		}
		return null;
	}

	
	/**
	 * initializes the catalog for the given coords
	 * 
	 * @param coordinates
	 */
	protected void initializeCatalog(CamelCatalogCoordinates coordinates) {
		CamelModel catalog = new CamelModel();

		Dependency dep = new Dependency();
		dep.setGroupId(coordinates.getGroupId());
		dep.setArtifactId(coordinates.getArtifactId());
		dep.setVersion(coordinates.getVersion());
		catalog = CamelServiceManagerUtil.getManagerService().getCamelModel(coordinates.getVersion(), CamelCatalogUtils.getRuntimeProvider(dep));
//		
//		// load the catalog maven dep into classpath
//		Artifact artifact = new CamelMavenUtils().resolveArtifact(coordinates.getGroupId(), coordinates.getArtifactId(),
//				coordinates.getVersion());
//		loadCatalogFromJar(artifact, catalog);

		CACHE.put(coordinates, catalog);
	}

	protected void loadCatalogFromJar(Artifact artifact, CamelModel catalog) {
//		Thread t = new Thread("tmp");
//		ClassLoader classLoader = t.getContextClassLoader();
//		try {
//			URL catalogFileURL = artifact.getFile().toURI().toURL();
//			// setup classloader for external jar
//			if (classLoader != null && classLoader instanceof URLClassLoader) {
//				URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
//				Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
//				addURL.setAccessible(true);
//				addURL.invoke(urlClassLoader, new Object[] { catalogFileURL });
//			}
//			// get the catalog class and instatiate it
//			Class catalogClass = classLoader.loadClass("org.apache.camel.catalog.DefaultCamelCatalog");
//			Object catalogInstance = catalogClass.getConstructor(new Class[0]).newInstance(null);
//			// retrieve the catalog entries for the model building
//			resolveCatalogEntries(catalog, catalogInstance, "findComponentNames", "componentJsonSchema", Component.class);
//			resolveCatalogEntries(catalog, catalogInstance, "findDataFormatNames", "dataFormatJsonSchema", DataFormat.class);
//			resolveCatalogEntries(catalog, catalogInstance, "findModelNames", "modelJsonSchema", Eip.class);
//			resolveCatalogEntries(catalog, catalogInstance, "findLanguageNames", "languageJsonSchema", Language.class);
//		} catch (Exception ex) {
//			CamelModelServiceCoreActivator.pluginLog().logError(ex);
//		} finally {
//			classLoader = null;
//			t = null;
//		}
	}
	
	protected void resolveCatalogEntries(CamelModel catalog, Object catalogInstance, String namesMethodName, String jsonMethodName, Class clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
//		Method getNamesMethod = catalogInstance.getClass().getMethod(namesMethodName, null);
//		Method getJsonMethod = catalogInstance.getClass().getMethod(jsonMethodName, new Class[] { String.class });
//		Object oNames = getNamesMethod.invoke(catalogInstance, null);
//		if (oNames instanceof List) {
//			List<String> names = (List<String>)oNames;
//			for (String name : names) {
//				Object oJson = getJsonMethod.invoke(catalogInstance, new Object[] { name });
//				if (oJson instanceof String) {
//					if (clazz.getName().endsWith(".Component")) {
//						Component o = Component.getJSONFactoryInstance(new ByteArrayInputStream(oJson.toString().getBytes()));
//						catalog.addComponent(o);						
//					} else if (clazz.getName().endsWith(".DataFormat")) {
//						DataFormat o = DataFormat.getJSONFactoryInstance(new ByteArrayInputStream(oJson.toString().getBytes()));
//						catalog.addDataFormat(o);
//					} else if (clazz.getName().endsWith(".Eip")) {
//						Eip o = Eip.getJSONFactoryInstance(new ByteArrayInputStream(oJson.toString().getBytes()));
//						catalog.addEip(o);
//					} else if (clazz.getName().endsWith(".Language")) {
//						Language o = Language.getJSONFactoryInstance(new ByteArrayInputStream(oJson.toString().getBytes()));
//						catalog.addLanguage(o);
//					} else {
//						// unsupported
//					}
//				}
//			}
//		}
	}
}

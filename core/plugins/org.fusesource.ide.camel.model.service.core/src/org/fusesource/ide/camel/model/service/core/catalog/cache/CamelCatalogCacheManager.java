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

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.eclipse.core.resources.IProject;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;

/**
 * @author lhein
 */
public class CamelCatalogCacheManager {

	private static Map<CamelCatalogCoordinates, CamelModel> camelModelCache = new HashMap<>();

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
		camelModelCache.clear();
	}

	/**
	 * returns the cached catalog for the given coordinates or a new catalog
	 * cache if not existing
	 * 
	 * @param coordinates
	 *            the coordinates
	 * @return the cached catalog or an empty one if not yet in cache
	 */
	private synchronized CamelModel getCachedCatalog(CamelCatalogCoordinates coordinates) {
		if (!camelModelCache.containsKey(coordinates)) {
			initializeCatalog(coordinates);
		}
		return camelModelCache.get(coordinates);
	}

	/**
	 * removes the cached catalog for the given coordinates
	 * 
	 * @param coordinates
	 *            the coordinates
	 */
	public void clearCachedCatalog(CamelCatalogCoordinates coordinates) {
		camelModelCache.remove(coordinates);
	}

	/**
	 * returns the model for the given project 
	 * 
	 * @param project
	 * @return	the model or NULL
	 */
	public CamelModel getCamelModelForProject(IProject project) {
		CamelCatalogCoordinates coords;
		if (project == null) {
			coords = CamelCatalogUtils.getDefaultCatalogCoordinates();
		} else {
			coords = CamelCatalogUtils.getCatalogCoordinatesForProject(project);
		}
		// initialize repos for the dep lookup
		if (project != null) {
			CamelServiceManagerUtil.getManagerService().updateMavenRepositoryLookup(CamelMavenUtils.getRepositories(project), coords);
		}
		if (coords != null) {
			return getCachedCatalog(coords);
		}
		return null;
	}
	
	public CamelModel getDefaultCamelModel(String version) {
		CamelCatalogCoordinates coords = CamelCatalogUtils.getDefaultCatalogCoordinates();
		coords.setVersion(version);
		if (CamelCatalogUtils.isCamelVersionWithoutProviderSupport(version) ) {
			coords.setArtifactId(CamelCatalogUtils.CATALOG_CAMEL_ARTIFACTID);
		}
		return getCachedCatalog(coords);
	}
	
	/**
	 * initializes the catalog for the given coords
	 * 
	 * @param coordinates
	 */
	protected void initializeCatalog(CamelCatalogCoordinates coordinates) {
		Dependency dep = new Dependency();
		dep.setGroupId(coordinates.getGroupId());
		dep.setArtifactId(coordinates.getArtifactId());
		dep.setVersion(coordinates.getVersion());
		
		CamelModel catalog = CamelServiceManagerUtil.getManagerService().getCamelModel(coordinates.getVersion(), CamelCatalogUtils.getRuntimeProviderFromDependency(dep));

		System.err.println("Loaded catalog for version: " + coordinates);

		camelModelCache.put(coordinates, catalog);
	}
}

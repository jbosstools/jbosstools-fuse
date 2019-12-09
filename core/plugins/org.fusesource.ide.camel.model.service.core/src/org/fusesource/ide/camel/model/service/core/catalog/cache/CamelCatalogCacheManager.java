/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Repository;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.model.service.core.CamelServiceManagerUtil;
import org.fusesource.ide.camel.model.service.core.internal.Messages;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.CamelMavenUtils;

/**
 * @author lhein
 */
public class CamelCatalogCacheManager {

	private static Map<CamelCatalogCoordinates, CamelModel> camelModelCache = new HashMap<>();

	private static final CamelCatalogCacheManager instance = new CamelCatalogCacheManager();
	private static CamelModel lastRetrievedCamelCatalog;

	protected CamelCatalogCacheManager() {
	}

	public static CamelCatalogCacheManager getInstance() {
		return instance;
	}

	/**
	 * flushes the cache
	 */
	public void clear() {
		camelModelCache.clear();
	}

	/**
	 * returns the cached catalog for the given coordinates or a new catalog
	 * cache if not existing
	 * 
	 * @param coordinates
	 *            the coordinates
	 * @param monitor 
	 * @return the cached catalog or an empty one if not yet in cache
	 */
	private synchronized CamelModel getCachedCatalog(CamelCatalogCoordinates coordinates, IProgressMonitor monitor) {
		if (!camelModelCache.containsKey(coordinates)) {
			initializeCatalog(coordinates, monitor);
		}
		CamelModel camelModel = camelModelCache.get(coordinates);
		lastRetrievedCamelCatalog = camelModel;
		return camelModel;
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
	 * @deprecated prefer to use the version with progress Monitor as first load can take several minutes
	 */
	@Deprecated
	public CamelModel getCamelModelForProject(IProject project) {
		return getCamelModelForProject(project, new NullProgressMonitor());
	}
	
	public CamelModel getDefaultCamelModel(String version) {
		CamelCatalogCoordinates coords = CamelCatalogUtils.getDefaultCatalogCoordinates();
		coords.setVersion(version);
		if (CamelCatalogUtils.isCamelVersionWithoutProviderSupport(version) ) {
			coords.setArtifactId(CamelCatalogUtils.CATALOG_CAMEL_ARTIFACTID);
		}
		return getCachedCatalog(coords, new NullProgressMonitor());
	}
	
	/**
	 * initializes the catalog for the given coords
	 * 
	 * @param coordinates
	 * @param monitor 
	 */
	protected void initializeCatalog(CamelCatalogCoordinates coordinates, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, NLS.bind(Messages.initializingCamelModel, coordinates.getVersion()), 1);
		Dependency dep = new Dependency();
		dep.setGroupId(coordinates.getGroupId());
		dep.setArtifactId(coordinates.getArtifactId());
		dep.setVersion(coordinates.getVersion());
		
		camelModelCache.put(coordinates, CamelServiceManagerUtil.getManagerService().getCamelModel(coordinates.getVersion(), CamelCatalogUtils.getRuntimeProviderFromDependency(dep)));
		subMonitor.setWorkRemaining(0);
	}
	
	public CamelModel getCamelModelForProject(IProject project, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, Messages.retrievingCamelModel, 3);
		CamelCatalogCoordinates coords;
		if (project == null) {
			if(lastRetrievedCamelCatalog != null) {
				return lastRetrievedCamelCatalog;
			}
			coords = CamelCatalogUtils.getDefaultCatalogCoordinates();
		} else {
			coords = CamelCatalogUtils.getCatalogCoordinatesForProject(project, subMonitor.split(1));
			// initialize repos for the dep lookup
			List<Repository> mavenRepositories = new CamelMavenUtils().getRepositories(project, subMonitor.split(1));
			CamelServiceManagerUtil.getManagerService().updateMavenRepositoryLookup(mavenRepositories, coords);
		}

		subMonitor.setWorkRemaining(1);
		if (coords != null) {
			return getCachedCatalog(coords, subMonitor.split(1));
		}
		subMonitor.setWorkRemaining(0);
		return null;
	}

	/**
	 *  /!\ public for test purpose
	 *  
	 * @return
	 */
	public Map<CamelCatalogCoordinates, CamelModel> getCachedCatalog() {
		return camelModelCache;
	}
	
	/**
	 *  /!\ public for test purpose
	 *  
	 * @param cachedCatalog
	 */
	public static void setCachedCatalog(Map<CamelCatalogCoordinates, CamelModel> cachedCatalog) {
		camelModelCache = cachedCatalog;
	}

}

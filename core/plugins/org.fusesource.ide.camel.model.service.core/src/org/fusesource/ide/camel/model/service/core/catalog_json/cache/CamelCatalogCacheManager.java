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
package org.fusesource.ide.camel.model.service.core.catalog_json.cache;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author lhein
 */
public class CamelCatalogCacheManager {
	
	private static Map<CamelCatalogCoordinates, CamelCatalogCache> CACHE = new WeakHashMap<>();
	
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
	 * returns the cached catalog for the given coordinates or a new catalog cache if not existing
	 * 
	 * @param coordinates	the coordinates
	 * @return	the cached catalog or an empty one if not yet in cache
	 */
	public CamelCatalogCache getCachedCatalog(CamelCatalogCoordinates coordinates) {
		if (!CACHE.containsKey(coordinates)) {
			CACHE.put(coordinates, new CamelCatalogCache());
		}
		return CACHE.get(coordinates);
	}
	
	/**
	 * removes the cached catalog for the given coordinates
	 * 
	 * @param coordinates	the coordinates
	 */
	public void clearCachedCatalog(CamelCatalogCoordinates coordinates) {
		CACHE.remove(coordinates);
	}
}

/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Repository;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author André Dietisheim
 */
public class CamelManagerServiceProxy extends ServiceTracker<ICamelManagerService, ICamelManagerService>
		implements ICamelManagerService {

	private String serviceVersion;
	public CamelManagerServiceProxy(BundleContext context, String serviceVersion) throws InvalidSyntaxException {
		super(
				context,
				context.createFilter(MessageFormat
						.format("(&(objectClass={0})(camel.version={1}))", ICamelManagerService.class.getCanonicalName(), serviceVersion)), null); //$NON-NLS-1$
		this.serviceVersion = serviceVersion;
		open();
	}
	
	public String getServiceVersion() {
		return serviceVersion;
	}

    private ICamelManagerService checkedGetService() throws CamelManagerException {
    	ICamelManagerService service = getService();
		if (service == null) {
			throw new CamelManagerException("Could not acquire Camel Management service"); //$NON-NLS-1$
		}
		return service;
	}

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelModel(java.lang.String, java.lang.String)
     */
    @Override
    public CamelModel getCamelModel(String camelVersion, String runtimeProvider) {
    	return checkedGetService().getCamelModel(camelVersion, runtimeProvider);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelModel(java.lang.String)
     */
    @Override
    public CamelModel getCamelModel(String camelVersion) {
    	return getCamelModel(camelVersion, CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#updateMavenRepositoryLookup(java.util.List, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
     */
    @Override
    public void updateMavenRepositoryLookup(List<Repository> repositories, CamelCatalogCoordinates coords) {
    	checkedGetService().updateMavenRepositoryLookup(repositories, coords);
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelSchemaProvider(org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
     */
    @Override
    public CamelSchemaProvider getCamelSchemaProvider(CamelCatalogCoordinates coords) {
    	return checkedGetService().getCamelSchemaProvider(coords);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointUri(java.lang.String, java.util.Map, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
     */
    @Override
    public String createEndpointUri(String scheme, Map<String, String> properties, CamelCatalogCoordinates coords)
    		throws URISyntaxException {
    	return checkedGetService().createEndpointUri(scheme, properties, coords);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointXml(java.lang.String, java.util.Map, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
     */
    @Override
    public String createEndpointXml(String scheme, Map<String, String> properties, CamelCatalogCoordinates coords)
    		throws URISyntaxException {
    	return checkedGetService().createEndpointXml(scheme, properties, coords);
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getEndpointProperties(java.lang.String, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
     */
    @Override
    public Map<String, String> getEndpointProperties(String uri, CamelCatalogCoordinates coords)
    		throws URISyntaxException {
    	return checkedGetService().getEndpointProperties(uri, coords);
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getEndpointScheme(java.lang.String, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
     */
    @Override
    public String getEndpointScheme(String uri, CamelCatalogCoordinates coords) {
    	return checkedGetService().getEndpointScheme(uri, coords);
    }
    
    /*
     * (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#testExpression(java.lang.String, java.lang.String)
     */
    @Override
    public String testExpression(String language, String expression) {
    	return checkedGetService().testExpression(language, expression);
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointUri(java.lang.String, java.util.Map, boolean, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
     */
    @Override
    public String createEndpointUri(String scheme, Map<String, String> properties, boolean encode,
    		CamelCatalogCoordinates coords) throws URISyntaxException {
		return checkedGetService().createEndpointUri(scheme, properties, encode, coords);
	}

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointXml(java.lang.String, java.util.Map, boolean, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
     */
    @Override
    public String createEndpointXml(String scheme, Map<String, String> properties, boolean encode,
    		CamelCatalogCoordinates coords) throws URISyntaxException {
		return checkedGetService().createEndpointXml(scheme, properties, encode, coords);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#durationToMillis(java.lang.String)
	 */
	@Override
	public long durationToMillis(String duration) {
		return checkedGetService().durationToMillis(duration);
	}

	@Override
	public Map<String, Object> parseQuery(String uri) throws URISyntaxException{
		return checkedGetService().parseQuery(uri);
	}

	@Override
	public String createQuery(Map<String, Object> parameters) throws URISyntaxException {
		return checkedGetService().createQuery(parameters);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#isCamelVersionExisting(java.lang.String)
	 */
	@Override
	public boolean isCamelVersionExisting(String camelVersion) {
		return checkedGetService().isCamelVersionExisting(camelVersion);
	}
}

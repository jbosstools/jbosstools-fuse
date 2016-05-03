/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.converter.TimePatternConverter;
import org.fusesource.ide.camel.model.service.core.CamelSchemaProvider;
import org.fusesource.ide.camel.model.service.core.ICamelManagerService;
import org.fusesource.ide.camel.model.service.core.adopters.CamelModelLoader;
import org.fusesource.ide.camel.model.service.core.adopters.XmlCamelModelLoader;
import org.fusesource.ide.camel.model.service.core.catalog.CamelModel;

/**
 * @author lhein
 */
public class CamelService implements ICamelManagerService {
	
	private static final boolean ENCODE_DEFAULT = false;
	
	private CamelModelLoader loader;
	private CamelCatalog catalog;
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelModel()
	 */
	@Override
	public CamelModel getCamelModel() {
		if (this.loader == null) this.loader = new XmlCamelModelLoader();
		try {
			return loader.getCamelModel(	getComponentModelURL(), 
											getEipModelURL(), 
											getLanguageModelURL(), 
											getDataFormatModelURL());
		} catch (IOException ex) {
			CamelServiceImplementationActivator.pluginLog().logError(ex);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelSchemaProvider()
	 */
	@Override
	public CamelSchemaProvider getCamelSchemaProvider() {
		if (catalog == null) catalog = new DefaultCamelCatalog();
		return new CamelSchemaProvider(catalog.blueprintSchemaAsXml(), catalog.springSchemaAsXml());
	}	
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointUri(java.lang.String, java.util.Map)
	 */
	@Override
	public String createEndpointUri(String scheme, Map<String, String> properties) throws URISyntaxException {
		if (catalog == null) catalog = new DefaultCamelCatalog();
		return catalog.asEndpointUri(scheme, properties, ENCODE_DEFAULT);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointUri(java.lang.String, java.util.Map, boolean)
	 */
	@Override
	public String createEndpointUri(String scheme, Map<String, String> properties, boolean encode)
			throws URISyntaxException {
		if (catalog == null) catalog = new DefaultCamelCatalog();
		return catalog.asEndpointUri(scheme, properties, encode);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getEndpointProperties(java.lang.String)
	 */
	@Override
	public Map<String, String> getEndpointProperties(String uri) throws URISyntaxException {
		if (catalog == null) catalog = new DefaultCamelCatalog();
		return catalog.endpointProperties(uri);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointXml(java.lang.String, java.util.Map)
	 */
	@Override
	public String createEndpointXml(String scheme, Map<String, String> properties) throws URISyntaxException {
		if (catalog == null) catalog = new DefaultCamelCatalog();
		return catalog.asEndpointUriXml(scheme, properties, ENCODE_DEFAULT);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointXml(java.lang.String, java.util.Map, boolean)
	 */
	@Override
	public String createEndpointXml(String scheme, Map<String, String> properties, boolean encode)
			throws URISyntaxException {
		if (catalog == null) catalog = new DefaultCamelCatalog();
		return catalog.asEndpointUriXml(scheme, properties, encode);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getEndpointScheme(java.lang.String)
	 */
	@Override
	public String getEndpointScheme(String uri) {
		if (catalog == null) catalog = new DefaultCamelCatalog();
		return catalog.endpointComponentName(uri);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#testExpression(java.lang.String, java.lang.String)
	 */
	@Override
	public String testExpression(String language, String expression) {
		String result = null;
		org.apache.camel.impl.DefaultCamelContext ctx = new org.apache.camel.impl.DefaultCamelContext();
		try {
			ctx.resolveLanguage(language).createPredicate(expression.replaceAll("\n", "").replaceAll("\r", "").trim());
			result = null;
		} catch (Exception ex) {
			result = ex.getMessage();
		} finally {
			try {
				ctx.shutdown();
			} catch (Exception ex) {
				// ignore
			}
			ctx = null;
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#durationToMillis(java.lang.String)
	 */
	@Override
	public long durationToMillis(String duration) throws IllegalArgumentException {
		return TimePatternConverter.toMilliSeconds(duration);
	}
	
	/**
	 * TODO :	IMPROVE CODE
	 * 
	 * At the moment we just reuse the xml files we already have. In future we will generate the model objects on
	 * the fly using the api functions of camel catalog class. once thats done these constants are obsolete and
	 * should be removed together with the methods for obtaining the files.
	 */
	private static final String CATALOG_FOLDER = "catalogs";
	
	private static final String COMPONENTS_FILENAME = "components.xml";
	private static final String EIPS_FILENAME = "eips.xml";
	private static final String LANGUAGES_FILENAME = "languages.xml";
	private static final String DATAFORMATS_FILENAME = "dataformats.xml";
	
	private static final String COMPONENTS_CATALOG_FILE = String.format("%s/%s", CATALOG_FOLDER, COMPONENTS_FILENAME);
	private static final String EIPS_CATALOG_FILE = String.format("%s/%s", CATALOG_FOLDER, EIPS_FILENAME);
	private static final String LANGUAGES_CATALOG_FILE = String.format("%s/%s", CATALOG_FOLDER, LANGUAGES_FILENAME);
	private static final String DATAFORMATS_CATALOG_FILE = String.format("%s/%s", CATALOG_FOLDER, DATAFORMATS_FILENAME);

	private URL getComponentModelURL() {
		return CamelServiceImplementationActivator.getDefault().getBundle().getEntry(COMPONENTS_CATALOG_FILE);
	}
	
	private URL getEipModelURL() {
		return CamelServiceImplementationActivator.getDefault().getBundle().getEntry(EIPS_CATALOG_FILE);
	}
	
	private URL getDataFormatModelURL() {
		return CamelServiceImplementationActivator.getDefault().getBundle().getEntry(DATAFORMATS_CATALOG_FILE);
	}
	
	private URL getLanguageModelURL() {
		return CamelServiceImplementationActivator.getDefault().getBundle().getEntry(LANGUAGES_CATALOG_FILE);
	}
}

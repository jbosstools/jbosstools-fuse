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

import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.catalog.maven.MavenVersionManager;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fusesource.ide.camel.model.service.core.CamelSchemaProvider;
import org.fusesource.ide.camel.model.service.core.ICamelManagerService;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.TimePatternConverter;
import org.fusesource.ide.camel.model.service.core.util.URISupport;

/**
 * @author lhein
 */
public class CamelService implements ICamelManagerService {
	
	private static final boolean ENCODE_DEFAULT = false;

	private MavenVersionManager versionManager;
	private CamelCatalog catalog;

	/**
	 * initializing
	 */
	public CamelService() {
		catalog = new DefaultCamelCatalog(true);
		versionManager = new MavenVersionManager();
		List<List<String>> additionalM2Repos = getAdditionalRepos();
		for (List<String> repo : additionalM2Repos) {
			versionManager.addMavenRepository(repo.get(0), repo.get(1));
		}
		catalog.setVersionManager(versionManager);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelModel(java.lang.String)
	 */
	@Override
	public CamelModel getCamelModel(String camelVersion) {
		return this.getCamelModel(camelVersion, CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelModel(java.lang.String, java.lang.String)
	 */
	@Override
	public CamelModel getCamelModel(String camelVersion, String runtimeProvider) {
		catalog.loadVersion(camelVersion);
		loadRuntimeProvider(runtimeProvider, camelVersion);
		return loadCamelModelFromCatalog(catalog);
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
	
	@Override
	public Map<String, Object> parseQuery(String uri) throws URISyntaxException {
		return URISupport.parseQuery(uri);
	}
	
	@Override
	public String createQuery(Map<String, Object> parameters) throws URISyntaxException {
		Map<String, String> params = new HashMap<>();
		for (Entry<String, Object> e : parameters.entrySet()) {
			params.put(e.getKey(), (String)e.getValue());
		}
		return URISupport.createQueryString(params, "&amp;", true);
	}
	
	private void loadRuntimeProvider(String runtimeProvider, String version) {
		CamelCatalogCoordinates coords = CamelCatalogUtils.getCatalogCoordinatesFor(runtimeProvider, version);
		if (!catalog.loadRuntimeProviderVersion(coords.getGroupId(), coords.getArtifactId(), version)) {
			CamelServiceImplementationActivator.pluginLog().logError(String.format("Unable to load the Camel Catalog for %s:%s:%s", coords.getGroupId(), coords.getArtifactId(), coords.getVersion()));
		}
	}
	
	private List<List<String>> getAdditionalRepos() {
		IPreferenceStore s = new ScopedPreferenceStore(new InstanceScope(), "org.fusesource.ide.projecttemplates");
		if (s != null) {
			String repos = s.getString("stagingRepositories");
			return Arrays.asList(repos.split(";"))
					.stream()
					.map(repoName -> Arrays.asList(repoName.split(",")))
					.collect(Collectors.toList());
		}
		return Collections.EMPTY_LIST;
	}
	
	private CamelModel loadCamelModelFromCatalog(CamelCatalog catalog) {
		CamelModel model = new CamelModel();
		
		for (String name : catalog.findComponentNames()) {
			String json = catalog.componentJSonSchema(name);
			Component elem = Component.getJSONFactoryInstance(new ByteArrayInputStream(json.getBytes()));
			model.addComponent(elem);
			System.err.println(elem.getDependencies().get(0).getVersion());
		}
		for (String name : catalog.findDataFormatNames()) {
			String json = catalog.dataFormatJSonSchema(name);
			DataFormat elem = DataFormat.getJSONFactoryInstance(new ByteArrayInputStream(json.getBytes()));
			model.addDataFormat(elem);
		}
		for (String name : catalog.findLanguageNames()) {
			String json = catalog.languageJSonSchema(name);
			Language elem = Language.getJSONFactoryInstance(new ByteArrayInputStream(json.getBytes()));
			model.addLanguage(elem);
		}
		for (String name : catalog.findModelNames()) {
			String json = catalog.modelJSonSchema(name);
			Eip elem = Eip.getJSONFactoryInstance(new ByteArrayInputStream(json.getBytes()));
			model.addEip(elem);
		}
		
		return model;
	}
}


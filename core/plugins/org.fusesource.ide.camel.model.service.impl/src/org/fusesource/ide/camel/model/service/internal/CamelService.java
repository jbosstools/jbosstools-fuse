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
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.catalog.TimePatternConverter;
import org.apache.camel.catalog.URISupport;
import org.apache.camel.catalog.karaf.KarafRuntimeProvider;
import org.apache.camel.catalog.maven.MavenVersionManager;
import org.apache.camel.catalog.springboot.SpringBootRuntimeProvider;
import org.apache.maven.model.Repository;
import org.fusesource.ide.camel.model.service.core.CamelSchemaProvider;
import org.fusesource.ide.camel.model.service.core.ICamelManagerService;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.preferences.StagingRepositoriesUtils;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;

/**
 * @author lhein
 */
public class CamelService implements ICamelManagerService {
	
	private static final String OLDER_CAMEL_VERSION_WITH_CATALOG = "2.15.0";
	private static final String OLDER_CAMEL_CATALOG_VERSION_TO_LOAD_IN_CASE_OF_ERROR = "2.15.6";

	private static final boolean ENCODE_DEFAULT = false;

	private Map<CamelCatalogCoordinates, CamelCatalog> cachedCatalogs = new HashMap<>();
	
	private File tempFolder;

	private IPluginLog logger;

	public CamelService() {
		this(CamelServiceImplementationActivator.pluginLog());
	}
	
	public CamelService(IPluginLog logger) {
		try {
			Path grapeFolder = getGrapeFolderInsideTempFolder();
			Files.createTempDirectory(grapeFolder,"m2repo");
		} catch (IOException ex) {
			logger.logError(ex);
			tempFolder = null;
		}
		this.logger = logger;
	}

	private Path getGrapeFolderInsideTempFolder() throws IOException {
		Path grapeFolder = Paths.get(System.getProperty("java.io.tmpdir"),"grape");
		if(!grapeFolder.toFile().exists()){
			Files.createDirectory(grapeFolder);
		}
		return grapeFolder;
	}
	
	private CamelCatalog getCatalog(CamelCatalogCoordinates coords) {
		if (!cachedCatalogs.containsKey(coords) ) {
			CamelCatalog catalog = new DefaultCamelCatalog(true);
			MavenVersionManager versionManager = new MavenVersionManager();
			if (tempFolder != null) {
				versionManager.setCacheDirectory(tempFolder.getPath());
			}
			configureAdditionalRepos(versionManager);
			catalog.setVersionManager(versionManager);
			String version = coords.getVersion();
			String loadedVersion = loadVersion(catalog, version);
			coords.setVersion(loadedVersion);
			if (!CamelCatalogUtils.isCamelVersionWithoutProviderSupport(loadedVersion)) {
				configureRuntimeprovider(coords, catalog);
			}
			cachedCatalogs.put(coords, catalog);
		}
		return cachedCatalogs.get(coords);
	}

	/**
	 * @param catalog
	 * @param requestedVersion
	 * @return the version which was really loaded for the catalog.
	 */
	private String loadVersion(CamelCatalog catalog, String requestedVersion) {
		if (!catalog.loadVersion(requestedVersion)) {
			if(OLDER_CAMEL_VERSION_WITH_CATALOG.compareTo(requestedVersion) > 1) {
				logger.logError("No catalog available for older version than 2.15.0, the 2.15.6 catalog will be used."); //$NON-NLS-0$
				if(catalog.loadVersion(OLDER_CAMEL_CATALOG_VERSION_TO_LOAD_IN_CASE_OF_ERROR)) {
					return OLDER_CAMEL_CATALOG_VERSION_TO_LOAD_IN_CASE_OF_ERROR;
				} else {
					return loadVersion(catalog, CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY);
				}
			} else {
				logger.logWarning("Unable to load Camel Catalog for version " + requestedVersion); //$NON-NLS-0$
				logger.logWarning("The version "+ CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY +" will be used instead."); //$NON-NLS-0$ //$NON-NLS-1$
				if(catalog.loadVersion(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY)) {
					return CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY;
				} else {
					logger.logError("Unable to load Camel Catalog for version " + CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY + ". Please check your connection and your local .m2 repository."); //$NON-NLS-0$ //$NON-NLS-1$
					return null;
				}
			}
		} else {
			return requestedVersion;
		}
	}

	private void configureRuntimeprovider(CamelCatalogCoordinates coords, CamelCatalog catalog) {
		String runtimeProvider = CamelCatalogUtils.getRuntimeProviderFromDependency(coords.asMavenDependency());
		if (CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT.equalsIgnoreCase(runtimeProvider)) {
			catalog.setRuntimeProvider(new SpringBootRuntimeProvider());
		} else {
			catalog.setRuntimeProvider(new KarafRuntimeProvider());
		}
		if (!catalog.loadRuntimeProviderVersion(coords.getGroupId(), coords.getArtifactId(), coords.getVersion())) {
			logger.logError(String.format("Unable to load the Camel Catalog for %s! Loaded %s as fallback.", coords, catalog.getCatalogVersion()));
		}
	}

	private void configureAdditionalRepos(MavenVersionManager versionManager) {
		List<List<String>> additionalM2Repos = StagingRepositoriesUtils.getAdditionalRepos();
		for (List<String> repo : additionalM2Repos) {
			String repoName = repo.get(0);
			String repoUri = repo.get(1);
			versionManager.addMavenRepository(repoName, repoUri);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelModel(java.lang.String)
	 */
	@Override
	public CamelModel getCamelModelForKarafRuntimeProvider(String camelVersion) {
		return this.getCamelModel(camelVersion, CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelModel(java.lang.String, java.lang.String)
	 */
	@Override
	public CamelModel getCamelModel(String camelVersion, String runtimeProvider) {
		CamelCatalogCoordinates coords = CamelCatalogUtils.getCatalogCoordinatesFor(runtimeProvider, camelVersion);
		CamelCatalog catalog = getCatalog(coords);
		CamelModel loadedModel = loadCamelModelFromCatalog(catalog);
		CamelModelPatcher.applyVersionSpecificCatalogFixes(catalog, loadedModel);
		return loadedModel;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelSchemaProvider(org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
	 */
	@Override
	public CamelSchemaProvider getCamelSchemaProvider(CamelCatalogCoordinates coords) {
		CamelCatalog catalog = getCatalog(coords);
		return new CamelSchemaProvider(catalog.blueprintSchemaAsXml(), catalog.springSchemaAsXml());
	}	
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointUri(java.lang.String, java.util.Map, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
	 */
	@Override
	public String createEndpointUri(String scheme, Map<String, String> properties, CamelCatalogCoordinates coords) throws URISyntaxException {
		CamelCatalog catalog = getCatalog(coords);
		return catalog.asEndpointUri(scheme, properties, ENCODE_DEFAULT);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointUri(java.lang.String, java.util.Map, boolean, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
	 */
	@Override
	public String createEndpointUri(String scheme, Map<String, String> properties, boolean encode,
			CamelCatalogCoordinates coords) throws URISyntaxException {
		CamelCatalog catalog = getCatalog(coords);
		return catalog.asEndpointUri(scheme, properties, encode);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getEndpointProperties(java.lang.String, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
	 */
	@Override
	public Map<String, String> getEndpointProperties(String uri, CamelCatalogCoordinates coords)
			throws URISyntaxException {
		CamelCatalog catalog = getCatalog(coords);
		return catalog.endpointProperties(uri);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointXml(java.lang.String, java.util.Map, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
	 */
	@Override
	public String createEndpointXml(String scheme, Map<String, String> properties, CamelCatalogCoordinates coords)
			throws URISyntaxException {
		CamelCatalog catalog = getCatalog(coords);
		return catalog.asEndpointUriXml(scheme, properties, ENCODE_DEFAULT);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createEndpointXml(java.lang.String, java.util.Map, boolean, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
	 */
	@Override
	public String createEndpointXml(String scheme, Map<String, String> properties, boolean encode,
			CamelCatalogCoordinates coords) throws URISyntaxException {
		CamelCatalog catalog = getCatalog(coords);
		return catalog.asEndpointUriXml(scheme, properties, encode);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getEndpointScheme(java.lang.String, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
	 */
	@Override
	public String getEndpointScheme(String uri, CamelCatalogCoordinates coords) {
		CamelCatalog catalog = getCatalog(coords);
		return catalog.endpointComponentName(uri);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#testExpression(java.lang.String, java.lang.String)
	 */
	@Override
	public String testExpression(String language, String expression) {
		String errorResult = null;
		org.apache.camel.impl.DefaultCamelContext ctx = new org.apache.camel.impl.DefaultCamelContext();
		try {
			ctx.resolveLanguage(language).createPredicate(expression.replaceAll("\n", "").replaceAll("\r", "").trim());
		} catch (Exception ex) {
			errorResult = ex.getMessage();
		} finally {
			try {
				ctx.shutdown();
			} catch (Exception ex) {
				logger.logError(ex);
			}
			ctx = null;
		}
		return errorResult;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#durationToMillis(java.lang.String)
	 */
	@Override
	public long durationToMillis(String duration) {
		return TimePatternConverter.toMilliSeconds(duration);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#parseQuery(java.lang.String)
	 */
	@Override
	public Map<String, Object> parseQuery(String uri) throws URISyntaxException {
		return URISupport.parseQuery(uri);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#createQuery(java.util.Map)
	 */
	@Override
	public String createQuery(Map<String, Object> parameters) throws URISyntaxException {
		Map<String, String> params = new HashMap<>();
		for (Entry<String, Object> e : parameters.entrySet()) {
			params.put(e.getKey(), (String)e.getValue());
		}
		return URISupport.createQueryString(params, Character.toString('&'), ENCODE_DEFAULT);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#updateMavenRepositoryLookup(java.util.List, org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates)
	 */
	@Override
	public void updateMavenRepositoryLookup(List<Repository> repositories, CamelCatalogCoordinates coords) {
		for (Repository repo : repositories) {
			((MavenVersionManager)getCatalog(coords).getVersionManager()).addMavenRepository(repo.getId(), repo.getUrl());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#isCamelVersionExisting(java.lang.String)
	 */
	@Override
	public boolean isCamelVersionExisting(String camelVersion) {
		MavenVersionManager tmpMan = new MavenVersionManager();
		try {
			Path grapeFolder = getGrapeFolderInsideTempFolder();
			Path tmpFolderPath = Files.createTempDirectory(grapeFolder, UUID.randomUUID().toString());
			File tmpFolder = tmpFolderPath.toFile();
			tmpFolder.deleteOnExit();
			tmpMan.setCacheDirectory(tmpFolder.getPath());
			tmpMan.setLog(true);
		} catch (IOException ex) {
			logger.logError(ex);
		} finally {
			for (List<String> rep : StagingRepositoriesUtils.getAdditionalRepos()) {
				tmpMan.addMavenRepository(rep.get(0), rep.get(1));
			}
		}
		return tmpMan.loadVersion(camelVersion);
	}
	
	CamelModel loadCamelModelFromCatalog(CamelCatalog catalog) {
		CamelModel model = new CamelModel();
		loadCamelComponents(catalog, model);
		loadDataformats(catalog, model);
		loadLanguages(catalog, model);
		loadEips(catalog, model);
		return model;
	}

	private void loadEips(CamelCatalog catalog, CamelModel model) {
		for (String name : catalog.findModelNames()) {
			String json = catalog.modelJSonSchema(name);
			Eip elem = Eip.getJSONFactoryInstance(new ByteArrayInputStream(getUnicodeEncodedStreamIfPossible(json)));
			model.addEip(elem);
		}
	}

	private void loadLanguages(CamelCatalog catalog, CamelModel model) {
		for (String name : catalog.findLanguageNames()) {
			String json = catalog.languageJSonSchema(name);
			Language elem = Language.getJSONFactoryInstance(new ByteArrayInputStream(getUnicodeEncodedStreamIfPossible(json)));
			model.addLanguage(elem);
		}
	}

	private void loadDataformats(CamelCatalog catalog, CamelModel model) {
		for (String name : catalog.findDataFormatNames()) {
			String json = catalog.dataFormatJSonSchema(name);
			DataFormat elem = DataFormat.getJSONFactoryInstance(new ByteArrayInputStream(getUnicodeEncodedStreamIfPossible(json)));
			model.addDataFormat(elem);
		}
	}

	private void loadCamelComponents(CamelCatalog catalog, CamelModel model) {
		for (String name : catalog.findComponentNames()) {
			String json = catalog.componentJSonSchema(name);
			Component elem = Component.getJSONFactoryInstance(new ByteArrayInputStream(getUnicodeEncodedStreamIfPossible(json)));
			model.addComponent(elem);
		}
	}
	
	private byte[] getUnicodeEncodedStreamIfPossible(String json) {
		return json.getBytes(StandardCharsets.UTF_8);
	}
}


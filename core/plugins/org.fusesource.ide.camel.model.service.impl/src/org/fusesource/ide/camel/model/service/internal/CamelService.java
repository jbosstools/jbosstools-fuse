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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.camel.catalog.TimePatternConverter;
import org.apache.camel.catalog.URISupport;
import org.apache.camel.catalog.maven.MavenVersionManager;
import org.apache.maven.model.Repository;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.model.service.core.CamelSchemaProvider;
import org.fusesource.ide.camel.model.service.core.ICamelManagerService;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCoordinates;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.dataformats.DataFormat;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.catalog.languages.Language;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.impl.ICamelCatalogWrapper;
import org.fusesource.ide.preferences.StagingRepositoriesUtils;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;

/**
 * @author lhein
 */
public class CamelService implements ICamelManagerService {
	
	private static final boolean ENCODE_DEFAULT = false;
	private Map<CamelCatalogCoordinates, ICamelCatalogWrapper> cachedCatalogs = new HashMap<>();
	private IPluginLog logger;

	public CamelService() {
		this(CamelServiceImplementationActivator.pluginLog());
	}
	
	public CamelService(IPluginLog logger) {
		this.logger = logger;
	}
	
	private ICamelCatalogWrapper getCatalog(CamelCatalogCoordinates coords) {
		if (!cachedCatalogs.containsKey(coords)) {
			ICamelCatalogWrapper catalog = getEmbeddedCatalog(coords);
			if (catalog == null) {
				catalog = createCatalogForNotEmbeddedVersions(coords);
			}
			cachedCatalogs.put(coords, catalog);
		}
		return cachedCatalogs.get(coords);
	}

	protected ICamelCatalogWrapper getEmbeddedCatalog(CamelCatalogCoordinates coords) {
		String runtimeProvider = CamelCatalogUtils.getRuntimeProviderFromDependency(coords.asMavenDependency());
		Set<ICamelCatalogWrapper> camelCatalogsEmbedded = CamelCatalogEmbeddedManager.getCamelCatalogsEmbedded();
		for (ICamelCatalogWrapper camelCatalogWrapper : camelCatalogsEmbedded) {
			String loadedVersion = camelCatalogWrapper.getLoadedVersion();
			if(loadedVersion.equals(coords.getVersion()) && runtimeProvider.equals(camelCatalogWrapper.getRuntimeprovider())) {
				return camelCatalogWrapper;
			}
		}
		return null;
	}

	protected DynamicCamelCatalog createCatalogForNotEmbeddedVersions(CamelCatalogCoordinates coords) {
		DynamicCamelCatalog res = new DynamicCamelCatalog(logger);
		String version = coords.getVersion();
		String loadedVersion = loadVersion(res, version);
		coords.setVersion(loadedVersion);
		if (!CamelCatalogUtils.isCamelVersionWithoutProviderSupport(loadedVersion)) {
			configureRuntimeprovider(coords, res);
		}
		return res;
	}

	/**
	 * @param catalog
	 * @param requestedVersion
	 * @return the version which was really loaded for the catalog.
	 */
	private String loadVersion(DynamicCamelCatalog catalog, String requestedVersion) {
		return catalog.loadVersion(requestedVersion);
	}

	private void configureRuntimeprovider(CamelCatalogCoordinates coords, DynamicCamelCatalog catalog) {
		String runtimeProvider = CamelCatalogUtils.getRuntimeProviderFromDependency(coords.asMavenDependency());
		catalog.setRuntimeProvider(runtimeProvider);
		if (!catalog.loadRuntimeProviderVersion(coords.getGroupId(), coords.getArtifactId(), coords.getVersion())) {
			logger.logError(String.format("Unable to load the Camel Catalog for %s! Loaded %s as fallback.", coords, catalog.getLoadedVersion()));
		}
	}

	@Override
	public CamelModel getCamelModelForKarafRuntimeProvider(String camelVersion) {
		return this.getCamelModel(camelVersion, CamelCatalogUtils.RUNTIME_PROVIDER_KARAF);
	}
	
	@Override
	public CamelModel getCamelModel(String camelVersion, String runtimeProvider) {
		return getCamelModel(camelVersion, runtimeProvider, new NullProgressMonitor());
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.ICamelManagerService#getCamelModel(java.lang.String, java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public CamelModel getCamelModel(String camelVersion, String runtimeProvider, IProgressMonitor monitor) {
		CamelCatalogCoordinates coords = CamelCatalogUtils.getCatalogCoordinatesFor(runtimeProvider, camelVersion);
		SubMonitor subMonitor = SubMonitor.convert(monitor, NLS.bind(Messages.loadingCamelModel, coords.getVersion()), 4);
		subMonitor.setWorkRemaining(3);
		ICamelCatalogWrapper catalog = getCatalog(coords);
		subMonitor.setWorkRemaining(2);
		CamelModel loadedModel = loadCamelModelFromCatalog(catalog);
		subMonitor.setWorkRemaining(1);
		CamelModelPatcher.applyVersionSpecificCatalogFixes(catalog, loadedModel);
		subMonitor.setWorkRemaining(0);
		return loadedModel;
	}
	
	@Override
	public CamelSchemaProvider getCamelSchemaProvider(CamelCatalogCoordinates coords) {
		ICamelCatalogWrapper catalog = getCatalog(coords);
		return new CamelSchemaProvider(catalog.blueprintSchemaAsXml(), catalog.springSchemaAsXml());
	}	
	
	@Override
	public Map<String, String> getEndpointProperties(String uri, CamelCatalogCoordinates coords)
			throws URISyntaxException {
		ICamelCatalogWrapper catalog = getCatalog(coords);
		return catalog.endpointProperties(uri);
	}

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
	
	@Override
	public long durationToMillis(String duration) {
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
		return URISupport.createQueryString(params, Character.toString('&'), ENCODE_DEFAULT);
	}
	
	@Override
	public void updateMavenRepositoryLookup(List<Repository> repositories, CamelCatalogCoordinates coords) {
		ICamelCatalogWrapper catalog = getCatalog(coords);
		for (Repository repo : repositories) {
			catalog.addMavenRepositoryToVersionManager(repo.getId(), repo.getUrl());
		}
	}
	
	@Override
	public boolean isCamelVersionExisting(String camelVersion) {
		try (MavenVersionManager tmpMan = new MavenVersionManager()) {
			try {
				Path grapeFolder = new GrapeEnvironmentConfigurator().getGrapeFolderInsideTempFolder();
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
		} catch (IOException ioex) {
			logger.logError(ioex);
		}
		return false;
	}
	
	CamelModel loadCamelModelFromCatalog(ICamelCatalogWrapper catalog) {
		CamelModel model = new CamelModel();
		loadCamelComponents(catalog, model);
		loadDataformats(catalog, model);
		loadLanguages(catalog, model);
		loadEips(catalog, model);
		return model;
	}

	private void loadEips(ICamelCatalogWrapper catalog, CamelModel model) {
		for (String name : catalog.findModelNames()) {
			String json = catalog.modelJSonSchema(name);
			Eip elem = Eip.getJSONFactoryInstance(new ByteArrayInputStream(getUnicodeEncodedStreamIfPossible(json)));
			model.addEip(elem);
		}
	}

	private void loadLanguages(ICamelCatalogWrapper catalog, CamelModel model) {
		for (String name : catalog.findLanguageNames()) {
			String json = catalog.languageJSonSchema(name);
			Language elem = Language.getJSONFactoryInstance(new ByteArrayInputStream(getUnicodeEncodedStreamIfPossible(json)));
			model.addLanguage(elem);
		}
	}

	private void loadDataformats(ICamelCatalogWrapper catalog, CamelModel model) {
		for (String name : catalog.findDataFormatNames()) {
			String json = catalog.dataFormatJSonSchema(name);
			DataFormat elem = DataFormat.getJSONFactoryInstance(new ByteArrayInputStream(getUnicodeEncodedStreamIfPossible(json)));
			model.addDataFormat(elem);
		}
	}

	private void loadCamelComponents(ICamelCatalogWrapper catalog, CamelModel model) {
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


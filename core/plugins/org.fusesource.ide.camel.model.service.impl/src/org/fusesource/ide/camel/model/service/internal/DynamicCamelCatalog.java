/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
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
import java.util.List;
import java.util.Map;

import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.catalog.karaf.KarafRuntimeProvider;
import org.apache.camel.catalog.maven.MavenVersionManager;
import org.apache.camel.catalog.springboot.SpringBootRuntimeProvider;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.impl.ICamelCatalogWrapper;
import org.fusesource.ide.preferences.StagingRepositoriesUtils;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;

public class DynamicCamelCatalog implements ICamelCatalogWrapper {
	
	private static final String OLDER_CAMEL_VERSION_WITH_CATALOG = "2.15.0";
	private static final String OLDER_CAMEL_CATALOG_VERSION_TO_LOAD_IN_CASE_OF_ERROR = "2.15.6";
	
	private CamelCatalog catalog;
	private IPluginLog logger;
	
	public DynamicCamelCatalog(IPluginLog logger) {
		this.logger = logger;
		this.catalog = new DefaultCamelCatalog(true);
		MavenVersionManager versionManager = new MavenVersionManager();
		GrapeEnvironmentConfigurator grapeEnvironmentConfigurator = new GrapeEnvironmentConfigurator();
		try {
			versionManager.setCacheDirectory(grapeEnvironmentConfigurator.getGrapeFolderInsideTempFolder().toFile().getAbsolutePath());
		} catch (IOException e) {
			logger.logError(e);
		}
		configureAdditionalRepos(versionManager);
		catalog.setVersionManager(versionManager);
	}
	
	private void configureAdditionalRepos(MavenVersionManager versionManager) {
		List<List<String>> additionalM2Repos = StagingRepositoriesUtils.getAdditionalRepos();
		for (List<String> repo : additionalM2Repos) {
			String repoName = repo.get(0);
			String repoUri = repo.get(1);
			versionManager.addMavenRepository(repoName, repoUri);
		}
	}

	@Override
	public String getLoadedVersion() {
		return catalog.getLoadedVersion();
	}

	public String loadVersion(String requestedVersion) {
		if (!catalog.loadVersion(requestedVersion)) {
			if(OLDER_CAMEL_VERSION_WITH_CATALOG.compareTo(requestedVersion) > 1) {
				logger.logError("No catalog available for older version than 2.15.0, the 2.15.6 catalog will be used."); //$NON-NLS-0$
				if(catalog.loadVersion(OLDER_CAMEL_CATALOG_VERSION_TO_LOAD_IN_CASE_OF_ERROR)) {
					return OLDER_CAMEL_CATALOG_VERSION_TO_LOAD_IN_CASE_OF_ERROR;
				} else {
					return loadVersion(CamelCatalogUtils.CAMEL_VERSION_LATEST_COMMUNITY);
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

	public boolean loadRuntimeProviderVersion(String groupId, String artifactId, String version) {
		return catalog.loadRuntimeProviderVersion(groupId, artifactId, version);
	}

	@Override
	public Map<String, String> endpointProperties(String uri) throws URISyntaxException {
		return catalog.endpointProperties(uri);
	}

	@Override
	public List<String> findModelNames() {
		return catalog.findModelNames();
	}

	@Override
	public String modelJSonSchema(String name) {
		return catalog.modelJSonSchema(name);
	}

	@Override
	public List<String> findLanguageNames() {
		return catalog.findLanguageNames();
	}

	@Override
	public String languageJSonSchema(String name) {
		return catalog.languageJSonSchema(name);
	}

	@Override
	public List<String> findDataFormatNames() {
		return catalog.findDataFormatNames();
	}

	@Override
	public String dataFormatJSonSchema(String name) {
		return catalog.dataFormatJSonSchema(name);
	}

	@Override
	public List<String> findComponentNames() {
		return catalog.findComponentNames();
	}

	@Override
	public String componentJSonSchema(String name) {
		return catalog.componentJSonSchema(name);
	}

	@Override
	public String blueprintSchemaAsXml() {
		return catalog.blueprintSchemaAsXml();
	}

	@Override
	public String springSchemaAsXml() {
		return catalog.springSchemaAsXml();
	}

	@Override
	public void setRuntimeProvider(String runtimeProvider) {
		if (CamelCatalogUtils.RUNTIME_PROVIDER_KARAF.equals(runtimeProvider)) {
			catalog.setRuntimeProvider(new KarafRuntimeProvider());
		} else {
			catalog.setRuntimeProvider(new SpringBootRuntimeProvider());
		}
	}

	@Override
	public String getRuntimeprovider() {
		if (catalog.getRuntimeProvider() instanceof KarafRuntimeProvider) {
			return CamelCatalogUtils.RUNTIME_PROVIDER_KARAF;
		} else {
			return CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT;
		}
	}

	@Override
	public void addMavenRepositoryToVersionManager(String id, String url) {
		((MavenVersionManager)catalog.getVersionManager()).addMavenRepository(id, url);
	}

}

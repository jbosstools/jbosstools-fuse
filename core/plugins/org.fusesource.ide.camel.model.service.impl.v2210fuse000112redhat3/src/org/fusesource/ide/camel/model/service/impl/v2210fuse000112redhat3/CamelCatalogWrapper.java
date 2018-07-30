package org.fusesource.ide.camel.model.service.impl.v2210fuse000112redhat3;
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
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.camel.catalog.CamelCatalog;
import org.apache.camel.catalog.DefaultCamelCatalog;
import org.apache.camel.catalog.karaf.KarafRuntimeProvider;
import org.apache.camel.catalog.springboot.SpringBootRuntimeProvider;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.impl.ICamelCatalogWrapper;

public class CamelCatalogWrapper implements ICamelCatalogWrapper {
	
	private CamelCatalog camelCatalog;

	public CamelCatalogWrapper() {
		camelCatalog = new DefaultCamelCatalog(true);
	}
	
	public CamelCatalog getCamelCatalog() {
		return camelCatalog;
	}

	@Override
	public String getLoadedVersion() {
		return camelCatalog.getLoadedVersion();
	}

	@Override
	public Map<String, String> endpointProperties(String uri) throws URISyntaxException {
		return camelCatalog.endpointProperties(uri);
	}

	@Override
	public List<String> findModelNames() {
		return camelCatalog.findModelNames();
	}

	@Override
	public String modelJSonSchema(String name) {
		return camelCatalog.modelJSonSchema(name);
	}

	@Override
	public List<String> findLanguageNames() {
		return camelCatalog.findLanguageNames();
	}

	@Override
	public String languageJSonSchema(String name) {
		return camelCatalog.languageJSonSchema(name);
	}

	@Override
	public List<String> findDataFormatNames() {
		return camelCatalog.findDataFormatNames();
	}

	@Override
	public String dataFormatJSonSchema(String name) {
		return camelCatalog.dataFormatJSonSchema(name);
	}

	@Override
	public List<String> findComponentNames() {
		return camelCatalog.findComponentNames();
	}

	@Override
	public String componentJSonSchema(String name) {
		return camelCatalog.componentJSonSchema(name);
	}

	@Override
	public String blueprintSchemaAsXml() {
		return camelCatalog.blueprintSchemaAsXml();
	}

	@Override
	public String springSchemaAsXml() {
		return camelCatalog.springSchemaAsXml();
	}

	@Override
	public void setRuntimeProvider(String runtimeProvider) {
		if (CamelCatalogUtils.RUNTIME_PROVIDER_KARAF.equals(runtimeProvider)) {
			camelCatalog.setRuntimeProvider(new KarafRuntimeProvider());
		} else {
			camelCatalog.setRuntimeProvider(new SpringBootRuntimeProvider());
		}
	}

	@Override
	public String getRuntimeprovider() {
		if (camelCatalog.getRuntimeProvider() instanceof KarafRuntimeProvider) {
			return CamelCatalogUtils.RUNTIME_PROVIDER_KARAF;
		} else {
			return CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT;
		}
	}

	@Override
	public void addMavenRepositoryToVersionManager(String id, String url) {
		//Do nothing
	}

}

/******************************************************************************* 
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.impl;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;


public interface ICamelCatalogWrapper {
	
	public String getLoadedVersion();
	public Map<String, String> endpointProperties(String uri) throws URISyntaxException;
	public List<String> findModelNames();
	public String modelJSonSchema(String name);
	public List<String> findLanguageNames();
	public String languageJSonSchema(String name);
	public List<String> findDataFormatNames();
	public String dataFormatJSonSchema(String name);
	public List<String> findComponentNames();
	public String componentJSonSchema(String name);
	public String blueprintSchemaAsXml();
	public String springSchemaAsXml();
	public void setRuntimeProvider(String runtimeProvider);
	public String getRuntimeprovider();
	public void addMavenRepositoryToVersionManager(String id, String url);
	

}

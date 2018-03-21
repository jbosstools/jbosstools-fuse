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
package org.fusesource.ide.camel.editor.restconfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RestModelBuilder {

	public Map<String, List<Object>> build(CamelFile cf) {
		Map<String, List<Object>> model = new HashMap<>();
		model.put(RestConfigConstants.REST_CONFIGURATION_TAG, new ArrayList<>());
		model.put(RestConfigConstants.REST_TAG, new ArrayList<>());
		if (cf != null && cf.getRouteContainer() != null) {
			Node node = cf.getRouteContainer().getXmlNode();
			if (node instanceof Element) {
				fillForTag(model, RestConfigConstants.REST_CONFIGURATION_TAG, (Element) node);
				fillForTag(model, RestConfigConstants.REST_TAG, (Element) node);
			}
		}
		return model;
	}

	private void fillForTag(Map<String, List<Object>> model, String tag, Element element) {
		NodeList configlist = element.getElementsByTagName(tag);
		if (configlist != null) {
			for (int i=0; i < configlist.getLength(); i++) {
				Element config = (Element) configlist.item(i);
				model.get(tag).add(config);
			}
		}
	}

	public static Map<String, Eip> getRestModelFromCatalog(IProject project, IProgressMonitor monitor) {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		Map<String, Eip> restModel = new HashMap<>();
		CamelModel catalogModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(project, subMon.split(1));
		catalogModel.getEips().stream()
			.filter( (Eip t) -> t.getTags().contains(RestConfigConstants.REST_CONFIGURATION_TAG) || 
								t.getTags().contains(RestConfigConstants.REST_TAG) )
			.forEach( (Eip t) -> restModel.put(t.getName(), t) );
		subMon.setWorkRemaining(0);
		return restModel;
	}
}

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.RestConfigurationElement;
import org.fusesource.ide.camel.model.service.core.model.RestElement;

public class RestModelBuilder {

	public Map<String, List<Object>> build(CamelFile cf) {
		Map<String, List<Object>> model = new HashMap<>();
		model.put(RestConfigurationElement.REST_CONFIGURATION_TAG, new ArrayList<>());
		model.put(RestElement.REST_TAG, new ArrayList<>());
		CamelContextElement ctx = null;
		if (cf.getRouteContainer() instanceof CamelContextElement) {
			ctx = (CamelContextElement)cf.getRouteContainer();
		}
		if (ctx != null && !ctx.getRestConfigurations().isEmpty()) {
			Iterator<AbstractCamelModelElement> rcIter = ctx.getRestConfigurations().values().iterator();
			while (rcIter.hasNext()) {
				AbstractCamelModelElement acme = rcIter.next();
				model.get(RestConfigurationElement.REST_CONFIGURATION_TAG).add(acme);
			}
		}
		if (ctx != null && !ctx.getRestElements().isEmpty()) {
			Iterator<AbstractCamelModelElement> reIter = ctx.getRestElements().values().iterator();
			while (reIter.hasNext()) {
				AbstractCamelModelElement acme = reIter.next();
				model.get(RestElement.REST_TAG).add(acme);
				// then process operations
			}
		}
		return model;
	}

	public static Map<String, Eip> getRestModelFromCatalog(IProject project, IProgressMonitor monitor) {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		Map<String, Eip> restModel = new HashMap<>();
		CamelModel catalogModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(project, subMon.split(1));
		catalogModel.getEips().stream()
			.filter( (Eip t) -> t.getTags().contains(RestConfigurationElement.REST_CONFIGURATION_TAG) || 
								t.getTags().contains(RestElement.REST_TAG) )
			.forEach( (Eip t) -> restModel.put(t.getName(), t) );
		subMon.setWorkRemaining(0);
		return restModel;
	}
}

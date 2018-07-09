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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelCatalogCacheManager;
import org.fusesource.ide.camel.model.service.core.catalog.cache.CamelModel;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.RestConfigurationElement;
import org.fusesource.ide.camel.model.service.core.model.RestElement;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;

public class RestModelBuilder {

	private RestModelBuilder() {
		// util class
	}

	public static Map<String, Eip> getRestModelFromCatalog(IProject project, IProgressMonitor monitor) {
		SubMonitor subMon = SubMonitor.convert(monitor, 2);
		Map<String, Eip> restModel = new HashMap<>();
		CamelModel catalogModel = CamelCatalogCacheManager.getInstance().getCamelModelForProject(project, subMon.split(1));
		catalogModel.getEips().stream()
			.filter( (Eip t) -> t.getTags().contains(RestConfigurationElement.REST_CONFIGURATION_TAG) ||
							    t.getTags().contains(RestVerbElement.CONNECT_VERB) ||
							    t.getTags().contains(RestVerbElement.DELETE_VERB) ||
							    t.getTags().contains(RestVerbElement.GET_VERB) ||
							    t.getTags().contains(RestVerbElement.HEAD_VERB) ||
							    t.getTags().contains(RestVerbElement.OPTIONS_VERB) ||
							    t.getTags().contains(RestVerbElement.PATCH_VERB) ||
							    t.getTags().contains(RestVerbElement.POST_VERB) ||
							    t.getTags().contains(RestVerbElement.PUT_VERB) ||
							    t.getTags().contains(RestVerbElement.TRACE_VERB) ||
								t.getTags().contains(RestElement.REST_TAG) )
			.forEach( (Eip t) -> restModel.put(t.getName(), t) );
		subMon.setWorkRemaining(0);
		return restModel;
	}
}

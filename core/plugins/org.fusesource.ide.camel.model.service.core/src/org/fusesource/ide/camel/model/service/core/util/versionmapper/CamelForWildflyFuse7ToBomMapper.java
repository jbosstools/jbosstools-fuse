/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util.versionmapper;

import java.util.Collections;
import java.util.Map;

import org.fusesource.ide.foundation.core.util.OnlineVersionMapper;

public class CamelForWildflyFuse7ToBomMapper extends OnlineVersionMapper {
	
	private static final String CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_PROPERTY = "org.jboss.tools.fuse.camel2bom.fuse7wildfly.url";
	private static final String CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_DEFAULT_URL = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/camel2bom.fuse7wildfly.properties";
	
	public CamelForWildflyFuse7ToBomMapper() {
		super(CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_PROPERTY, CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_DEFAULT_URL);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		return Collections.emptyMap();
	}

}

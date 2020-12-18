/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.util.versionmapper;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.foundation.core.util.OnlineVersionMapper;

public class CamelForWildflyFuse7ToBomMapper extends OnlineVersionMapper {
	
	private static final String CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_PROPERTY = "org.jboss.tools.fuse.camel2bom.fuse7wildfly.url";
	private static final String CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_DEFAULT_URL = BASE_REPO_CONFIG_URI+"camel2bom.fuse7wildfly.properties";
	
	public CamelForWildflyFuse7ToBomMapper() {
		super(CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_PROPERTY, CAMEL_TO_BOM_MAPPING_FUSE_7_WILDFLY_DEFAULT_URL);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> fallbackMappings = new HashMap<>();
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_700_CAMEL_VERSION, "5.1.0.fuse-000063-redhat-1");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_701_CAMEL_VERSION, "5.1.0.fuse-000083-redhat-3");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_710_CAMEL_VERSION, "5.2.0.fuse-710021-redhat-00001");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_720_CAMEL_VERSION, "5.2.0.fuse-720023-redhat-00001");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_730_CAMEL_VERSION, "5.3.0.fuse-730041-redhat-00001");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_731_CAMEL_VERSION, "5.3.0.fuse-731003-redhat-00002");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_740_CAMEL_VERSION, "5.3.0.fuse-740022-redhat-00002");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_750_CAMEL_VERSION, "5.3.0.fuse-750026-redhat-00001");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_760_CAMEL_VERSION, "5.4.0.fuse-760021-redhat-00001");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_770_CAMEL_VERSION, "5.5.0.fuse-770010-redhat-00003");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_770_CAMEL_VERSION_CAMEL_2_21, "5.5.0.fuse-770010-redhat-00003");
		fallbackMappings.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_780_CAMEL_VERSION, "5.6.0.fuse-780026-redhat-00001");
		return fallbackMappings;
	}

}

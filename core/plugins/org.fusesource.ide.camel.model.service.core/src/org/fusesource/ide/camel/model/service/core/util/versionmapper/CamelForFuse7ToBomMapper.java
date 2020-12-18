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

public class CamelForFuse7ToBomMapper extends OnlineVersionMapper {

	private static final String CAMEL_TO_BOM_MAPPING_FUSE_7_PROPERTY = "org.jboss.tools.fuse.camel2bom.fuse7.url";
	private static final String CAMEL_TO_BOM_MAPPING_FUSE_7_DEFAULT_URL = BASE_REPO_CONFIG_URI+"camel2bom.fuse7.properties";
	
	public CamelForFuse7ToBomMapper() {
		super(CAMEL_TO_BOM_MAPPING_FUSE_7_PROPERTY, CAMEL_TO_BOM_MAPPING_FUSE_7_DEFAULT_URL);
	}
	
	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_700_CAMEL_VERSION, "7.0.0.fuse-000191-redhat-1");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_701_CAMEL_VERSION, "7.0.1.fuse-000011-redhat-3");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_710_CAMEL_VERSION, "7.1.0.fuse-710023-redhat-00001");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_720_CAMEL_VERSION, "7.2.0.fuse-720020-redhat-00001");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_730_CAMEL_VERSION, "7.3.0.fuse-730079-redhat-00001");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_731_CAMEL_VERSION, "7.3.1.fuse-731003-redhat-00003");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_740_CAMEL_VERSION, "7.4.0.fuse-740036-redhat-00002");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_750_CAMEL_VERSION, "7.5.0.fuse-750029-redhat-00002");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_760_CAMEL_VERSION, "7.6.0.fuse-760027-redhat-00001");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_770_CAMEL_VERSION, "7.7.0.fuse-770012-redhat-00003");
		mapping.put(CamelForFuseOnOpenShiftToBomMapper.FUSE_780_CAMEL_VERSION, "7.8.0.fuse-sb2-780038-redhat-00001");
		return mapping;
	}

}

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

public class CamelForFIS20ToBomMapper extends OnlineVersionMapper {
	
	private static final String FIS_MAPPING_PROPERTY = "org.jboss.tools.fuse.fismarker.url";
	private static final String FIS_MAPPING_DEFAULT_URL = BASE_REPO_CONFIG_URI+"fismarker.properties";
	
	static final String FIS_20_R1_CAMEL_VERSION = "2.18.1.redhat-000012";
	static final String FIS_20_R2_CAMEL_VERSION = "2.18.1.redhat-000015";
	public static final String FIS_20_R3_CAMEL_VERSION = "2.18.1.redhat-000021";
	public static final String FIS_20_R4_CAMEL_VERSION = "2.18.1.redhat-000024";
	public static final String FIS_20_R5_CAMEL_VERSION = "2.18.1.redhat-000025";
	public static final String FIS_20_R6_CAMEL_VERSION = "2.18.1.redhat-000026";
	
	public CamelForFIS20ToBomMapper() {
		super(FIS_MAPPING_PROPERTY, FIS_MAPPING_DEFAULT_URL);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put(FIS_20_R1_CAMEL_VERSION, "2.2.170.redhat-000010");
		mapping.put(FIS_20_R2_CAMEL_VERSION, "2.2.170.redhat-000013");
		mapping.put(FIS_20_R3_CAMEL_VERSION, "2.2.170.redhat-000019");
		mapping.put(FIS_20_R4_CAMEL_VERSION, "2.2.170.redhat-000022");
		mapping.put(FIS_20_R5_CAMEL_VERSION, "2.2.170.redhat-000023");
		mapping.put(FIS_20_R6_CAMEL_VERSION, "2.2.170.redhat-000024");
		return mapping;
	}

}

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

public class CamelForFuseOnOpenShiftToBomMapper extends OnlineVersionMapper {

	private static final String FUSE_ON_OPENSHIFT_MAPPING_PROPERTY = "org.jboss.tools.fuse.fismarker.url";
	private static final String FUSE_ON_OPENSHIFT_MAPPING_DEFAULT_URL = BASE_REPO_CONFIG_URI+"camel2bom.fuse7onOpenShift.properties";
	
	public static final String FUSE_700_CAMEL_VERSION = "2.21.0.fuse-000077-redhat-1";
	public static final String FUSE_701_CAMEL_VERSION = "2.21.0.fuse-000112-redhat-3";
	public static final String FUSE_710_CAMEL_VERSION = "2.21.0.fuse-710018-redhat-00001";
	public static final String FUSE_720_CAMEL_VERSION = "2.21.0.fuse-720050-redhat-00001";
	public static final String FUSE_730_CAMEL_VERSION = "2.21.0.fuse-730078-redhat-00001";
	public static final String FUSE_731_CAMEL_VERSION = "2.21.0.fuse-731003-redhat-00003";
	public static final String FUSE_740_CAMEL_VERSION = "2.21.0.fuse-740039-redhat-00001";
	public static final String FUSE_750_CAMEL_VERSION = "2.21.0.fuse-750033-redhat-00001";
	public static final String FUSE_760_CAMEL_VERSION = "2.21.0.fuse-760027-redhat-00001";
	public static final String FUSE_770_CAMEL_VERSION = "2.23.2.fuse-770010-redhat-00001";
	public static final String FUSE_770_CAMEL_VERSION_CAMEL_2_21 = "2.23.2.fuse-770010-redhat-00001";
	public static final String FUSE_780_CAMEL_VERSION = "2.23.2.fuse-780036-redhat-00001";

	public CamelForFuseOnOpenShiftToBomMapper() {
		super(FUSE_ON_OPENSHIFT_MAPPING_PROPERTY, FUSE_ON_OPENSHIFT_MAPPING_DEFAULT_URL);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put(FUSE_700_CAMEL_VERSION, "3.0.11.fuse-000039-redhat-1");
		mapping.put(FUSE_701_CAMEL_VERSION, "3.0.11.fuse-000065-redhat-3");
		mapping.put(FUSE_710_CAMEL_VERSION, "3.0.11.fuse-710023-redhat-00001");
		mapping.put(FUSE_720_CAMEL_VERSION, "3.0.11.fuse-720027-redhat-00001");
		mapping.put(FUSE_730_CAMEL_VERSION, "3.0.11.fuse-730075-redhat-00001");
		mapping.put(FUSE_731_CAMEL_VERSION, "3.0.11.fuse-731003-redhat-00004");
		mapping.put(FUSE_740_CAMEL_VERSION, "3.0.11.fuse-740029-redhat-00002");
		mapping.put(FUSE_750_CAMEL_VERSION, "3.0.11.fuse-750035-redhat-00001");
		mapping.put(FUSE_760_CAMEL_VERSION, "3.0.11.fuse-760025-redhat-00001");
		mapping.put(FUSE_770_CAMEL_VERSION, "3.0.11.fuse-770009-redhat-00002");
		return mapping;
	}

}

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

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.foundation.core.util.OnlineVersionMapper;

public class CamelForFuseOnOpenShiftToBomMapper extends OnlineVersionMapper {

	private static final String FUSE_ON_OPENSHIFT_MAPPING_PROPERTY = "org.jboss.tools.fuse.fismarker.url";
	private static final String FUSE_ON_OPENSHIFT_MAPPING_DEFAULT_URL = BASE_REPO_CONFIG_URI+"camel2bom.fuse7onOpenShift.properties";
	
	public static final String FUSE_700_TP3_CAMEL_VERSION = "2.21.0.000033-fuse-000001-redhat-1";

	public CamelForFuseOnOpenShiftToBomMapper() {
		super(FUSE_ON_OPENSHIFT_MAPPING_PROPERTY, FUSE_ON_OPENSHIFT_MAPPING_DEFAULT_URL);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put(FUSE_700_TP3_CAMEL_VERSION, "2.3.7.fuse-000036-redhat-2");
		return mapping;
	}

}

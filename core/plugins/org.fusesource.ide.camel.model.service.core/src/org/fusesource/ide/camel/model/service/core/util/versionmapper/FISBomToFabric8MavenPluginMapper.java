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

public class FISBomToFabric8MavenPluginMapper extends OnlineVersionMapper {
	
	private static final String FISBOM_TOFABRIC8MAVENPLUGIN_MAPPING_FUSE_7_PROPERTY = "org.jboss.tools.fuse.fisbom2fabric8MavenVersion.fuse7.url";
	private static final String FISBOM_TOFABRIC8MAVENPLUGIN_MAPPING_FUSE_7_DEFAULT_URL = BASE_REPO_CONFIG_URI+"fisBomToFabric8MavenPlugin.fuse7.properties";
	
	
	public FISBomToFabric8MavenPluginMapper() {
		super(FISBOM_TOFABRIC8MAVENPLUGIN_MAPPING_FUSE_7_PROPERTY, FISBOM_TOFABRIC8MAVENPLUGIN_MAPPING_FUSE_7_DEFAULT_URL);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		mapping.put("2.3.7.fuse-000036-redhat-2", "3.5.32.fuse-000040-redhat-2");
		mapping.put("3.0.11.fuse-000039-redhat-1", "3.5.33.fuse-000067-redhat-1");
		mapping.put("3.0.11.fuse-000065-redhat-3", "3.5.33.fuse-000089-redhat-4");
		mapping.put("3.0.11.fuse-710023-redhat-00001","3.5.33.fuse-710023-redhat-00002");
		mapping.put("3.0.11.fuse-720027-redhat-00001","3.5.33.fuse-720026-redhat-00001");
		mapping.put("3.0.11.fuse-730075-redhat-00001", "3.5.33.fuse-730073-redhat-00001");
		mapping.put("3.0.11.fuse-740029-redhat-00002", "3.5.33.fuse-740029-redhat-00001");
		mapping.put("3.0.11.fuse-750035-redhat-00001", "3.5.33.fuse-750033-redhat-00001");
		mapping.put("3.0.11.fuse-760025-redhat-00001", "3.5.33.fuse-760025-redhat-00001");
		mapping.put("3.0.11.fuse-770009-redhat-00002","3.5.42.fuse-770010-redhat-00001");
		return mapping;
	}

}

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

public class DefaultVersions extends OnlineVersionMapper {

	public DefaultVersions() {
		super("org.jboss.fuse.default.versions.url", BASE_REPO_CONFIG_URI+"defaultVersionToSelect.properties");
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		return Collections.singletonMap("camel", CamelForFuseOnOpenShiftToBomMapper.FUSE_720_CAMEL_VERSION);
	}
	
	public String getDefaultCamelVersion() {
		return getMapping().get("camel");
	}

}

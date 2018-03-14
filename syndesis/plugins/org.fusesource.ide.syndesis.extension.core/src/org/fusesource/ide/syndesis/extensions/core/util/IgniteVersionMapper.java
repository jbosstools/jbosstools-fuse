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
package org.fusesource.ide.syndesis.extensions.core.util;

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.foundation.core.util.OnlineVersionMapper;

public class IgniteVersionMapper extends OnlineVersionMapper {
	
	private static final String URL_IGNITE_VERSIONS_FILE = "https://raw.githubusercontent.com/jbosstools/jbosstools-fuse/master/configuration/igniteVersionToDisplayName.properties";

	public IgniteVersionMapper() {
		super("org.jboss.tools.fuse.ignite.url", IgniteVersionMapper.URL_IGNITE_VERSIONS_FILE);
	}

	@Override
	protected Map<String, String> createFallbackMapping() {
		Map<String, String> mapping = new HashMap<>();
		
		mapping.put("1.3-SNAPSHOT", "1.3-SNAPSHOT (Fuse Ignite TP4)");
		
		return mapping;
	}
}

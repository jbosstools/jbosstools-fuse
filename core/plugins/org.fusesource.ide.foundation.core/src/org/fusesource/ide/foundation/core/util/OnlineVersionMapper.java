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
package org.fusesource.ide.foundation.core.util;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.fusesource.ide.foundation.core.internal.FoundationCoreActivator;

public abstract class OnlineVersionMapper {
	
	protected static final String BASE_REPO_CONFIG_URI = "https://raw.githubusercontent.com/bfitzpat/fuseide/FUSETOOLS-3061/configuration/";
	
	private String mappingProperty;
	private String defaultUrl;

	protected  abstract Map<String, String> createFallbackMapping();
	
	/**
	 * @param mappingProperty the system property used to override the default url to use. Very useful when testing.
	 * @param defaultUrl
	 */
	public OnlineVersionMapper(String mappingProperty, String defaultUrl) {
		this.mappingProperty = mappingProperty;
		this.defaultUrl = defaultUrl;
	}

	public String getUrl() {
		return System.getProperty(mappingProperty, defaultUrl);
	}

	public Map<String, String> getMapping() {
		try {
			return createMappingFromOnlineFiles();
		} catch (IOException e) {
			FoundationCoreActivator.pluginLog().logError("Unable to retrieve the mapping from online repo. Falling back to defaults.", e);
			return consolidateMapping(createFallbackMapping());
		}
	}

	protected Map<String, String> createMappingFromOnlineFiles() throws IOException {
		Map<String, String> mapping = new HashMap<>();
		Properties vMapping = new Properties();
		URL url = new URL(getUrl());
		vMapping.load(url.openStream());

		for(String key : vMapping.stringPropertyNames()) {
			String value = vMapping.getProperty(key);
			mapping.put(key, value);
		}
		return consolidateMapping(mapping);
	}
	
	protected Map<String, String> consolidateMapping(Map<String, String> mapping) {
		// default impl...let subclasses override on demand
		return mapping;
	}
}

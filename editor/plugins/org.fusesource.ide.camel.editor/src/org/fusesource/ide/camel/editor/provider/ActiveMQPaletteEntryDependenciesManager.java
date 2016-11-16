/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.fusesource.ide.camel.editor.provider.ext.IDependenciesManager;
import org.fusesource.ide.foundation.core.util.Strings;

public class ActiveMQPaletteEntryDependenciesManager implements IDependenciesManager {

	public static final String RED_HAT_SUFFIX = ".redhat-";
	public static final Map<String, String> camelToAMQVersionMapping;
	// TODO: change me after each release
	public static final String LATEST_AMQ_VERSION = "5.11.0";
	
	static {
		camelToAMQVersionMapping = new HashMap<>();
		camelToAMQVersionMapping.put("2.15.1", "5.11.0");
		camelToAMQVersionMapping.put("2.17.0", "5.11.0");
		camelToAMQVersionMapping.put("2.17.3", "5.11.0");
		// TODO: update me with every new release of camel and amq supported in tooling
	}
	
	static final String ACTIVEMQ_CAMEL = "activemq-camel";
	static final String ORG_APACHE_ACTIVEMQ = "org.apache.activemq";

	public ActiveMQPaletteEntryDependenciesManager() {
		// keep for reflection instanciation
	}

	@Override
	public void updatePluginDependencies(List<Plugin> currentPlugins, String camelVersion) {
		// Do nothing
	}

	@Override
	public void updateDependencies(List<Dependency> currentDependencies, String camelVersion) {
		for (Dependency dependency : currentDependencies) {
			if(isActiveMQCamelDependency(dependency)){
				dependency.setVersion(getActiveMQVersion(camelVersion));
				return;
			}
		}
	}

	private boolean isActiveMQCamelDependency(Dependency dependency) {
		return ORG_APACHE_ACTIVEMQ.equals(dependency.getGroupId()) && ACTIVEMQ_CAMEL.equals(dependency.getArtifactId());
	}

	String getActiveMQVersion(String camelVersion) {
		boolean productizedVersion = camelVersion.indexOf(RED_HAT_SUFFIX) != -1;		
		String key = getVersionWithoutIdentifier(camelVersion);
		String amqVersion = camelToAMQVersionMapping.get(key);
		if(amqVersion != null) {
			if (productizedVersion) {
				return String.format("%s%s%s", amqVersion, RED_HAT_SUFFIX, getBuildNumberFromVersion(camelVersion));
			} else {
				return amqVersion;
			}
		} else {
			//use latest version
			return LATEST_AMQ_VERSION;
		}
	}

	String getBuildNumberFromVersion(String camelVersion) {
		if (camelVersion.split("\\.").length>3) {
			return camelVersion.substring(camelVersion.lastIndexOf('-')+1);
		}
		throw new IllegalArgumentException("Camel version " + camelVersion + " has no valid format for retrieving the build number.");
	}
	
	String getVersionWithoutIdentifier(String camelVersion) {
		if (!Strings.isEmpty(camelVersion)) {
			String[] versionParts = camelVersion.split("\\.");
			if (versionParts.length>2) {
				return String.format("%s.%s.%s", versionParts[0], versionParts[1], versionParts[2]);
			} 
		}
		throw new IllegalArgumentException("Given Camel Version " + camelVersion + " doesn't contain a valid value");
	}
}

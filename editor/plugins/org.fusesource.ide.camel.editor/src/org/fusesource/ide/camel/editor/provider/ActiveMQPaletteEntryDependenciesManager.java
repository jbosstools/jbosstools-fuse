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

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.fusesource.ide.camel.editor.provider.ext.IDependenciesManager;

public class ActiveMQPaletteEntryDependenciesManager implements IDependenciesManager {

	public static final String ACTIVEMQ_VERSION_FOR_CAMEL_2_17 = "5.11.0.redhat-630077";
	public static final String ACTIVEMQ_VERSION_FOR_CAMEL_2_15 = "5.11.0.redhat-621084";
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
		if(camelVersion.startsWith("2.15")){
			return ACTIVEMQ_VERSION_FOR_CAMEL_2_15;
		} else {
			//use latest version
			return ACTIVEMQ_VERSION_FOR_CAMEL_2_17;
		}
	}

}

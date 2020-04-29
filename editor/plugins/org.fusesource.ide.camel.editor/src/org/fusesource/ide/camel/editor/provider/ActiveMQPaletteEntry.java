/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.features.create.ext.CreateEndpointFigureFeature;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;

/**
 * @author lhein
 */
public class ActiveMQPaletteEntry implements ICustomPaletteEntry {

	public static final String ACTIVE_MQ = "ActiveMQ";
	private static final String ORG_APACHE_CAMEL = "org.apache.camel";
	public static final String CAMEL_JMS = "camel-jms";
	public static final String CAMEL_JMS_STARTER = "camel-jms-starter";
	private static final String PROTOCOL = "activemq";

	@Override
	public ICreateFeature newCreateFeature(IFeatureProvider fp) {
		List<Dependency> requiredDependencies = getRequiredDependencies(CamelUtils.getRuntimeProvider(fp));
		return new CreateEndpointFigureFeature(fp, ACTIVE_MQ, "Creates an ActiveMQ endpoint...", "activemq:queue:foo", requiredDependencies);
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	@Override
	public boolean providesProtocol(String protocol) {
		return PROTOCOL.equalsIgnoreCase(protocol);
	}

	@Override
	public List<Dependency> getRequiredDependencies(String runtimeProvider) {
		List<Dependency> deps = new ArrayList<>();
		deps.add(createActiveMQDependency());
		deps.add(createJMSDependency(runtimeProvider));
		return deps;
	}

	private Dependency createJMSDependency(String runtimeProvider) {
		return createDependency(
				ORG_APACHE_CAMEL,
				computeJMSArtifactId(runtimeProvider),
				getCurrentProjectCamelVersion());
	}

	private String computeJMSArtifactId(String runtimeProvider) {
		if(CamelCatalogUtils.RUNTIME_PROVIDER_SPRINGBOOT.equals(runtimeProvider)){
			return CAMEL_JMS_STARTER;
		} else {
			return CAMEL_JMS;
		}
	}

	private Dependency createActiveMQDependency() {
		ActiveMQPaletteEntryDependenciesManager activeMQPaletteEntryDependenciesManager = new ActiveMQPaletteEntryDependenciesManager();
		return createDependency(
				ActiveMQPaletteEntryDependenciesManager.ORG_APACHE_ACTIVEMQ,
				activeMQPaletteEntryDependenciesManager.getArtifactId(),
				activeMQPaletteEntryDependenciesManager.getActiveMQVersion(getCurrentProjectCamelVersion()));
	}

	String getCurrentProjectCamelVersion() {
		return CamelUtils.getCurrentProjectCamelVersion();
	}

	private Dependency createDependency(String groupId, String artifactId, String version) {
		Dependency dep = new Dependency();
		dep.setGroupId(groupId);
		dep.setArtifactId(artifactId);
		dep.setVersion(version);
		return dep;
	}

	@Override
	public boolean isValid(String runtimeProvider) {
		return true;
	}
}

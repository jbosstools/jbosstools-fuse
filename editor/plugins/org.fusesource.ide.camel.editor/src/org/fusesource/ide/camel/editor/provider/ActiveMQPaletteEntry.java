/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
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

/**
 * @author lhein
 */
public class ActiveMQPaletteEntry implements ICustomPaletteEntry {

	private static final String PROTOCOL = "activemq";
	
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ICustomPaletteEntry#newCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
     */
    @Override
    public ICreateFeature newCreateFeature(IFeatureProvider fp) {
        return new CreateEndpointFigureFeature(fp, "ActiveMQ", "Creates an ActiveMQ endpoint...", "activemq:queue:foo", getRequiredDependencies());
    }

    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getProtocol()
     */
    @Override
    public String getProtocol() {
    	return PROTOCOL;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#providesProtocol(java.lang.String)
     */
    @Override
    public boolean providesProtocol(String protocol) {
    	return PROTOCOL.equalsIgnoreCase(protocol);
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry#getRequiredDependencies()
     */
    @Override
    public List<Dependency> getRequiredDependencies() {
        List<Dependency> deps = new ArrayList<>();
        deps.add(createDependency(
        		ActiveMQPaletteEntryDependenciesManager.ORG_APACHE_ACTIVEMQ,
        		ActiveMQPaletteEntryDependenciesManager.ACTIVEMQ_CAMEL,
        		new ActiveMQPaletteEntryDependenciesManager().getActiveMQVersion(CamelUtils.getCurrentProjectCamelVersion())));
        deps.add(createDependency(
        		"org.apache.camel",
        		"camel-jms",
        		CamelUtils.getCurrentProjectCamelVersion()));
        return deps;
    }

    private Dependency createDependency(String groupId, String artifactId, String version) {
    	Dependency dep = new Dependency();
    	dep.setGroupId(groupId);
    	dep.setArtifactId(artifactId);
    	dep.setVersion(version);
    	return dep;
    }
}

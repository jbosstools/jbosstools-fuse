/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.transformation.extensions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;

public class DataTransformationPaletteEntry implements ICustomPaletteEntry {

    private static final String PROTOCOL = "dozer";

    /**
     * {@inheritDoc}
     *
     * @see org.fusesource.ide.camel.editor.provider.ext.ICustomPaletteEntry
     * #newCreateFeature(org.eclipse.graphiti.features.IFeatureProvider)
     */
    @Override
    public ICreateFeature newCreateFeature(IFeatureProvider fp) {
        return new DataMapperEndpointFigureFeature(fp,
                "Data Transformation",
                "Creates a Data Transformation endpoint...", getRequiredDependencies());
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
        Dependency dep = new Dependency();
        dep.setGroupId("org.apache.camel");
        dep.setArtifactId("camel-dozer");
        dep.setVersion(CamelUtils.getCurrentProjectCamelVersion());
        deps.add(dep);
        return deps;
    }
}
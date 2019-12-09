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
package org.fusesource.ide.camel.editor.provider.ext;

import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;

/**
 * @author lhein
 */
public interface ICustomPaletteEntry {
    
    /**
    * @param fp the feature provider
    * @return a new create feature that can be used for creating a new
    * component or implementation of the types supported by this
    * extension; must not be null.
    */
    public ICreateFeature newCreateFeature(IFeatureProvider fp);
    
    /**
    * Returns a list of capabilities required to use this object within a
    * project. The ID's returned will be used to resolve component extensions,
    * adding the listed dependencies to the project's pom.
    *
    * @param runtimeProvider the runtime provider currently used (SringBoot or Karaf for instance) see {@link CamelModelFactory}
    * @return a list of Maven dependencies that are required to use this object within a project.
    */
    public List<Dependency> getRequiredDependencies(String runtimeProvider);
    
    /**
     * returns true if this palette entry creates endpoints with the given
     * protocol (for instance "sap")
     * 
     * @param protocol	the protocol used in the endpoint uri
     * @return	true if this class injected this kind of endpoint to the palette
     */
    public boolean providesProtocol(String protocol);
    
    /**
     * returns the endpoint protocol this entry provides to the palette
     * 
     * @return	the camel endpoint protocol (like "sap" or "smtp")
     */
    public String getProtocol();
    
    /**
     * @param runtimeProvider (SringBoot or Karaf for instance) see {@link CamelModelFactory}
     * @return if the Palette entry should be displayed with the runtime provider provided
     */
    public boolean isValid(String runtimeProvider);
}

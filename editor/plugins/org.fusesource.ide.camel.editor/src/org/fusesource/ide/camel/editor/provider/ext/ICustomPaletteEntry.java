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
package org.fusesource.ide.camel.editor.provider.ext;

import java.util.List;

import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.tb.IImageDecorator;
import org.fusesource.ide.camel.model.connectors.ConnectorDependency;

/**
 * @author lhein
 */
public interface ICustomPaletteEntry {
    
    /**
     * returns the palette category this items goes into
     * 
     * @return
     */
    public String getPaletteCategory();
    
    /**
    * @param fp the feature provider
    * @return a new create feature that can be used for creating a new
    * component or implementation of the types supported by this
    * extension; must not be null.
    */
    public ICreateFeature newCreateFeature(IFeatureProvider fp);
    
    /**
    * @param object the model object.
    * @return an IImageDecorator that represents the model object.
    */
    public IImageDecorator getImageDecorator(Object object);
    
    /**
    * @return the display text for the object type.
    */
    public String getTypeName();
    
    /**
    * @param type the type
    * @return true if this extension supports the specified type.
    */
    public boolean supports(Class type);
    
    /**
    * Returns a list of capabilities required to use this object within a
    * project. The ID's returned will be used to resolve component extensions,
    * adding the listed dependencies to the project's pom.
    *
    * @param object the object being used.
    * @return a list of maven dependencies that are required to use this object within a project.
    */
    public List<ConnectorDependency> getRequiredCapabilities(Object object);
}

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
package org.fusesource.ide.camel.editor.features.create.ext;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.ConnectorEndpoint;
import org.fusesource.ide.camel.model.catalog.components.Component;
import org.fusesource.ide.camel.model.catalog.eips.Eip;
import org.fusesource.ide.commons.util.Strings;

/**
 * @author lhein
 */
public class CreateConnectorFigureFeature extends CreateFigureFeature {
    
    private final ConnectorEndpoint endpoint;
    protected final Component component;
    
    /**
     * creates a component create feature
     * 
     * @param fp
     * @param component
     */
    public CreateConnectorFigureFeature(IFeatureProvider fp, Component component) {
        super(fp, Strings.isBlank(component.getTitle()) ? Strings.humanize(component.getSchemeTitle()) : component.getTitle(), component.getDescription(), (Eip)null);
        this.endpoint = new ConnectorEndpoint(component.getSyntax() != null ? component.getSyntax() : String.format("%s:", component.getScheme())); // we use the first found protocol string
        this.component = component;
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#getIconName()
     */
    @Override
    protected String getIconName() {
        return String.format("%s.png", this.component.getSchemeTitle().replaceAll("-", "_"));
    }
        
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#createNode()
     */
    @Override
    protected AbstractNode createNode() {
        return new ConnectorEndpoint(this.endpoint);
    }
        
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#create(org.eclipse.graphiti.features.context.ICreateContext)
     */
    @Override
    public Object[] create(ICreateContext context) {
        // add maven dependency to pom.xml if needed
        try {
            updateMavenDependencies(component.getDependencies());
        } catch (CoreException ex) {
            Activator.getLogger().error("Unable to add the component dependency to the project maven configuration file.", ex);
        }
        // and then let the super class continue the work
        return super.create(context);
    }
    
    /**
	 * @return the component
	 */
	public Component getConnector() {
		return this.component;
	}
}

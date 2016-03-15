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
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.w3c.dom.Element;

/**
 * @author lhein
 */
public class CreateConnectorFigureFeature extends CreateFigureFeature {
    
    protected final Component component;
    
    /**
     * creates a component create feature
     * 
     * @param fp
     * @param component
     */
    public CreateConnectorFigureFeature(IFeatureProvider fp, Component component) {
		super(fp, component.getDisplayTitle(), component.getDescription(), (Eip) null);
        this.component = component;        
        setEip(getEipByName("to"));
    }
        
//    /* (non-Javadoc)
//     * @see org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
//     */
//    @Override
//    public boolean canCreate(ICreateContext context) {
//    	return true;
//    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.CreateFigureFeature#getIconName()
     */
    @Override
    protected String getIconName() {
        return this.component.getSchemeTitle();
    }
        
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature#createNode(org.fusesource.ide.camel.model.service.core.model.CamelModelElement, boolean)
     */
    @Override
    protected AbstractCamelModelElement createNode(AbstractCamelModelElement parent, boolean createDOMNode) {
    	if( getEip() != null ) {
			CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
			if (editor.getModel() != null) { 
				Element newNode = null;
				if (createDOMNode) {
					newNode = editor.getModel().createElement(getEip().getName(), parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
				}
				CamelEndpoint ep = new CamelEndpoint(component.getSyntax() != null ? component.getSyntax() : String.format("%s:", component.getScheme())); // we use the first found protocol string
				ep.setParent(parent);
				ep.setUnderlyingMetaModelObject(getEip());
				if (createDOMNode) {
					ep.setXmlNode(newNode);
					ep.updateXMLNode();
				}
				return ep;
			}
		}
        return null;
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
            CamelEditorUIActivator.pluginLog().logError("Unable to add the component dependency to the project maven configuration file.", ex);
        }
        // and then let the super class continue the work
        return super.create(context);
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature#getCategoryName()
     */
    @Override
    public String getCategoryName() {
    	return "components";
    }
    
    /**
	 * @return the component
	 */
	public Component getConnector() {
		return this.component;
	}
}

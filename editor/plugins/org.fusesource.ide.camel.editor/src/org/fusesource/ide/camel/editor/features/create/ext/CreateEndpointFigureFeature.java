/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.features.create.ext;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.model.CamelEndpoint;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
import org.w3c.dom.Node;


public class CreateEndpointFigureFeature extends CreateFigureFeature {
	private String endpointUri;
	private List<Dependency> deps;

	/**
	 * 
	 * @param fp
	 * @param name
	 * @param description
	 * @param endpointUri
	 * @param deps	optional dependencies...if not applicable hand over null or empty list
	 */
	public CreateEndpointFigureFeature(IFeatureProvider fp, String name, String description, String endpointUri, List<Dependency> deps) {
		super(fp, name, description, (Class<? extends CamelModelElement>)null);
		this.endpointUri = endpointUri;
		this.deps = deps;
		setEip(getEipByName("from"));
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature#canCreate(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public boolean canCreate(ICreateContext context) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature#createNode(org.fusesource.ide.camel.model.service.core.model.CamelModelElement, boolean)
	 */
	@Override
	protected CamelModelElement createNode(CamelModelElement parent, boolean createDOMNode) {
		CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
		if (editor.getModel() != null) { 
			Node newNode = null;
			if (createDOMNode) {
				newNode = editor.getModel().createElement(getEip().getName(), parent != null && parent.getXmlNode() != null ? parent.getXmlNode().getPrefix() : null);
			}
			CamelEndpoint ep = new CamelEndpoint(this.endpointUri);
			ep.setParent(parent);
			ep.setUnderlyingMetaModelObject(getEip());
			if (createDOMNode) {
				ep.setXmlNode(newNode);
				ep.updateXMLNode();
			}
			return ep;
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature#getIconName()
	 */
	@Override
	protected String getIconName() {
		return new CamelEndpoint(endpointUri).getIconName();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature#create(org.eclipse.graphiti.features.context.ICreateContext)
	 */
	@Override
	public Object[] create(ICreateContext context) {
		if (this.deps != null && this.deps.isEmpty() == false) {
			// add maven dependency to pom.xml if needed
	        try {
	            updateMavenDependencies(this.deps);
	        } catch (CoreException ex) {
	            CamelEditorUIActivator.pluginLog().logError("Unable to add the component dependency to the project maven configuration file.", ex);
	        }
		}
		return super.create(context);
	}
}

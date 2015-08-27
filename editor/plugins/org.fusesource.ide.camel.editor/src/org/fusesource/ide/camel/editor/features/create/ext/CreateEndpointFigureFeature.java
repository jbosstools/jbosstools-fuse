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
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.service.core.catalog.Dependency;


public class CreateEndpointFigureFeature extends CreateFigureFeature {
	private final Endpoint endpoint;
	private List<Dependency> deps;

	/**
	 * 
	 * @param fp
	 * @param name
	 * @param description
	 * @param endpoint
	 * @param deps	optional dependencies...if not applicable hand over null or empty list
	 */
	public CreateEndpointFigureFeature(IFeatureProvider fp, String name, String description, Endpoint endpoint, List<Dependency> deps) {
		super(fp, name, description, (Class<? extends AbstractNode>)null);
		this.endpoint = endpoint;
		this.deps = deps;
	}

	@Override
	protected AbstractNode createNode() {
		return new Endpoint(endpoint);
	}
	
	protected String getIconName() {
		return endpoint.getIconName();
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
	            Activator.getLogger().error("Unable to add the component dependency to the project maven configuration file.", ex);
	        }
		}
		return super.create(context);
	}
}

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

package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.CamelModelLoader;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.RouteSupport;


public class ImportCamelContextElementsCommand extends RecordingCommand {

	private final RiderDesignEditor designEditor;
	private TransactionalEditingDomain editingDomain;
	private Resource createdResource;
	private Diagram diagram;
	private IFeatureProvider featureProvider;

	public ImportCamelContextElementsCommand(RiderDesignEditor designEditor, TransactionalEditingDomain editingDomain, Diagram diagram) {
		super(editingDomain);
		this.diagram = diagram;
		this.designEditor = designEditor;
		this.editingDomain = editingDomain;
	}

	@Override
	protected void doExecute() {
		// lets use the route ID in the URI so we can switch between route diagrams without caching issues
		RouteSupport selectedRoute = designEditor.getSelectedRoute();
		/*
		String id = selectedRoute.getId();
		if (id == null || id.length() == 0) {
			id = "#" + selectedRoute.hashCode();
		}
		URI uri = URI.createPlatformResourceURI(designEditor.getCamelContextFile().getFullPath().toString() + "_" + id, true);
		 */

		URI uri = URI.createPlatformResourceURI(designEditor.getCamelContextURI(), true);

		createdResource = editingDomain.getResourceSet().createResource(uri);
		createdResource.getContents().add(diagram);

		featureProvider = designEditor.getFeatureProvider();

		CamelModelLoader bpmnFileReader = new CamelModelLoader(diagram, featureProvider);
		System.out.println("Loading diagram: " + diagram + " with route: " + selectedRoute + " # " + System.identityHashCode(selectedRoute));
		try {
			bpmnFileReader.loadModel(selectedRoute);
		} catch (RuntimeException e) {
			Activator.getLogger().error("Failed to load model: " + e, e);
			//throw e;
		}
	}

	public IFeatureProvider getFeatureProvider() {
		return featureProvider;
	}

	/**
	 * @return the createdResource
	 */
	public Resource getCreatedResource() {
		return createdResource;
	}

	public Diagram getDiagram() {
		return diagram;
	}
}

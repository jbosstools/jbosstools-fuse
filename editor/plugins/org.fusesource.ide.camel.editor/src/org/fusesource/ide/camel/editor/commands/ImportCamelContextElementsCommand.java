/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.internal.CamelDiagramLoader;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElementIDUtil;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;

/**
 * @author lhein
 */
public class ImportCamelContextElementsCommand extends RecordingCommand {

	private TransactionalEditingDomain editingDomain;
	private CamelDesignEditor designEditor;
	private AbstractCamelModelElement container;
	private Resource createdResource;
	private Diagram diagram;
	private IFeatureProvider featureProvider;
	private CamelFile camelContextFile;
	
	/**
	 * 
	 * @param project
	 * @param editingDomain
	 * @param diagramName
	 * @param camelContextFile
	 */
	public ImportCamelContextElementsCommand(CamelDesignEditor designEditor, TransactionalEditingDomain editingDomain, CamelFile camelContextFile) {
		this(designEditor, editingDomain, camelContextFile, null);
	}
	
	/**
	 * 
	 * @param project
	 * @param editingDomain
	 * @param diagramName
	 * @param camelContextFile
	 */
	public ImportCamelContextElementsCommand(CamelDesignEditor designEditor, TransactionalEditingDomain editingDomain, CamelFile camelContextFile, Diagram diagram) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.editingDomain = editingDomain;
		this.camelContextFile = camelContextFile;
		this.diagram = diagram;
	}
	
	/**
	 * 
	 * @param project
	 * @param editingDomain
	 * @param diagramName
	 * @param camelContextFile
	 */
	public ImportCamelContextElementsCommand(CamelDesignEditor designEditor, TransactionalEditingDomain editingDomain, AbstractCamelModelElement container, Diagram diagram) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.editingDomain = editingDomain;
		this.camelContextFile = container.getCamelFile();
		this.diagram = diagram;
		this.container = container;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 */
	@Override
	protected void doExecute() {
		CamelRouteContainerElement context = null;
		try {
			designEditor.getParent().stopDirtyListener();
			if (designEditor.getModel() != null) designEditor.getModel().unregisterDOMListener();
			
			// Create the diagram and its file
			String diagramName = "CamelContext";
			diagram = Graphiti.getPeCreateService().createDiagram("CamelContext", diagramName, true); //$NON-NLS-1$
			java.net.URI camelContextlocationURI = camelContextFile.getResource().getLocationURI();
			URI uri = URI.createPlatformResourceURI(camelContextlocationURI != null ? camelContextlocationURI.getPath() : camelContextFile.getResource().getFullPath().toOSString(), true);
			createdResource = editingDomain.getResourceSet().createResource(uri);
			createdResource.getContents().add(diagram);

			IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(diagram, "org.fusesource.ide.camel.editor.dtp.id"); //$NON-NLS-1$
			IFeatureProvider featureProvider = dtp.getFeatureProvider();
			CamelDiagramLoader diagramReader = new CamelDiagramLoader(diagram, featureProvider);
			try {
				context = camelContextFile.getChildElements().isEmpty() ? null : (CamelRouteContainerElement)camelContextFile.getChildElements().get(0);
				diagramReader.loadModel(editingDomain, this.container != null && this.container instanceof CamelFile == false ? this.container : context);
			} catch (Exception e) {
				CamelEditorUIActivator.pluginLog().logError("Failed to load model: " + e, e);
			}
		} catch (Exception ex) {
			CamelEditorUIActivator.pluginLog().logError(ex);
		} finally {
			if (designEditor.getDiagramTypeProvider() != null){
				designEditor.getDiagramTypeProvider().resourceReloaded(diagram);
			}
			designEditor.getParent().startDirtyListener();
			if(designEditor.getModel() != null) {
				designEditor.getModel().registerDOMListener();
			}
			if (context != null){
				new CamelModelElementIDUtil().ensureUniqueID(context);
			}
		}
	}
	
	/**
	 * returns the feature provider 
	 * 
	 * @return
	 */
	public IFeatureProvider getFeatureProvider() {
		return featureProvider;
	}

	/**
	 * @return the createdResource
	 */
	public Resource getCreatedResource() {
		return createdResource;
	}

	/**
	 * returns the diagram
	 * 
	 * @return
	 */
	public Diagram getDiagram() {
		return diagram;
	}
}

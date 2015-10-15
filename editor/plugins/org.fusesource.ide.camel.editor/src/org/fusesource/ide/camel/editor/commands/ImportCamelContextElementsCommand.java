/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.commands;

import java.util.ArrayList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.features.custom.LayoutDiagramFeature;
import org.fusesource.ide.camel.editor.internal.CamelDiagramLoader;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.CamelContextElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;

/**
 * @author lhein
 */
public class ImportCamelContextElementsCommand extends RecordingCommand {

	private TransactionalEditingDomain editingDomain;
	private CamelDesignEditor designEditor;
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
		super(editingDomain);
		this.designEditor = designEditor;
		this.editingDomain = editingDomain;
		this.camelContextFile = camelContextFile;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 */
	@Override
	protected void doExecute() {
		// Create the diagram and its file
		String diagramName = "CamelContext";
		diagram = Graphiti.getPeCreateService().createDiagram("CamelContext", diagramName, true); //$NON-NLS-1$
		URI uri = URI.createPlatformResourceURI(camelContextFile.getResource().getLocation().toString(), true);
		createdResource = editingDomain.getResourceSet().createResource(uri);
		createdResource.getContents().add(diagram);
		IDiagramTypeProvider dtp = GraphitiUi.getExtensionManager().createDiagramTypeProvider(diagram, "org.fusesource.ide.camel.editor.dtp.id"); //$NON-NLS-1$
		IFeatureProvider featureProvider = dtp.getFeatureProvider();
		CamelDiagramLoader diagramReader = new CamelDiagramLoader(diagram, featureProvider);
		try {
			CamelContextElement context = (CamelContextElement)camelContextFile.getChildElements().get(0);
			diagramReader.loadModel(context);
		} catch (Exception e) {
			e.printStackTrace();
			CamelEditorUIActivator.pluginLog().logError("Failed to load model: " + e, e);
		}
		
        ArrayList<PictogramElement> containers = new ArrayList<PictogramElement>();
        containers.add(diagram);
        NodeUtils.getAllContainers(featureProvider, designEditor.getModel().getChildElements().get(0), containers);
        for (int i=0; i<containers.size(); i++) {
	        for (PictogramElement pe : containers) {
	        	CustomContext cc = new CustomContext(new PictogramElement[] {pe});
	        	ICustomFeature[] cfs = featureProvider.getCustomFeatures(null);
	        	for (ICustomFeature cf : cfs) {
	        		if (cf instanceof LayoutDiagramFeature) {
	        			cf.execute(cc);		
	        		}
	        	}        	
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

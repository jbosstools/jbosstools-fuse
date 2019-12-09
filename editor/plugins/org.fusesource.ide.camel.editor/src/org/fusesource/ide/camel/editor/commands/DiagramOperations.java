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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;

/**
 * @author lhein
 */
public class DiagramOperations {

	public static LayoutCommand layoutDiagram(CamelDesignEditor designEditor) {
		return layoutDiagram(designEditor, false);
	}
	
	public static LayoutCommand layoutDiagram(CamelDesignEditor designEditor, boolean modelChanged) {
		if (designEditor == null){
			return null;
		}
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		LayoutCommand operation = new LayoutCommand(
				designEditor.getFeatureProvider(),
				designEditor.getDiagramTypeProvider().getDiagram(),
				designEditor.getModel().getRouteContainer(),
				editingDomain);
		execute(editingDomain, operation, modelChanged);
		return operation;
	}
	
	public static LayoutCommand layoutDiagram(CamelDesignEditor designEditor, boolean modelChanged, AbstractCamelModelElement container) {
		if (designEditor == null){
			return null;
		}
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		LayoutCommand operation = new LayoutCommand(designEditor.getFeatureProvider(), designEditor.getDiagramTypeProvider().getDiagram(), container, editingDomain);
		execute(editingDomain, operation, modelChanged);
		return operation;
	}

	public static LayoutCommand layoutDiagram(TransactionalEditingDomain editingDomain, IFeatureProvider featureProvider, Diagram diagram, AbstractCamelModelElement container) {
		LayoutCommand operation = new LayoutCommand(featureProvider, diagram, container, editingDomain);
		execute(editingDomain, operation, false);
		return operation;
	}

	public static UpdateCommand updateSelectedNode(CamelDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		UpdateCommand operation = new UpdateCommand(designEditor, editingDomain, designEditor.getSelectedNode());
		execute(editingDomain, operation, true);

		return operation;
	}

	public static UpdateCommand updateDiagram(CamelDesignEditor designEditor) {
		CamelFile designEditorModel = designEditor.getModel();
		if(designEditorModel != null){
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		UpdateCommand operation = new UpdateCommand(designEditor, editingDomain, designEditorModel.getRouteContainer());
		execute(editingDomain, operation, true);

		return operation;
		} else {
			return null;
		}
	}

	public static DeleteNodeCommand deleteNode(CamelDesignEditor designEditor, AbstractCamelModelElement selectedNode) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		DeleteNodeCommand operation = new DeleteNodeCommand(designEditor, editingDomain, selectedNode);
		execute(editingDomain, operation, true);
		return operation;
	}

	public static void execute(TransactionalEditingDomain editingDomain, RecordingCommand operation, boolean modelChanged) {
		editingDomain.getCommandStack().execute(operation);
		if (!modelChanged) {
			// if the model isn't changed we just reset the dirty marker which
			// is done by flushing the command stack
			editingDomain.getCommandStack().flush();
		}
	}

	protected static synchronized TransactionalEditingDomain createEditingDomain(CamelDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = designEditor.getEditingDomain();
		IDiagramTypeProvider diagramTypeProvider = designEditor.getDiagramTypeProvider();
		if(diagramTypeProvider == null){
			return null;
		}
		Diagram diagram = diagramTypeProvider.getDiagram();
		if (diagram == null) {
			return null;
		}
		if (editingDomain == null) {
			IDiagramBehavior diagramBehavior = diagramTypeProvider.getDiagramBehavior();
			if (diagramBehavior != null) {
				editingDomain = diagramBehavior.getEditingDomain();
			}
		}
		if (editingDomain == null) {
			ResourceSet resourceSet = null;
			Resource eResource = diagram.eResource();
			if (eResource != null) {
				resourceSet = eResource.getResourceSet();
			}
			if (resourceSet == null) {
				resourceSet = new ResourceSetImpl();
			}

			editingDomain = TransactionUtil.getEditingDomain(resourceSet);
			if (editingDomain == null) {
				editingDomain = GraphitiUi.getEmfService().createResourceSetAndEditingDomain();
			}
		}

		// TODO: do we need to set transactionaldomain?
//		if (designEditor.getEditingDomain() == null) {
//			designEditor.setEditingDomain(editingDomain);
//		}
		
		return editingDomain;
	}

	public static ChangeGridColorCommand updateGridColor(CamelDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		ChangeGridColorCommand operation = new ChangeGridColorCommand(designEditor, editingDomain);
		execute(editingDomain, operation, false);

		return operation;
	}
	
	public static void highlightNode(final CamelDesignEditor designEditor, final AbstractCamelModelElement node,  final boolean highlight) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
				if (editingDomain != null) {
					HighlightNodeCommand operation = new HighlightNodeCommand(designEditor, editingDomain, node, highlight);
					execute(editingDomain, operation, false);
				}
			}
		});
	}
}

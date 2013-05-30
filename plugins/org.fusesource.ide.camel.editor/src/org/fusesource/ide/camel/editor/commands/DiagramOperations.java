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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteSupport;


public class DiagramOperations {

	public static LayoutCommand layoutDiagram(RiderDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		LayoutCommand operation = new LayoutCommand(designEditor, editingDomain);
		execute(editingDomain, operation, false);
		return operation;
	}

	public static UpdateCommand updateSelectedNode(RiderDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		UpdateCommand operation = new UpdateCommand(designEditor, editingDomain, designEditor.getSelectedNode());
		execute(editingDomain, operation, true);

		return operation;
	}

	public static UpdateCommand updateDiagram(RiderDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		UpdateCommand operation = new UpdateCommand(designEditor, editingDomain, designEditor.getSelectedRoute());
		execute(editingDomain, operation, true);

		return operation;
	}

	public static AddNodeCommand addNode(RiderDesignEditor designEditor, Class<? extends AbstractNode> aClass, AbstractNode selectedNode) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		AddNodeCommand operation = new AddNodeCommand(designEditor, editingDomain, aClass, selectedNode);
		execute(editingDomain, operation, true);
		return operation;
	}

	public static AddNewRouteCommand addNewRoute(RiderDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		AddNewRouteCommand operation = new AddNewRouteCommand(designEditor, editingDomain);
		execute(editingDomain, operation, true);
		return operation;
	}

	public static DeleteRouteCommand deleteRoute(RiderDesignEditor designEditor, RouteSupport selectedRoute) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		DeleteRouteCommand operation = new DeleteRouteCommand(designEditor, editingDomain, selectedRoute);
		execute(editingDomain, operation, true);
		return operation;
	}
	
	public static DeleteNodeCommand deleteNode(RiderDesignEditor designEditor, AbstractNode selectedNode) {
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

	protected static synchronized TransactionalEditingDomain createEditingDomain(RiderDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = designEditor.getEditingDomain();
		Diagram diagram = designEditor.getDiagram();
		if (editingDomain == null) {
			IDiagramBehavior diagramBehavior = designEditor.getDiagramTypeProvider().getDiagramBehavior();
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
		
		if (designEditor.getEditingDomain() == null) {
			designEditor.setEditingDomain(editingDomain);
		}
		
		return editingDomain;
	}

	public static ChangeGridColorCommand updateGridColor(RiderDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		ChangeGridColorCommand operation = new ChangeGridColorCommand(designEditor, editingDomain);
		execute(editingDomain, operation, false);

		return operation;
	}
}

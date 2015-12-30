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

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.platform.IDiagramBehavior;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;

/**
 * @author lhein
 */
public class DiagramOperations {

	public static LayoutCommand layoutDiagram(CamelDesignEditor designEditor) {
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		LayoutCommand operation = new LayoutCommand(designEditor, editingDomain);
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
		TransactionalEditingDomain editingDomain = createEditingDomain(designEditor);
		UpdateCommand operation = new UpdateCommand(designEditor, editingDomain, designEditor.getSelectedRoute());
		execute(editingDomain, operation, true);

		return operation;
	}

	public static DeleteNodeCommand deleteNode(CamelDesignEditor designEditor, CamelModelElement selectedNode) {
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
		Diagram diagram = designEditor.getDiagramTypeProvider().getDiagram();
		if (diagram == null) {
			return null;
		}
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
	
	public static void highlightNode(final CamelDesignEditor designEditor, final CamelModelElement node,  final boolean highlight) {
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

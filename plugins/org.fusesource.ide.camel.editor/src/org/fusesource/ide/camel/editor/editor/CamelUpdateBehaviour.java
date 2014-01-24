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

package org.fusesource.ide.camel.editor.editor;

import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryEvent;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;

/**
 * @author lhein
 */
public class CamelUpdateBehaviour extends DefaultUpdateBehavior {

	@SuppressWarnings("unused")
	private TransactionalEditingDomain editingDomain;
	private RiderDesignEditor riderDesignEditor = null;
	
	/**
	 * @param riderDesignEditor
	 */
	public CamelUpdateBehaviour(RiderDesignEditor riderDesignEditor) {
		super(riderDesignEditor.getDiagramBehavior());
		this.riderDesignEditor = riderDesignEditor;
	}
	
	/**
	 * Is called by the operation history of the
	 * {@link TransactionalEditingDomain} in case the history changes. Reacts on
	 * undo and redo events and updates the dirty state of the editor.
	 * 
	 * @param event
	 *            the {@link OperationHistoryEvent} to react upon
	 */
	@Override
	public void historyNotification(OperationHistoryEvent event) {
		super.historyNotification(event);
		
		if (event.getEventType() == OperationHistoryEvent.UNDONE) {	
			System.out.println("Undo GF complete!");
			RiderDesignEditor rde = Activator.getDiagramEditor();
			//RouteSupport selectedRoute = Activator.getDiagramEditor().getSelectedRoute();
			
			RouteSupport selectedRoute = riderDesignEditor.getSelectedRoute();
			CommandStack cs = riderDesignEditor.getCommandStack();
			IUndoableOperation operation = event.getOperation();
			String lbl = operation.getLabel();
			
			//riderDesignEditor.getEditor().resourceChanged(event);
			if ((riderDesignEditor != null) && (lbl.contentEquals("Delete"))) {
				System.out.println("historyNotification - event undone!!! - operation " + lbl);
				Diagram diagram = Activator.getDiagramEditor().getDiagram();
				EList<EObject> objs = diagram.eContents();
				EList<Adapter> adapters = diagram.eAdapters();
				TreeIterator<EObject> tree = diagram.eAllContents();
				//cs.undo();
				IUndoContext[] undoContexts = operation.getContexts();
				RouteContainer model = riderDesignEditor.getModel();
				//List<AbstractNode> modelChildren = model.getSourceNodes();
				List<AbstractNode> modelChildren = selectedRoute.getRootNodes();
		        for (AbstractNode child : modelChildren) {
		        	System.out.println("Model child " + child.getName());
		        }				
				//selectedRoute.recreateModel();
				//DiagramOperations.updateSelectedNode(riderDesignEditor);
				//riderDesignEditor.getDiagramTypeProvider().getDiagramBehavior().refresh();

				riderDesignEditor.refreshDiagramContents();
				//riderDesignEditor.update();
				riderDesignEditor.updateDirtyState();
				//riderDesignEditor.getEditor().updatedDesignPage();  // pleacu  - gets text from the document
			}

		}	
		
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#createEditingDomain()
//	 */
//	@Override
//	protected void createEditingDomain() {
//		this.editingDomain = GraphitiUi.getEmfService().createResourceSetAndEditingDomain(); 
//		initializeEditingDomain(this.editingDomain);
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#getEditingDomain()
//	 */
//	@Override
//	public synchronized TransactionalEditingDomain getEditingDomain() {
//		if (this.editingDomain == null) {
//			createEditingDomain();
//		}
//		return this.editingDomain;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior#disposeEditingDomain()
//	 */
//	@Override
//	protected void disposeEditingDomain() {
//		this.editingDomain.dispose();
//	}
}

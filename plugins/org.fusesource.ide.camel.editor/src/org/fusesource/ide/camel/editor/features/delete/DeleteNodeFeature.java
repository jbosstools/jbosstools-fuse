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

package org.fusesource.ide.camel.editor.features.delete;

import java.util.List;
import java.util.Stack;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.graphiti.features.ICustomUndoableFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.Flow;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;


/**
 * @author lhein
 */
public class DeleteNodeFeature extends DefaultDeleteFeature implements ICustomUndoableFeature {
	private Stack<AbstractNode> deletedNodes = new Stack<AbstractNode>();
	private Class<?> clazz = Endpoint.class;

	/**
	 * 
	 * @param fp
	 */
	public DeleteNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#preDelete(org.eclipse.graphiti.features.context.IDeleteContext)
	 */
	@Override
	public void preDelete(IDeleteContext context) {
		super.preDelete(context);

		// now delete the BO from our model
		PictogramElement pe = context.getPictogramElement();
		Object[] businessObjectsForPictogramElement = getAllBusinessObjectsForPictogramElement(pe);
		if (businessObjectsForPictogramElement != null &&
				businessObjectsForPictogramElement.length > 0) {
			Object bo = businessObjectsForPictogramElement[0];
			if (bo instanceof Flow) {
				deleteFlowFromModel((Flow) bo);
			} else if (bo instanceof AbstractNode) {
				deleteBOFromModel((AbstractNode)bo);
			} else if (bo instanceof EReferenceImpl) {
				EReferenceImpl eimpl = (EReferenceImpl) bo;
				EClassifier eType = eimpl.getEType();
				if (eType instanceof AbstractNode) {
					AbstractNode target = (AbstractNode) eType;
					Activator.getLogger().debug("==== trying to zap the target: " + target);
					EObject eContainer = eimpl.eContainer();
					if (eContainer instanceof AbstractNode) {
						AbstractNode source = (AbstractNode) eContainer;
						Activator.getLogger().debug("==== trying to source: " + source + " -> target: " + target);
						source.removeConnection(target);
					}
				}
			} else {
				Activator.getLogger().warning("Cannot figure out Node or Flow from BO: " + bo);
			}
		}
	}

	private void deleteBOFromModel(AbstractNode nodeToRemove) {
		// we can't remove null objects or the root of the routes
		if (nodeToRemove == null || nodeToRemove instanceof RouteContainer) return;
		
		RouteContainer p = nodeToRemove.getParent();
		if (p == null) 
			return;
		
		deletedNodes.add(nodeToRemove);
		
		List<AbstractNode> children = p.getChildren();		
		nodeToRemove.setInsertionIndex(children.indexOf(nodeToRemove));
		
		// lets remove all connections
		nodeToRemove.detach();
	}

	private void deleteFlowFromModel(Flow bo) {
		bo.disconnect();
	}
	
	@Override
	public void undo(IContext context) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> undo");
		if ((!(context instanceof IDeleteContext)) || (deletedNodes.empty()))
			return;
		
		IDeleteContext delContext = (IDeleteContext)context;
		
		PictogramElement pe = delContext.getPictogramElement();
		Object[] businessObjectsForPictogramElement = getAllBusinessObjectsForPictogramElement(pe);
		EList<EObject> contents = pe.eContents();
		EObject container = pe.eContainer();
		RiderDesignEditor rde = Activator.getDiagramEditor();

		//RouteContainer model = rde.getModel();
		//CommandStack commandStack = rde.getCommandStack();

		
		AbstractNode deletedNode = deletedNodes.pop();
		AbstractNode node = new Endpoint((Endpoint)deletedNode);
		//try {
		//	node = (AbstractNode)this.clazz.newInstance();
		//} catch (Exception ex) {
		//	throw new RuntimeException(ex);
		//}
		//node = deletedNode;
		

		////DiagramOperations.addNode(rde, Endpoint.class, node);
		RouteSupport selectedRoute = Activator.getDiagramEditor().getSelectedRoute();
		//Diagram diagram = getDiagram();
		int insertionIndex = deletedNode.getInsertionIndex();

		if (selectedRoute != null) {
			// This call doesn't add to the undo command stack.
			selectedRoute.addChildSilent(node, insertionIndex);
		} else {
			Activator.getLogger().warning("Warning - Could not find the currently selected node, unable to associate node " +
				node.getName() + " with the route");
		}
		//node.setInsertionIndex(0);
		List<AbstractNode> children = selectedRoute.getChildren();
		AbstractNode deletedNodeParent = children.get(insertionIndex - 1);
		deletedNodeParent.addTargetNode(node);
		//DiagramOperations.layoutDiagram(rde);
		rde.getDiagramBehavior().refresh();
		//new CreateEndpointFigureFeature(getFeatureProvider(), "", node.getDescription(), (Endpoint) node);
		//IEditorInput iei = rde.getEditorInput();
		//rde.asFileEditorInput(iei);
		//rde.setInputAndInit(iei);
		//pe.eContents().add((EObject) node);
		//pe.setVisible(true);
		//pe.setActive(true);

		//getFeatureProvider().getDirectEditingInfo().setActive(true);
		//getFeatureProvider().link(pe, node);
		//createPEandLink(delContext, node, insertionIndex);
		
		//PictogramElement pe = addGraphicalRepresentation(context, node);

		//getFeatureProvider().link(pe, node);
		
		// activate direct editing after object creation
		//getFeatureProvider().getDirectEditingInfo().setActive(true);
		// activate direct editing after object creation

		//rde.setSelectedNode(node);
		
		//DiagramOperations.updateSelectedNode(rde);
		//selectedRoute.recreateModel();
		rde.updateDirtyState();

		//rde.refreshDiagramContents();
		//rde.update();
		/*
		rde.fireModelChanged();
		rde.getDiagramTypeProvider().getDiagramBehavior().refresh();
		*/
		//rde.getEditor().updatedDesignPage();
		//Object businessObjectForPictogramElement = getBusinessObjectForPictogramElement(pe);

		//	if (businessObjectForPictogramElement instanceof AbstractNode) {
		//		System.out.println("yea");
		//	}

	}

	@Override
	public boolean canUndo(IContext context) {
		return true;
	}
	
	@Override
	public boolean canRedo(IContext context) {
		// TODO Auto-generated method stub
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> canRedo");
		return false;
	}


	@Override
	public void redo(IContext context) {
		// TODO Auto-generated method stub
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> redo");
		
	}

}

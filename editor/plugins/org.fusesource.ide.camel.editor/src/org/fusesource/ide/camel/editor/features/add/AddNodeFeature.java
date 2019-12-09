/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.internal.editor.DiagramEditorDummy;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.FigureUIFactory;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class AddNodeFeature extends AbstractAddShapeFeature {

	public static final String DEACTIVATE_LAYOUT = "deactivateLayout";

	/**
	 * 
	 * @param fp
	 */
	public AddNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IAdd#canAdd(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public boolean canAdd(IAddContext context) {
		// check if user wants to add a EClass
		final Object newObject = context.getNewObject();
		if (newObject instanceof AbstractCamelModelElement) {
			// check if user wants to add to a diagram
			if (context.getTargetContainer() instanceof Diagram) {
                final String nodeTypeId = ((AbstractCamelModelElement) newObject).getNodeTypeId();
				return  AbstractCamelModelElement.ROUTE_NODE_NAME.equalsIgnoreCase(nodeTypeId) ||
                		"rest".equalsIgnoreCase(nodeTypeId) ||
                		"restConfiguration".equalsIgnoreCase(nodeTypeId);
            } else if (getBusinessObjectForPictogramElement(context.getTargetContainer()) instanceof AbstractCamelModelElement) {
            	AbstractCamelModelElement container =  (AbstractCamelModelElement)getBusinessObjectForPictogramElement(context.getTargetContainer());
            	AbstractCamelModelElement child = (AbstractCamelModelElement)newObject;
            	if (CollapseFeature.isCollapsed(getFeatureProvider(), container)) {
            		// we don't allow drop on a collapsed figure
            		return false;
            	}
            	if (NodeUtils.isValidChild(container, child)) {
            		return true;
            	} else {
            		// seems user wants to drop a figure on a non-container to connect new node to other node
            		AbstractCamelModelElement sourceNode =  (AbstractCamelModelElement)getBusinessObjectForPictogramElement(context.getTargetContainer());
                	AbstractCamelModelElement newNode = (AbstractCamelModelElement)newObject;
                	if (sourceNode.getOutputElement() != null) {
                		// insert between 2 nodes
                		AbstractCamelModelElement targetNode =  sourceNode.getOutputElement();
                		return 	sourceNode.hasSameParent(newNode) &&
                				newNode.hasSameParent(targetNode);
                	} else {
                		// append new node and connect to source node
                		return sourceNode.hasSameParent(newNode);
                	}
            	}
            }
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public PictogramElement add(IAddContext context) {
		AbstractCamelModelElement addedClass = (AbstractCamelModelElement)context.getNewObject();
		ContainerShape targetContainer = context.getTargetContainer();
		Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(targetContainer);
		String label = addedClass.getDisplayText();
		
		// CONTAINER SHAPE
		ContainerShape containerShape = Graphiti.getPeCreateService().createContainerShape(targetContainer, true);

		// now paint the shape
		FigureUIFactory.createFigureUI(context, getFeatureProvider(), containerShape, addedClass, diagram, label);
		
		// create link and wire it (the added class and the containershape)
        link(containerShape, addedClass);
		
		// call the layout feature
		layoutPictogramElement(containerShape);
		
		final Object deactivateLayout = context.getProperty(DEACTIVATE_LAYOUT);
		if (!Boolean.TRUE.equals(deactivateLayout)) {
			Object o_editor = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer();
			CamelDesignEditor editor;
			if (o_editor == null || o_editor instanceof DiagramEditorDummy) {
				editor = CamelUtils.getDiagramEditor();
			} else {
				editor = (CamelDesignEditor) o_editor;
			}
			DiagramOperations.layoutDiagram(editor);
		}

		return containerShape;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
	 */
	@Override
	public boolean hasDoneChanges() {
		return true;
	}
}

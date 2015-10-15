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
package org.fusesource.ide.camel.editor.features.add;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.editor.utils.FigureUIFactory;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;

/**
 * @author lhein
 */
public class AddNodeFeature extends AbstractAddShapeFeature {

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
		if (newObject instanceof CamelModelElement) {
			// check if user wants to add to a diagram
			if (context.getTargetContainer() instanceof Diagram) {
                return ((CamelModelElement) newObject).getNodeTypeId().equalsIgnoreCase("route");
            } else if (getBusinessObjectForPictogramElement(context.getTargetContainer()) instanceof CamelModelElement) {
            	CamelModelElement container =  (CamelModelElement)getBusinessObjectForPictogramElement(context.getTargetContainer());
            	CamelModelElement child = (CamelModelElement)newObject;
            	return NodeUtils.isValidChild(container, child);
            }
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public PictogramElement add(IAddContext context) {
		CamelModelElement addedClass = (CamelModelElement)context.getNewObject();
		ContainerShape targetContainer = (ContainerShape) context.getTargetContainer();
		Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(targetContainer);
		String label = DiagramUtils.filterFigureLabel(addedClass.getDisplayText());
		
		// CONTAINER SHAPE WITH ROUNDED RECTANGLE
		ContainerShape containerShape = Graphiti.getPeCreateService().createContainerShape(targetContainer, true);

		// now paint the shape
		FigureUIFactory.createFigureUI(context, getFeatureProvider(), containerShape, addedClass, diagram, label);
		
		// create link and wire it (the added class and the containershape)
        link(containerShape, addedClass);
		
		// call the layout feature
		layoutPictogramElement(containerShape);

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

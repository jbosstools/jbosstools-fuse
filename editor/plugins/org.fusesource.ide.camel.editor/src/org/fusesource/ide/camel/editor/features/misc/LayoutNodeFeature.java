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

package org.fusesource.ide.camel.editor.features.misc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.swt.graphics.Rectangle;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

public class LayoutNodeFeature extends AbstractLayoutFeature {

	public LayoutNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ILayout#canLayout(org.eclipse.graphiti.features.context.ILayoutContext)
	 */
	@Override
	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an EClass
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape))
			return false;
		
		Object[] bos = getAllBusinessObjectsForPictogramElement(pe);
		
		return bos != null && bos.length == 1
	              && bos[0] instanceof AbstractCamelModelElement;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.ILayout#layout(org.eclipse.graphiti.features.context.ILayoutContext)
	 */
	@Override
	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = false;
		IGaService gaService = Graphiti.getGaService();
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
		Object bo = getBusinessObjectForPictogramElement(containerShape);
		Rectangle maxDim = new Rectangle(containerGa.getX(), containerGa.getY(), containerGa.getWidth(), containerGa.getHeight());
		
		List<PictogramElement> containers = new ArrayList<>();		
		NodeUtils.getAllContainers(getFeatureProvider(), (AbstractCamelModelElement)bo, containers);
		containers.add(containerShape);
		for (PictogramElement pe : containers) {
			Object peBO = getBusinessObjectForPictogramElement(pe);
			if (peBO instanceof AbstractCamelModelElement) {
				IDimension dPe = gaService.calculateSize(pe.getGraphicsAlgorithm(), true);
				if (dPe.getWidth() + pe.getGraphicsAlgorithm().getX() > maxDim.width){
					maxDim.width = dPe.getWidth() + pe.getGraphicsAlgorithm().getX();
				}
				if (dPe.getHeight() + pe.getGraphicsAlgorithm().getY() > maxDim.height){
					maxDim.height = dPe.getHeight() + pe.getGraphicsAlgorithm().getY();
				}
			}
		}
		
		// the containerGa is the invisible rectangle
		// containing the visible rectangle as its (first and only) child
		GraphicsAlgorithm rectangle = containerGa.getGraphicsAlgorithmChildren().get(0);

		// height of invisible rectangle
		if (containerGa.getHeight() < maxDim.height) {
			containerGa.setHeight(maxDim.height);
		}

		// height of visible rectangle (same as invisible rectangle)
		if (rectangle.getHeight() != containerGa.getHeight()) {
			rectangle.setHeight(containerGa.getHeight());
			anythingChanged = true;
		}

		// width of invisible rectangle
		if (containerGa.getWidth() < maxDim.width) {
			containerGa.setWidth(maxDim.width);
			anythingChanged = true;
		}

		// width of visible rectangle (smaller than invisible rectangle)
		int rectangleWidth = containerGa.getWidth();
		if (rectangle.getWidth() != rectangleWidth) {
			rectangle.setWidth(rectangleWidth);
			anythingChanged = true;
		}

		return anythingChanged;
	}
}


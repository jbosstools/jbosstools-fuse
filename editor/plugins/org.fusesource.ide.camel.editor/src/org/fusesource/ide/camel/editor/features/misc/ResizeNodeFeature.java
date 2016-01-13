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

package org.fusesource.ide.camel.editor.features.misc;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.editor.utils.FigureUIFactory;

/**
 * @author lhein
 */
public class ResizeNodeFeature extends DefaultResizeShapeFeature {

	/**
	 * 
	 * @param fp
	 */
	public ResizeNodeFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature#canResizeShape(org.eclipse.graphiti.features.context.IResizeShapeContext)
	 */
	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature#resizeShape(org.eclipse.graphiti.features.context.IResizeShapeContext)
	 */
	@Override
	public void resizeShape(IResizeShapeContext context) {
		Shape shape = context.getShape();
		int x = context.getX();
		int y = context.getY();
		int w = context.getWidth(); // -1
		int h = context.getHeight(); // -1
				
		if (w == 0) {
			w = Graphiti.getPeService().getPropertyValue(shape, CollapseFeature.PROP_COLLAPSED_WIDTH) != null ?
					Integer.parseInt(Graphiti.getPeService().getPropertyValue(shape, CollapseFeature.PROP_COLLAPSED_WIDTH)) :
						shape.getGraphicsAlgorithm().getWidth();
		}
		if (h == 0) {
			h = Graphiti.getPeService().getPropertyValue(shape, CollapseFeature.PROP_COLLAPSED_HEIGHT) != null ?
					Integer.parseInt(Graphiti.getPeService().getPropertyValue(shape, CollapseFeature.PROP_COLLAPSED_HEIGHT)) :
						shape.getGraphicsAlgorithm().getHeight();
		}
		
// we don't save user made resizing of collapsed shapes for now
//		boolean collapsed = Graphiti.getPeService().getPropertyValue(shape, CollapseFeature.PROP_COLLAPSED_STATE) != null && 
//							Graphiti.getPeService().getPropertyValue(shape, CollapseFeature.PROP_COLLAPSED_STATE).equalsIgnoreCase("true");
//		if (collapsed) {
//			FigureUIFactory.storeCollapsedSize((ContainerShape)shape);
//		}
		
		for (GraphicsAlgorithm ga : shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
			if (Graphiti.getPeService().getPropertyValue(ga, DiagramUtils.PROP_SHAPE_KIND) != null && Graphiti.getPeService().getPropertyValue(ga, DiagramUtils.PROP_SHAPE_KIND).equalsIgnoreCase(DiagramUtils.PROP_SHAPE_KIND_TITLE)) {
				if (Graphiti.getPeService().getPropertyValue(ga, DiagramUtils.PROP_IMG_WIDTH) != null) {
					int imgWidth = Integer.parseInt(Graphiti.getPeService().getPropertyValue(ga, DiagramUtils.PROP_IMG_WIDTH));
					ga.setWidth(w - imgWidth - FigureUIFactory.LABEL_SPACER_X - FigureUIFactory.LABEL_SPACER_X - FigureUIFactory.SECTION_OFFSET_X);
				}						
			} else if (Graphiti.getPeService().getPropertyValue(ga, DiagramUtils.PROP_SHAPE_KIND) != null && Graphiti.getPeService().getPropertyValue(ga, DiagramUtils.PROP_SHAPE_KIND).equalsIgnoreCase(DiagramUtils.PROP_SHAPE_KIND_EXPANDABLE)) {
				ga.setWidth(w);
				if (Graphiti.getPeService().getPropertyValue(ga, DiagramUtils.PROP_ORIGINAL_SECTION_HEIGHT) != null) {
					int origHeight = Integer.parseInt(Graphiti.getPeService().getPropertyValue(ga, DiagramUtils.PROP_ORIGINAL_SECTION_HEIGHT));
					ga.setHeight(h - origHeight);
				}
			} else {
				ga.setWidth(w);
			}
		}
		if (shape.getGraphicsAlgorithm() != null) {
			Graphiti.getGaService().setLocationAndSize(shape.getGraphicsAlgorithm(), x, y, w-1+FigureUIFactory.CONTAINER_BORDER_SIZE+FigureUIFactory.CONTAINER_BORDER_SIZE, h+FigureUIFactory.CONTAINER_BORDER_SIZE+FigureUIFactory.CONTAINER_BORDER_SIZE);
		}
		
		layoutPictogramElement(shape);
	}
}

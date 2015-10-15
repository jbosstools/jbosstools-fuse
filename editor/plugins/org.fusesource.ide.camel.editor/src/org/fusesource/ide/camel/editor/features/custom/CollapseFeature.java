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
package org.fusesource.ide.camel.editor.features.custom;

import java.util.Iterator;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;

/**
 * @author lhein
 */
public class CollapseFeature extends AbstractCustomFeature {

	public static String PROP_COLLAPSED_STATE 	= "isCollapsed";
	public static String PROP_EXPANDED_WIDTH  	= "expandedWidth";
	public static String PROP_EXPANDED_HEIGHT 	= "expandedHeight";
	public static String PROP_COLLAPSED_WIDTH 	= "collapsedWidth";
	public static String PROP_COLLAPSED_HEIGHT 	= "collapsedHeight";
	
	/**
	 * 
	 * @param fp
	 */
	public CollapseFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		boolean ret = false;
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
			// Add more of the objects that collapse here
			if (bo instanceof CamelModelElement) {
				return ((CamelModelElement)bo).getChildElements().size() > 0;
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
	 	   	if(bo instanceof CamelModelElement) {
	 	   		collapseShape(pes[0]);
	 	   	}
		}
		getDiagramBehavior().getDiagramContainer().selectPictogramElements(pes);
	}
	
	/**
	 * Collapse the shape for the BusinessObject
	 *
	 * @param pe PictogamElement for the shape of the object
	 */
	public void collapseShape(PictogramElement pe) {
		ContainerShape cs = (ContainerShape) pe;
		int width = pe.getGraphicsAlgorithm().getWidth();
		int height = pe.getGraphicsAlgorithm().getHeight(); 

		int changeWidth = 0;
		int changeHeight = 0;

		boolean visible = false;
		if (Graphiti.getPeService().getPropertyValue(pe, PROP_COLLAPSED_STATE) == null || 
			Graphiti.getPeService().getPropertyValue(pe, PROP_COLLAPSED_STATE).equals("false")) {
			Graphiti.getPeService().setPropertyValue(pe, PROP_EXPANDED_WIDTH, String.valueOf(width));
			Graphiti.getPeService().setPropertyValue(pe, PROP_EXPANDED_HEIGHT, String.valueOf(height));
			visible = false;
		} else if (	Graphiti.getPeService().getPropertyValue(pe, PROP_COLLAPSED_STATE) != null && 
					Graphiti.getPeService().getPropertyValue(pe, PROP_COLLAPSED_STATE).equals("true")) {
			changeWidth = Integer.parseInt(Graphiti.getPeService().getPropertyValue(pe, PROP_EXPANDED_WIDTH));
			changeHeight = Integer.parseInt(Graphiti.getPeService().getPropertyValue(pe, PROP_EXPANDED_HEIGHT));
			Graphiti.getPeService().setPropertyValue(pe, PROP_COLLAPSED_STATE, "false");
			visible = true;
		}
		
		ResizeShapeContext context1 = new ResizeShapeContext(cs);
		context1.setSize(changeWidth, changeHeight);
		context1.setLocation(cs.getGraphicsAlgorithm().getX(), cs.getGraphicsAlgorithm().getY());
		IResizeShapeFeature rsf = getFeatureProvider().getResizeShapeFeature(context1);
		if (rsf.canExecute(context1)) {
			rsf.execute(context1);
		}
	 	 
		if(!visible) {
			Graphiti.getPeService().setPropertyValue(pe, PROP_COLLAPSED_STATE, "true");
		}
		
		//visible/invisible all the children
		makeChildrenInvisible(cs, visible);
	}

	/**
	 * Recursive function that makes all the children inside a shape visible/invisible
	 *
	 * @param cs ContainerShape
	 * @param visible true/false
	 */
	public void makeChildrenInvisible(ContainerShape cs, boolean visible) { 
		if(cs.getChildren().isEmpty()) {
			return;
		} else {
			Iterator<Shape> iter = cs.getChildren().iterator();
			while (iter.hasNext()) {
				Shape shape = iter.next();
				if(shape instanceof ContainerShape) {
					// we only want to hide 1 level nested elements
//					makeChildrenInvisible((ContainerShape) shape, visible);
					shape.setVisible(visible);
					Anchor anchr = shape.getAnchors().get(0);
					boolean initVisible = false;
					
					//Check whether the initial shape is visible or not
					for (Shape shape1 : ((ContainerShape) shape).getChildren()) {
						if(shape1.getGraphicsAlgorithm() instanceof Ellipse) {
							initVisible = shape1.isVisible();
						}
					}

					for(int i=0; i < anchr.getIncomingConnections().size(); i++){
						Connection conn = anchr.getIncomingConnections().get(i);
						if(initVisible) { //Change visibility only to visible connections
							conn.setVisible(visible);
							for(int j=0; j< conn.getConnectionDecorators().size(); j++){
								conn.getConnectionDecorators().get(j).setVisible(visible);
							}
						}
					}

					for(int i=0; i < anchr.getOutgoingConnections().size(); i++) {
						Connection conn = anchr.getOutgoingConnections().get(i);
						conn.setVisible(visible);
						for(int j=0; j< conn.getConnectionDecorators().size(); j++) {
							conn.getConnectionDecorators().get(j).setVisible(visible);
						}
					}
				}
			}
		}
	}
}

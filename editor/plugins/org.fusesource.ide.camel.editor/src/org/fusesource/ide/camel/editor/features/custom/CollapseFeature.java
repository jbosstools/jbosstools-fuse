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
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.ResizeShapeContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.utils.StyleUtil;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class CollapseFeature extends AbstractCustomFeature {

	public static final String PROP_COLLAPSED_STATE 	= "isCollapsed";
	public static final String PROP_EXPANDED_WIDTH  	= "expandedWidth";
	public static final String PROP_EXPANDED_HEIGHT 	= "expandedHeight";
	public static final String PROP_COLLAPSED_WIDTH 	= "collapsedWidth";
	public static final String PROP_COLLAPSED_HEIGHT 	= "collapsedHeight";
	
	private PictogramElement lastPE;
		
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
			if (bo instanceof AbstractCamelModelElement) {
				return !((AbstractCamelModelElement)bo).getChildElements().isEmpty();
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#isAvailable(org.eclipse.graphiti.features.context.IContext)
	 */
	@Override
	public boolean isAvailable(IContext context) {
		if (context instanceof ICustomContext) {
			this.lastPE = ((ICustomContext)context).getPictogramElements()[0];
		}
		return super.isAvailable(context);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object bo = getBusinessObjectForPictogramElement(pes[0]);
	 	   	if(bo instanceof AbstractCamelModelElement) {
	 	   		collapseShape(pes[0]);
 	 	   	}
		}
		((CamelDesignEditor)getDiagramBehavior().getDiagramContainer()).autoLayoutRoute();
		getDiagramBehavior().getDiagramContainer().selectPictogramElements(pes);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return isCollapsed() ? "Expand" : "Collapse";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return String.format("%s the selected node...", (isCollapsed() ? "Expands" : "Collapses"));
	}
	
	private boolean isCollapsed() {
		if (Graphiti.getPeService().getPropertyValue(lastPE, PROP_COLLAPSED_STATE) == null || 
			Graphiti.getPeService().getPropertyValue(lastPE, PROP_COLLAPSED_STATE).equals("false")) {
			return false;
		} 
		return true;
	}
	
	/**
	 * Collapse the shape for the BusinessObject
	 *
	 * @param pe PictogamElement for the shape of the object
	 */
	public void collapseShape(PictogramElement pe) {
		ContainerShape cs = (ContainerShape) pe;

		int changeWidth = 0;
		int changeHeight = 0;

		boolean childFiguresVisible = false;
		String initialCollapsedState = Graphiti.getPeService().getPropertyValue(pe, PROP_COLLAPSED_STATE);
		if (initialCollapsedState == null || initialCollapsedState.equals("false")) {
			int width = pe.getGraphicsAlgorithm().getWidth();
			int height = pe.getGraphicsAlgorithm().getHeight(); 
			Graphiti.getPeService().setPropertyValue(pe, PROP_EXPANDED_WIDTH, String.valueOf(width));
			Graphiti.getPeService().setPropertyValue(pe, PROP_EXPANDED_HEIGHT, String.valueOf(height));
		} else if (initialCollapsedState.equals("true")) {
			changeWidth = Integer.parseInt(Graphiti.getPeService().getPropertyValue(pe, PROP_EXPANDED_WIDTH));
			changeHeight = Integer.parseInt(Graphiti.getPeService().getPropertyValue(pe, PROP_EXPANDED_HEIGHT));
			Graphiti.getPeService().setPropertyValue(pe, PROP_COLLAPSED_STATE, "false");
			childFiguresVisible = true;
		}

		if(!childFiguresVisible) {
			Graphiti.getPeService().setPropertyValue(pe, PROP_COLLAPSED_STATE, "true");
		}
		
		//visible/invisible all the children
		makeChildrenInvisible(cs, childFiguresVisible);
		//set the border to reflect collapse state
		updateBorderStyle(cs, !childFiguresVisible);
		
		ResizeShapeContext context1 = new ResizeShapeContext(cs);
		context1.setSize(changeWidth, changeHeight);
		context1.setLocation(cs.getGraphicsAlgorithm().getX(), cs.getGraphicsAlgorithm().getY());
		IResizeShapeFeature rsf = getFeatureProvider().getResizeShapeFeature(context1);
		if (rsf.canExecute(context1)) {
			rsf.execute(context1);
		}
	}

	/**
	 * updates the border color to reflect the collapse state of the figure
	 * 
	 * @param cs
	 * @param collapsed
	 */
	public void updateBorderStyle(ContainerShape cs, boolean collapsed) {
		IGaService gaService = Graphiti.getGaService();
		Color col = collapsed ? gaService.manageColor(getDiagram(), StyleUtil.CONTAINER_FIGURE_COLLAPSED_BORDER_COLOR) : gaService.manageColor(getDiagram(), StyleUtil.CONTAINER_FIGURE_BORDER_COLOR); 
		GraphicsAlgorithm ga = cs.getGraphicsAlgorithm();
		ga.setLineStyle(collapsed ? LineStyle.DASH : LineStyle.SOLID);
		ga.setForeground(col);
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
					ContainerShape tmpCS = (ContainerShape)shape;
					if (Graphiti.getPeService().getPropertyValue(tmpCS, PROP_COLLAPSED_STATE) == null || 
						Graphiti.getPeService().getPropertyValue(tmpCS, PROP_COLLAPSED_STATE).equals("false")) {
						makeChildrenInvisible((ContainerShape) shape, visible); // comment out if collapse gets broken	
					}					
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
	
	/**
	 * returns whether the figure is collapsed or not
	 * 
	 * @param featureProvider	the feature provider
	 * @param container			the container model element
	 * @return		true if collapsed otherwise false
	 */
	public static boolean isCollapsed(IFeatureProvider featureProvider, AbstractCamelModelElement container) {
		PictogramElement pe = featureProvider.getPictogramElementForBusinessObject(container);
		return isCollapsed(pe);
	}
	
	/**
	 * returns whether the figure is collapsed or not
	 * 
	 * @param pe	the diagram figure
	 * @return		true if collapsed otherwise false
	 */
	public static boolean isCollapsed(PictogramElement pe) {
		if (pe != null) {
			return "true".equals(Graphiti.getPeService().getPropertyValue(pe, CollapseFeature.PROP_COLLAPSED_STATE));
		}
		return false;
	}
}

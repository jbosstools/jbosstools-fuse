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

package org.fusesource.ide.camel.editor.utils;

import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;

/**
 * @author lhein
 */
public class DiagramUtils {
	
	public static final String PROP_ORIGINAL_SECTION_HEIGHT = "originalSectionHeight";
	public static final String PROP_IMG_WIDTH = "imgWidth";
	public static final String PROP_SHAPE_KIND = "shapeKind";
	public static final String PROP_SHAPE_KIND_EXPANDABLE = "expandable";
	public static final String PROP_SHAPE_KIND_HEADER = "header";
	public static final String PROP_SHAPE_KIND_TITLE = "title";
	
	
	private static int TEXT_MAX_LENGTH = 20;
	private static String TEXT_FILL_CHARS = "...";
	
	private DiagramUtils(){
		/* Can be accessed statically only */
	}
	
	/**
	 * retrieves the anchor for a given pictogram element
	 * 
	 * @param element	the pictogram
	 * @return	the anchor or null
	 */
	public static Anchor getAnchor(PictogramElement element) {
		if (element instanceof AnchorContainer) {
			AnchorContainer container = (AnchorContainer) element;
			EList<Anchor> anchors = container.getAnchors();
			if (anchors != null && !anchors.isEmpty()) {
				return anchors.get(0);
			}
		}
		return null;
	}
	
	public static void setGridVisible(boolean visible, CamelDesignEditor editor) {
		if (editor == null) {
			editor = CamelUtils.getDiagramEditor();
		}
		if (editor != null) {
			GraphicalViewer graphicalViewer = editor.getGraphicalViewer();
			if (graphicalViewer != null
					&& graphicalViewer.getEditPartRegistry() != null) {
				ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) graphicalViewer.getEditPartRegistry().get(LayerManager.ID);
				IFigure gridFigure = rootEditPart.getLayer(LayerConstants.GRID_LAYER);
				gridFigure.setVisible(visible);
				editor.getDiagramBehavior().refreshContent();
			}
		}
	}
	
	/**
	 * this method shortens the label if needed
	 * 
	 * @param originalLabel
	 * @return
	 */
	public static String filterFigureLabel(String originalLabel) {
		String label = originalLabel;
		if (TEXT_MAX_LENGTH != -1 && label.length()>TEXT_MAX_LENGTH) {
			label = label.substring(0, TEXT_MAX_LENGTH - TEXT_FILL_CHARS.length());
			label += TEXT_FILL_CHARS;
		}
		return label;
	}
}

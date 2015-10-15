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
package org.fusesource.ide.camel.editor.utils;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.fusesource.ide.camel.editor.features.custom.CollapseFeature;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;

/**
 * this factory provides the draw functionality for diagram figures
 * 
 * @author lhein
 */
public class FigureUIFactory {
	
	// the additional size of the invisible rectangle at the right border
	// (this also equals the half width of the anchor to paint there)
	public static final int SECTION_OFFSET_X = 5;
	public static final int SECTION_OFFSET_Y = 5;
	public static final int LABEL_SPACER_X = 5;
	public static final int TEXT_LABEL_SIZE = 20;
	public static final int DEFAULT_FIGURE_CONTENT_WIDTH = 140;

	/**
	 * 
	 * @param context
	 * @param fp
	 * @param containerShape
	 * @param element
	 * @param diagram
	 * @param defaultLabel
	 */
	public static void createFigureUI(IAddContext context, IFeatureProvider fp, ContainerShape containerShape, CamelModelElement element, Diagram diagram, String defaultLabel) {
		if (false) {
			// potential special figures handling goes in here
			// FOR NOW WE ONLY USE THE DEFAULT PAINT LOGIC
		} else {
			// fall back to default figure painting
			paintDefaultFigure(context, fp, containerShape, element, diagram, defaultLabel);
		}
	}
	
	private static void paintDefaultFigure(IAddContext context, IFeatureProvider fp, ContainerShape containerShape, CamelModelElement element, Diagram diagram, String defaultLabel) {
		IPeCreateService peCreateService = Graphiti.getPeCreateService();

		// check whether the context has a size (e.g. from a create feature)
		// otherwise define a default size for the shape
		// now try to use the image dimension as figure dimension plus some height spacing
		// for the display label
		Font f = StyleUtil.getStyleForCamelText(diagram).getFont();
		IDimension fontDimension = GraphitiUi.getUiLayoutService().calculateTextSize(defaultLabel, f);
		int label_width = fontDimension.getWidth() + LABEL_SPACER_X + LABEL_SPACER_X;
		int label_height = Math.max(fontDimension.getHeight(), TEXT_LABEL_SIZE);

		Dimension imageDimension = ImageUtils.getImageSize(ImageProvider.getKeyForSmallIcon(element.getIconName()));
		int image_width = imageDimension.width;
		int image_height = imageDimension.height;

		int contentWidth = Math.max(image_width + label_width + SECTION_OFFSET_X + SECTION_OFFSET_X, DEFAULT_FIGURE_CONTENT_WIDTH);
		int upperSectionHeight = Math.max(image_height + SECTION_OFFSET_Y + SECTION_OFFSET_Y, label_height + SECTION_OFFSET_Y + SECTION_OFFSET_Y);
		int lowerSectionHeight = upperSectionHeight;
		
		// we will draw a box with an upper section containing an icon and the label
		// the lower section will be empty by default but on expand it will reveal
		// all child elements of that node

		// baseRect is the invisible ground of the figure
		org.eclipse.swt.graphics.Rectangle baseRect = new org.eclipse.swt.graphics.Rectangle(context.getX(), 
																							 context.getY(), 
																							 contentWidth, 
																							 upperSectionHeight + lowerSectionHeight);
		
		// the label and icon displayed as title bar of the figure	
		org.eclipse.swt.graphics.Rectangle upperSection = new org.eclipse.swt.graphics.Rectangle(0, 
																								 0, 
																								 contentWidth, 
																								 upperSectionHeight);
				
		// the figure children section	
		org.eclipse.swt.graphics.Rectangle lowerSection = new org.eclipse.swt.graphics.Rectangle(0, 
																								 0 + upperSectionHeight, 
																								 contentWidth, 
																								 lowerSectionHeight);
		
		IGaService gaService = Graphiti.getGaService();

		Color upperSectionColor = gaService.manageColor(diagram, 190, 190, 190);
		Color lowerSectionColor = gaService.manageColor(diagram, 253, 253, 243);
		
		// create invisible outer rectangle expanded by the width needed for the anchor
		Rectangle baseFigure = gaService.createInvisibleRectangle(containerShape);
		gaService.setLocationAndSize(baseFigure, baseRect.x, baseRect.y, baseRect.width, baseRect.height);
		baseFigure.setFilled(false);
		baseFigure.setLineVisible(true);
		
		// the upper section figure
		Rectangle upperRectangle = gaService.createPlainRectangle(baseFigure);
		upperRectangle.setParentGraphicsAlgorithm(baseFigure);
		upperRectangle.setStyle(StyleUtil.getStyleForCamelClass(diagram));
		gaService.setLocationAndSize(upperRectangle, upperSection.x, upperSection.y, upperSection.width, upperSection.height);
		upperRectangle.setBackground(upperSectionColor);
		upperRectangle.setLineVisible(true);
		upperRectangle.setFilled(true);
		markFigureHeaderArea(upperRectangle, upperSectionHeight);

		// create and set image
		Image image = gaService.createImage(upperRectangle, ImageProvider.getKeyForSmallIcon(element.getIconName()));
		gaService.setLocationAndSize(image, upperSection.x + SECTION_OFFSET_X, upperSection.y + SECTION_OFFSET_Y, image_width, image_height);

		// create and set text graphics algorithm
		Text text = gaService.createDefaultText(diagram, upperRectangle, defaultLabel);
		Style style = StyleUtil.getStyleForCamelText(diagram);
		text.setParentGraphicsAlgorithm(baseFigure);
		text.setStyle(style);
		text.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
		text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
		text.setFont(style.getFont());
		gaService.setLocationAndSize(text, upperSection.x + image_width + SECTION_OFFSET_X + LABEL_SPACER_X, upperSection.y, upperSection.width - image_width - SECTION_OFFSET_X - LABEL_SPACER_X - LABEL_SPACER_X, upperSection.height);
		markFigureTitleArea(text, image_width);
		
		// the lower section figure
		Rectangle lowerRectangle = gaService.createPlainRectangle(baseFigure);
		lowerRectangle.setParentGraphicsAlgorithm(baseFigure);
		lowerRectangle.setStyle(StyleUtil.getStyleForCamelClass(diagram));
		gaService.setLocationAndSize(lowerRectangle, lowerSection.x, lowerSection.y, lowerSection.width, lowerSection.height);
		lowerRectangle.setBackground(lowerSectionColor);
		lowerRectangle.setLineVisible(false);
		lowerRectangle.setFilled(true);
		markExpandableFigureArea(lowerRectangle, upperSectionHeight);
		
		// provide information to support direct-editing directly
		// after object creation (must be activated additionally)
		IDirectEditingInfo directEditingInfo = fp.getDirectEditingInfo();
		// set container shape for direct editing after object creation
		directEditingInfo.setMainPictogramElement(containerShape);
		// set shape and graphics algorithm where the editor for
		// direct editing shall be opened after object creation
		directEditingInfo.setPictogramElement(containerShape);
		directEditingInfo.setGraphicsAlgorithm(text);

		// add a chopbox anchor to the shape
		ChopboxAnchor ca = peCreateService.createChopboxAnchor(containerShape);
		fp.link(ca, element); 
	}
	
	/**
	 * marks the header area of the figure
	 * 
	 * @param area		the header section of the figure
	 * @param collapsedHeight	the initial area height in collapsed state
	 */
	public static void markFigureHeaderArea(GraphicsAlgorithm area, int collapsedHeight) {
		Graphiti.getPeService().setPropertyValue(area, DiagramUtils.PROP_ORIGINAL_SECTION_HEIGHT, "" + collapsedHeight);
		Graphiti.getPeService().setPropertyValue(area, DiagramUtils.PROP_SHAPE_KIND, DiagramUtils.PROP_SHAPE_KIND_HEADER);
	}
	
	/**
	 * marks the title area of the figure
	 * 
	 * @param area		the title / label graphics algorithm
	 * @param iconWidth	the width of the icon
	 */
	public static void markFigureTitleArea(GraphicsAlgorithm area, int iconWidth) {
		Graphiti.getPeService().setPropertyValue(area, DiagramUtils.PROP_IMG_WIDTH, "" + iconWidth);
		Graphiti.getPeService().setPropertyValue(area, DiagramUtils.PROP_SHAPE_KIND, DiagramUtils.PROP_SHAPE_KIND_TITLE);
	}
	
	/**
	 * marks an area of the figure as expandable area
	 * 
	 * @param area	the area to mark
	 * @param collapsedHeight	the initial area height in collapsed state
	 */
	public static void markExpandableFigureArea(GraphicsAlgorithm area, int collapsedHeight) {
		Graphiti.getPeService().setPropertyValue(area, DiagramUtils.PROP_SHAPE_KIND, DiagramUtils.PROP_SHAPE_KIND_EXPANDABLE);
		Graphiti.getPeService().setPropertyValue(area, DiagramUtils.PROP_ORIGINAL_SECTION_HEIGHT, "" + collapsedHeight);
	}
	
	/**
	 * marks the shape to be in collapsed state and saves the original size of the shape
	 * 
	 * @param containerShape
	 */
	public static void markFigureCollapsed(ContainerShape containerShape) {
		Graphiti.getPeService().setPropertyValue(containerShape, CollapseFeature.PROP_INITIAL_WIDTH, String.valueOf(containerShape.getGraphicsAlgorithm().getWidth()));
		Graphiti.getPeService().setPropertyValue(containerShape, CollapseFeature.PROP_INITIAL_HEIGHT, String.valueOf(containerShape.getGraphicsAlgorithm().getHeight()));
		Graphiti.getPeService().setPropertyValue(containerShape, CollapseFeature.PROP_COLLAPSED_STATE, Boolean.toString(true));
	}
}

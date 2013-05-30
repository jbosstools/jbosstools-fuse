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


import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.editor.utils.ImageUtils;
import org.fusesource.ide.camel.editor.utils.StyleUtil;
import org.fusesource.ide.camel.model.AbstractNode;


/**
 * @author lhein
 */
public class AddNodeFeature extends AbstractAddShapeFeature {

	// the additional size of the invisible rectangle at the right border
	// (this also equals the half width of the anchor to paint there)
	public static final int INVISIBLE_RECT_RIGHT = 6;
	public static final int VERTICAL_SPACER = 5;
	public static final int ROUNDED_RECTANGLE_WIDTH = 10;
	public static final int ROUNDED_RECTANGLE_HEIGHT = 10;
	public static final int SHADOW_WIDTH = 1;
	public static final int SHADOW_HEIGHT = 2;
	public static final int TOP_BOTTOM_SPACER = 1;
	
	public static int TEXT_LABEL_SIZE = 20;
	
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
		if (newObject instanceof AbstractNode) {
			// check if user wants to add to a diagram
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IAdd#add(org.eclipse.graphiti.features.context.IAddContext)
	 */
	@Override
	public PictogramElement add(IAddContext context) {
		final AbstractNode addedClass = (AbstractNode) context.getNewObject();
		//		final EClass addedClass = (EClass) context.getNewObject();
		final Diagram targetDiagram = (Diagram) context.getTargetContainer();

		// CONTAINER SHAPE WITH ROUNDED RECTANGLE
		final IPeCreateService peCreateService = Graphiti.getPeCreateService();
		final ContainerShape containerShape = peCreateService.createContainerShape(targetDiagram, true);

		String label = DiagramUtils.filterFigureLabel(addedClass.getDisplayText());
		
		// check whether the context has a size (e.g. from a create feature)
		// otherwise define a default size for the shape
		// now try to use the image dimension as figure dimension plus some height spacing
		// for the display label
		Font f = StyleUtil.getStyleForCamelText(getDiagram()).getFont();
		IDimension fd = GraphitiUi.getUiLayoutService().calculateTextSize(label, f);
		Dimension d = ImageUtils.getImageSize(addedClass.getIconName());

		// first determine dimensions from context -> or set defaults if context too small
		int width = context.getWidth() < 100 ? 100 : context.getWidth();
		int height = context.getHeight() < 80 ? 80 : context.getHeight();

		// then try to improve the width by evaluating the needed pixels for the font size
		width = Math.max(width, fd.getWidth() + INVISIBLE_RECT_RIGHT);
		TEXT_LABEL_SIZE = fd.getHeight() + VERTICAL_SPACER;

		if (d.width > 0 && d.width > width) {
			width = d.width + INVISIBLE_RECT_RIGHT;
		}
		if (d.height > 0) {
			height = d.height + TEXT_LABEL_SIZE;
		}
		
		height += TOP_BOTTOM_SPACER + TOP_BOTTOM_SPACER;
		
		final IGaService gaService = Graphiti.getGaService();
		final Rectangle invisibleRectangle = gaService.createInvisibleRectangle(containerShape);

		// create invisible outer rectangle expanded by
		// the width needed for the anchor
		gaService.setLocationAndSize(invisibleRectangle, context.getX(), context.getY(), width + INVISIBLE_RECT_RIGHT + SHADOW_WIDTH, height + SHADOW_HEIGHT);
		
		// shadow 
		RoundedRectangle shadowRectangle; // need to access it later
		{
			// create and set visible rectangle inside invisible rectangle
			shadowRectangle = gaService.createRoundedRectangle(invisibleRectangle, ROUNDED_RECTANGLE_WIDTH, ROUNDED_RECTANGLE_HEIGHT);
			shadowRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
			shadowRectangle.setStyle(StyleUtil.getShadowStyleForCamelClass(getDiagram()));
			gaService.setLocationAndSize(shadowRectangle, 0 + SHADOW_WIDTH, 0 + SHADOW_HEIGHT, width, height);
		}

		// the real figure
		RoundedRectangle roundedRectangle; // need to access it later
		{
			// create and set visible rectangle inside invisible rectangle
			roundedRectangle = gaService.createRoundedRectangle(invisibleRectangle, ROUNDED_RECTANGLE_WIDTH, ROUNDED_RECTANGLE_HEIGHT);
			roundedRectangle.setParentGraphicsAlgorithm(invisibleRectangle);
			roundedRectangle.setStyle(StyleUtil.getStyleForCamelClass(getDiagram()));
			gaService.setLocationAndSize(roundedRectangle, 0, 0, width, height);
		}
		
		// image
		{
			// create shape for image
			final Shape shape = peCreateService.createShape(containerShape, false);

			// create and set image
			final Image image = gaService.createImage(shape, ImageProvider.getKeyForLargeIcon(addedClass.getIconName()));
			gaService.setLocationAndSize(image, 0, 0 + TOP_BOTTOM_SPACER, width, height - TEXT_LABEL_SIZE + VERTICAL_SPACER);

			// if addedClass has no resource we add it to the resource of the diagram
			// in a real scenario the business model would have its own resource
//			if (addedClass.eResource() == null) {
//				getDiagram().eResource().getContents().add(addedClass);
//			}
		}

		// SHAPE WITH TEXT
		{
			// create shape for text
			final Shape shape = peCreateService.createShape(containerShape, false);

			// create and set text graphics algorithm
			final Text text = gaService.createDefaultText(getDiagram(), shape, label);

			Style style = StyleUtil.getStyleForCamelText(getDiagram());
			text.setStyle(style);
			text.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
			text.setFont(style.getFont());

			gaService.setLocationAndSize(text, 0, height - TEXT_LABEL_SIZE, width, TEXT_LABEL_SIZE);

			// create link and wire it
			//	      link(shape, addedClass);

			// provide information to support direct-editing directly
			// after object creation (must be activated additionally)
			final IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
			// set container shape for direct editing after object creation
			directEditingInfo.setMainPictogramElement(containerShape);
			// set shape and graphics algorithm where the editor for
			// direct editing shall be opened after object creation
			directEditingInfo.setPictogramElement(shape);
			directEditingInfo.setGraphicsAlgorithm(text);
		}

		// add a chopbox anchor to the shape
		peCreateService.createChopboxAnchor(containerShape);

		// call the layout feature
		layoutPictogramElement(containerShape);
		
		// create link and wire it
		link(containerShape, addedClass);
		getFeatureProvider().link(containerShape, addedClass);

		return containerShape;
	}
}

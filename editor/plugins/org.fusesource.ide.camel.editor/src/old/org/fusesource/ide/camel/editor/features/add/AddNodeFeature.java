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

package old.org.fusesource.ide.camel.editor.features.add;


import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.Image;
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
	public static final int INVISIBLE_RIGHT_SPACE = 0;
	public static final int IMAGE_LABEL_SPACE = 8;
	public static final int ROUNDED_RECTANGLE_WIDTH = 10;
	public static final int ROUNDED_RECTANGLE_HEIGHT = 10;
	public static final int BASE_RECT_SPACE_X = 4;
	public static final int BASE_RECT_SPACE_Y = 4;
	public static final int SHADOW_WIDTH = 5;
	public static final int SHADOW_HEIGHT = 5;
	public static final int LABEL_SPACER_X = 5;
	public static final int DEFAULT_FIGURE_CONTENT_WIDTH = 120;
	public static final int TEXT_LABEL_SIZE = 20;
	public static final int TOP_BOTTOM_SPACE = 5;
	
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
		IDimension fontDimension = GraphitiUi.getUiLayoutService().calculateTextSize(label, f);
		Dimension imageDimension = ImageUtils.getImageSize(addedClass.getIconName());

		int image_width = imageDimension.width;
		int image_height = imageDimension.height;
		
		int label_width = fontDimension.getWidth() + LABEL_SPACER_X + LABEL_SPACER_X;
		int label_height = Math.max(fontDimension.getHeight(), TEXT_LABEL_SIZE);

		int content_width = Math.max(Math.max(label_width, image_width), DEFAULT_FIGURE_CONTENT_WIDTH);
		
		// now lets define the shape dimensions
		org.eclipse.swt.graphics.Rectangle baseRect = new org.eclipse.swt.graphics.Rectangle(context.getX(), context.getY(), content_width + BASE_RECT_SPACE_X + BASE_RECT_SPACE_X + SHADOW_WIDTH + INVISIBLE_RIGHT_SPACE, BASE_RECT_SPACE_Y + BASE_RECT_SPACE_Y + image_height + SHADOW_HEIGHT + IMAGE_LABEL_SPACE + label_height + TOP_BOTTOM_SPACE + TOP_BOTTOM_SPACE);
		org.eclipse.swt.graphics.Rectangle shadowRect = new org.eclipse.swt.graphics.Rectangle(0 + BASE_RECT_SPACE_X + SHADOW_WIDTH, 0 + BASE_RECT_SPACE_Y + SHADOW_HEIGHT, content_width, image_height + label_height + IMAGE_LABEL_SPACE + TOP_BOTTOM_SPACE + TOP_BOTTOM_SPACE);
		org.eclipse.swt.graphics.Rectangle contentRect = new org.eclipse.swt.graphics.Rectangle(0 + BASE_RECT_SPACE_X, 0 + BASE_RECT_SPACE_Y, content_width, image_height + label_height + IMAGE_LABEL_SPACE + TOP_BOTTOM_SPACE + TOP_BOTTOM_SPACE);
		org.eclipse.swt.graphics.Rectangle imageRect = new org.eclipse.swt.graphics.Rectangle(0 + BASE_RECT_SPACE_X, 0 + BASE_RECT_SPACE_Y + TOP_BOTTOM_SPACE, content_width, image_height);
		org.eclipse.swt.graphics.Rectangle labelRect = new org.eclipse.swt.graphics.Rectangle(0 + BASE_RECT_SPACE_X, 0 + BASE_RECT_SPACE_Y + image_height + IMAGE_LABEL_SPACE, content_width, label_height + TOP_BOTTOM_SPACE);
		
		final IGaService gaService = Graphiti.getGaService();
		final RoundedRectangle baseRectangle = gaService.createRoundedRectangle(containerShape, ROUNDED_RECTANGLE_WIDTH, ROUNDED_RECTANGLE_HEIGHT);

		// create invisible outer rectangle expanded by
		// the width needed for the anchor
		gaService.setLocationAndSize(baseRectangle, baseRect.x, baseRect.y, baseRect.width, baseRect.height);
		baseRectangle.setFilled(false);
		baseRectangle.setLineVisible(false);
		
		// shadow 
		RoundedRectangle shadowRectangle; // need to access it later
		{
			// create and set visible rectangle inside invisible rectangle
			shadowRectangle = gaService.createRoundedRectangle(baseRectangle, ROUNDED_RECTANGLE_WIDTH, ROUNDED_RECTANGLE_HEIGHT);
			shadowRectangle.setParentGraphicsAlgorithm(baseRectangle);
			shadowRectangle.setStyle(StyleUtil.getShadowStyleForCamelClass(getDiagram()));
			shadowRectangle.setLineVisible(false);
			shadowRectangle.setFilled(true);
			gaService.setLocationAndSize(shadowRectangle, shadowRect.x, shadowRect.y, shadowRect.width, shadowRect.height);
		}

		// the real figure
		RoundedRectangle roundedRectangle; // need to access it later
		{
			// create and set visible rectangle inside invisible rectangle
			roundedRectangle = gaService.createRoundedRectangle(baseRectangle, ROUNDED_RECTANGLE_WIDTH, ROUNDED_RECTANGLE_HEIGHT);
			roundedRectangle.setParentGraphicsAlgorithm(baseRectangle);
			roundedRectangle.setStyle(StyleUtil.getStyleForCamelClass(getDiagram()));
			gaService.setLocationAndSize(roundedRectangle, contentRect.x, contentRect.y, contentRect.width, contentRect.height);
		}
		
		// image
		{
			// create shape for image
			final Shape shape = peCreateService.createShape(containerShape, false);

			// create and set image
			final Image image = gaService.createImage(shape, ImageProvider.getKeyForLargeIcon(addedClass.getIconName()));
			gaService.setLocationAndSize(image, imageRect.x, imageRect.y, imageRect.width, imageRect.height);

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

			gaService.setLocationAndSize(text, labelRect.x, labelRect.y, labelRect.width, labelRect.height);

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
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
	 */
	@Override
	public boolean hasDoneChanges() {
		return true;
	}
}

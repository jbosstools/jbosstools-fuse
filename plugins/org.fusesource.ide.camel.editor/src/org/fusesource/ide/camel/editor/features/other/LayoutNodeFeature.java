package org.fusesource.ide.camel.editor.features.other;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.fusesource.ide.camel.editor.features.add.AddNodeFeature;


public class LayoutNodeFeature extends AbstractLayoutFeature {

	private static final int MIN_HEIGHT = 30;

	private static final int MIN_WIDTH = 50;

	public LayoutNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canLayout(ILayoutContext context) {
		// return true, if pictogram element is linked to an EClass
		PictogramElement pe = context.getPictogramElement();
		if (!(pe instanceof ContainerShape))
			return false;
		EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
		return businessObjects.size() == 1 && businessObjects.get(0) instanceof EClass;
	}

	public boolean layout(ILayoutContext context) {
		boolean anythingChanged = false;
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
		// the containerGa is the invisible rectangle
		// containing the visible rectangle as its (first and only) child
		GraphicsAlgorithm rectangle = containerGa.getGraphicsAlgorithmChildren().get(0);

		// height of invisible rectangle
		if (containerGa.getHeight() < MIN_HEIGHT) {
			containerGa.setHeight(MIN_HEIGHT);
			anythingChanged = true;
		}

		// height of visible rectangle (same as invisible rectangle)
		if (rectangle.getHeight() != containerGa.getHeight()) {
			rectangle.setHeight(containerGa.getHeight());
			anythingChanged = true;
		}

		// width of invisible rectangle
		if (containerGa.getWidth() < MIN_WIDTH) {
			containerGa.setWidth(MIN_WIDTH);
			anythingChanged = true;
		}

		// width of visible rectangle (smaller than invisible rectangle)
		int rectangleWidth = containerGa.getWidth() - AddNodeFeature.INVISIBLE_RECT_RIGHT;
		if (rectangle.getWidth() != rectangleWidth) {
			rectangle.setWidth(rectangleWidth);
			anythingChanged = true;
		}

		// width of text and line (same as visible rectangle)
		for (Shape shape : containerShape.getChildren()) {
			GraphicsAlgorithm graphicsAlgorithm = shape.getGraphicsAlgorithm();
			IGaService gaService = Graphiti.getGaService();
			IDimension size = gaService.calculateSize(graphicsAlgorithm);
			if (rectangleWidth != size.getWidth()) {
				gaService.setWidth(graphicsAlgorithm, rectangleWidth);
				anythingChanged = true;
			}
		}

		return anythingChanged;
	}


}

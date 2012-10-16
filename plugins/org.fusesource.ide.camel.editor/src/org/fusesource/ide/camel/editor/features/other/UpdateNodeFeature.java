/**
 * 
 */
package org.fusesource.ide.camel.editor.features.other;


import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.features.add.AddNodeFeature;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.editor.utils.ImageUtils;
import org.fusesource.ide.camel.editor.utils.StyleUtil;
import org.fusesource.ide.camel.model.AbstractNode;


/**
 * @author lhein
 *
 */
public class UpdateNodeFeature extends AbstractUpdateFeature {

	public UpdateNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	public boolean canUpdate(IUpdateContext context) {
		// return true, if linked business object is an EClass
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof AbstractNode);
	}

	public IReason updateNeeded(IUpdateContext context) {
		// retrieve name from pictogram model
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					pictogramName = text.getValue();
				}
			}
		}

		// retrieve name from business model
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractNode) {
			AbstractNode eClass = (AbstractNode) bo;
			businessName = DiagramUtils.filterFigureLabel(eClass.getDisplayText());
		}

		// update needed, if names are different
		boolean updateNameNeeded = ((pictogramName == null && businessName != null) || (pictogramName != null && !pictogramName
				.equals(businessName)));
		if (updateNameNeeded) {
			return Reason.createTrueReason("Name is out of date"); //$NON-NLS-1$
		} else {
			return Reason.createFalseReason();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.func.IUpdate#update(org.eclipse.graphiti.features.context.IUpdateContext)
	 */
	public boolean update(IUpdateContext context) {
		// retrieve name from business model
		String businessName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractNode) {
			AbstractNode eClass = (AbstractNode) bo;
			businessName = DiagramUtils.filterFigureLabel(eClass.getDisplayText());
		}

		// Set name in pictogram model
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			// remember the actual width of the figure
			int oldWidth = cs.getGraphicsAlgorithm().getWidth();
			int oldHeight = cs.getGraphicsAlgorithm().getHeight();
			
			final IGaService gaService = Graphiti.getGaService();
	
			// check whether the context has a size (e.g. from a create feature)
			// otherwise define a default size for the shape
			// now try to use the image dimension as figure dimension plus some height spacing
			// for the display label
			Font f = StyleUtil.getStyleForCamelText(getDiagram()).getFont();
			IDimension fd = GraphitiUi.getUiLayoutService().calculateTextSize(businessName, f);
			Dimension d = ImageUtils.getImageSize(((AbstractNode)bo).getIconName());
			
			// first determine dimensions from context -> or set defaults if context too small
			int width = pictogramElement.getGraphicsAlgorithm().getWidth() < 100 ? 100 : pictogramElement.getGraphicsAlgorithm().getWidth();
			int height = pictogramElement.getGraphicsAlgorithm().getHeight() < 80 ? 80 : pictogramElement.getGraphicsAlgorithm().getHeight();

			// then try to improve the width by evaluating the needed pixels for the font size
			width = Math.max(width, fd.getWidth() + AddNodeFeature.INVISIBLE_RECT_RIGHT);
			AddNodeFeature.TEXT_LABEL_SIZE = fd.getHeight() + AddNodeFeature.VERTICAL_SPACER;

			if (d.width > 0 && d.width > width) {
				width = d.width + AddNodeFeature.INVISIBLE_RECT_RIGHT;
			}
			if (d.height > 0) {
				height = d.height + AddNodeFeature.TEXT_LABEL_SIZE;
			}
			
			height += AddNodeFeature.TOP_BOTTOM_SPACER + AddNodeFeature.TOP_BOTTOM_SPACER;
			
			// if the new width is bigger than the current one we will make the figure bigger
			if (width>oldWidth) {
				pictogramElement.getGraphicsAlgorithm().setWidth(width);
				for (GraphicsAlgorithm g : pictogramElement.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
					g.setWidth(width);
				}
			}
			// if the new height is bigger than the current one we will make the figure bigger
			if (height>oldHeight) {
				pictogramElement.getGraphicsAlgorithm().setHeight(height);
				for (GraphicsAlgorithm g : pictogramElement.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
					g.setHeight(height);
				}
			}
 			
			boolean finished_label = false;
			boolean finished_icon = false;
			// now also adapt the text label of the figure
			for (Shape shape : cs.getChildren()) {
				// if the new width is bigger we also make the inner shapes bigger
				if (width>oldWidth) {
					shape.getGraphicsAlgorithm().setWidth(width);	
				}
				if (height>oldHeight) {
					shape.getGraphicsAlgorithm().setHeight(height);	
				}
				// special handling for the text shape as its the figures label
				if (shape.getGraphicsAlgorithm() instanceof Text) {
					Text text = (Text) shape.getGraphicsAlgorithm();
					// set the new figure label
					text.setValue(businessName);
					
					finished_label = true;
				} else if (shape.getGraphicsAlgorithm() instanceof Image) {
					// update the icon image
					AbstractNode addedClass = (AbstractNode)bo;
					
					// set the new icon id - refresh will to the rest
					String iconKey = ImageProvider.getKeyForLargeIcon(addedClass.getIconName());
					((Image)shape.getGraphicsAlgorithm()).setId(iconKey);
					
					finished_icon = true;
				}
				if (finished_icon && finished_label) {
					// and update the diagram layout afterwards
					DiagramOperations.layoutDiagram(Activator.getDiagramEditor());
					return true;
				}
			}
		}

		return false;
	}
}

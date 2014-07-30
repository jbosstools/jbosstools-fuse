/**
 * 
 */
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
package org.fusesource.ide.camel.editor.features.other;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.editor.utils.DiagramUtils;
import org.fusesource.ide.camel.model.AbstractNode;

/**
 * @author lhein
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
 			
			boolean finished_label = false;
			boolean finished_icon = false;
			// now also adapt the text label of the figure
			for (Shape shape : cs.getChildren()) {
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
//					// and update the diagram layout afterwards
//					DiagramOperations.layoutDiagram(Activator.getDiagramEditor());
					return false;
				}
			}
		}

		return false;
	}
}

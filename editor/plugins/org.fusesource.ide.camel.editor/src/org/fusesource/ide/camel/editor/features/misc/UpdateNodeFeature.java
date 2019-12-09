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
package org.fusesource.ide.camel.editor.features.misc;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.provider.ImageProvider;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class UpdateNodeFeature extends AbstractUpdateFeature {

	private boolean changed = false;
	
	public UpdateNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		// return true, if linked business object is an EClass
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return bo instanceof AbstractCamelModelElement;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		// retrieve name from pictogram model
		String pictogramName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (GraphicsAlgorithm shape : cs.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
				if (shape instanceof Text) {
					Text text = (Text) shape;
					pictogramName = text.getValue();
					break;
				}
			}
		}

		// retrieve name from business model
		String businessName = null;
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement eClass = (AbstractCamelModelElement) bo;
			// do check if underlying xml node changed / document changed
			CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
			if (editor.getModel() != null) {
				AbstractCamelModelElement bo2 = editor.getModel().findNode(eClass.getId());
				if (bo2 != null && !bo2.getXmlNode().isEqualNode(eClass.getXmlNode())) {
					return Reason.createTrueReason("The Model has been changed. Please update the figure."); //$NON-NLS-1$
				}
				businessName = eClass.getDisplayText();
			}
		}

		// update needed, if names are different
		boolean updateNameNeeded = (pictogramName == null && businessName != null) || (pictogramName != null && !pictogramName
				.equals(businessName));
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
	@Override
	public boolean update(IUpdateContext context) {
		this.changed = false;
		
		// retrieve name from business model
		String businessName = null;
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement eClass = (AbstractCamelModelElement) bo;
		
			// do check if underlying xml node changed / document changed
			CamelDesignEditor editor = (CamelDesignEditor)getDiagramBehavior().getDiagramContainer();
			AbstractCamelModelElement bo2 = editor.getModel().findNode(eClass.getId());
			if (bo2 != null && !bo2.getXmlNode().isEqualNode(eClass.getXmlNode())) {
				link(pictogramElement, bo2);
				this.changed = true;
			}
			
			businessName = eClass.getDisplayText();
		}

		// Set name in pictogram model
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
 			
			// now also adapt the text label of the figure
			for (GraphicsAlgorithm shape : cs.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
				// special handling for the text shape as its the figures label
				if (shape instanceof Text) {
					Text text = (Text) shape;
					// set the new figure label
					text.setValue(businessName);
					
					this.changed = true;
				} else if (shape instanceof Image) {
					// update the icon image
					AbstractCamelModelElement addedClass = (AbstractCamelModelElement)bo;
					// set the new icon id - refresh will to the rest
					String iconKey = ImageProvider.getKeyForDiagramIcon(addedClass.isEndpointElement(), addedClass.getIconName());
					((Image)shape).setId(iconKey);
					this.changed = true;
				}
			}
		}

		return this.changed;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#hasDoneChanges()
	 */
	@Override
	public boolean hasDoneChanges() {
		return this.changed;
	}
}

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
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;


public class DirectEditNodeFeature extends AbstractDirectEditingFeature {

	public DirectEditNodeFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public int getEditingType() {
		// there are several possible editor-types supported:
		// text-field, checkbox, color-chooser, combobox, ...
		return TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		// support direct editing, if it is a EClass, and the user clicked
		// directly on the text and not somewhere else in the rectangle
		if (bo instanceof AbstractCamelModelElement && ga instanceof Text) {
			// EClass eClass = (EClass) bo;
			// additionally the flag isFrozen must be false
			// return !eClass.isFrozen();
			return true;
		}
		// direct editing not supported in all other cases
		return false;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		// return the current name of the EClass
		PictogramElement pe = context.getPictogramElement();
		//EClass eClass = (EClass) getBusinessObjectForPictogramElement(pe);
		AbstractCamelModelElement node = (AbstractCamelModelElement)getBusinessObjectForPictogramElement(pe);
		//return eClass.getName();
		return node.getDisplayText();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		if (value.length() < 1)
			return "Please enter any text as class name."; //$NON-NLS-1$
		if (value.contains(" ")) //$NON-NLS-1$
			return "Spaces are not allowed in class names."; //$NON-NLS-1$
		if (value.contains("\n")) //$NON-NLS-1$
			return "Line breaks are not allowed in class names."; //$NON-NLS-1$

		// null means, that the value is valid
		return null;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		// set the new name for the EClass
		PictogramElement pe = context.getPictogramElement();
		//EClass eClass = (EClass) getBusinessObjectForPictogramElement(pe);

		// TODO: rework to fit new model - feature currently not in use
//		if (bo instanceof Endpoint) {
//			Endpoint ep = (Endpoint)getBusinessObjectForPictogramElement(pe);
//			ep.setUri(value);
//		} else if (bo instanceof UniversalEIPNode) {
//			UniversalEIPNode uNode = (UniversalEIPNode)bo;
//			if( uNode.getNodeTypeId().equals("bean")) {
//				UniversalEIPNode bean = (UniversalEIPNode)getBusinessObjectForPictogramElement(pe);
//				bean.setShortPropertyValue("ref", value);
//			}
//		}
		
		// Explicitly update the shape to display the new value in the diagram
		// Note, that this might not be necessary in future versions of Graphiti
		// (currently in discussion)

		// we know, that pe is the Shape of the Text, so its container is the
		// main shape of the EClass
		updatePictogramElement(((Shape) pe).getContainer());
	}
}

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

package org.fusesource.ide.camel.editor.features.delete;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Flow;
import org.fusesource.ide.camel.model.RouteContainer;


/**
 * @author lhein
 */
public class DeleteNodeFeature extends DefaultDeleteFeature {

	/**
	 * 
	 * @param fp
	 */
	public DeleteNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#preDelete(org.eclipse.graphiti.features.context.IDeleteContext)
	 */
	@Override
	public void preDelete(IDeleteContext context) {
		super.preDelete(context);

		// now delete the BO from our model
		PictogramElement pe = context.getPictogramElement();
		Object[] businessObjectsForPictogramElement = getAllBusinessObjectsForPictogramElement(pe);
		if (businessObjectsForPictogramElement != null &&
				businessObjectsForPictogramElement.length > 0) {
			Object bo = businessObjectsForPictogramElement[0];
			if (bo instanceof Flow) {
				deleteFlowFromModel((Flow) bo);
			} else if (bo instanceof AbstractNode) {
				deleteBOFromModel((AbstractNode)bo);
			} else if (bo instanceof EReferenceImpl) {
				EReferenceImpl eimpl = (EReferenceImpl) bo;
				EClassifier eType = eimpl.getEType();
				if (eType instanceof AbstractNode) {
					AbstractNode target = (AbstractNode) eType;
					System.out.println("==== trying to zap the target: " + target);
					EObject eContainer = eimpl.eContainer();
					if (eContainer instanceof AbstractNode) {
						AbstractNode source = (AbstractNode) eContainer;
						System.out.println("==== trying to source: " + source + " -> target: " + target);
						source.removeConnection(target);
					}
				}
			} else {
				Activator.getLogger().warning("Cannot figure out Node or Flow from BO: " + bo);
			}
		}
	}

	private void deleteBOFromModel(AbstractNode nodeToRemove) {
		// we can't remove null objects or the root of the routes
		if (nodeToRemove == null || nodeToRemove instanceof RouteContainer) return;

		// lets remove all connections
		nodeToRemove.detach();
	}

	private void deleteFlowFromModel(Flow bo) {
		bo.disconnect();
	}

}

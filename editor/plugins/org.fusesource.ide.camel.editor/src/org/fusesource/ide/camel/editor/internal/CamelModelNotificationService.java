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

package org.fusesource.ide.camel.editor.internal;

import java.util.ArrayList;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;

/**
 * @author lhein
 */
public class CamelModelNotificationService extends DefaultNotificationService {
	
	public CamelModelNotificationService(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.internal.DefaultNotificationService#updatePictogramElements(org.eclipse.graphiti.mm.pictograms.PictogramElement[])
	 */
	@Override
	public void updatePictogramElements(PictogramElement[] dirtyPes) {
		for (PictogramElement pe : dirtyPes) {
			Object bo = getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(pe);
			UpdateContext uc = new UpdateContext(pe);
			getDiagramTypeProvider().getFeatureProvider().updateIfPossible(uc);
		}
		super.updatePictogramElements(dirtyPes);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.editor.internal.DefaultNotificationService#calculateRelatedPictogramElements(java.lang.Object[])
	 */
	@Override
	public PictogramElement[] calculateRelatedPictogramElements(Object[] changedBOs) {
		ArrayList<PictogramElement> relatedBOs = new ArrayList<PictogramElement>();
		for (Object bo : changedBOs) {
			PictogramElement picElem = (PictogramElement)bo;
			if (picElem != null) {
				Object obo = getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(picElem);
				if (obo != null) {
					CamelModelElement bo1 = (CamelModelElement)obo;
					CamelDesignEditor editor = (CamelDesignEditor)getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer();
					CamelModelElement bo2 = editor.getModel().findNode(bo1.getId());
					if (bo2 != null && bo2.getXmlNode().isEqualNode(bo1.getXmlNode()) == false) {
						relatedBOs.add(picElem);
					}					
				}
			}
		}
		return relatedBOs.toArray(new PictogramElement[relatedBOs.size()]);
	}
}

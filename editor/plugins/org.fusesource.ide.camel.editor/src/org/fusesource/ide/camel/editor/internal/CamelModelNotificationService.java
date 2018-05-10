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
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class CamelModelNotificationService extends DefaultNotificationService {
	
	public CamelModelNotificationService(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}
	
	@Override
	public void updatePictogramElements(PictogramElement[] dirtyPes) {
		for (PictogramElement pe : dirtyPes) {
			UpdateContext uc = new UpdateContext(pe);
			getDiagramTypeProvider().getFeatureProvider().updateIfPossible(uc);
		}
		super.updatePictogramElements(dirtyPes);
	}
	
	@Override
	public PictogramElement[] calculateRelatedPictogramElements(Object[] changedBOs) {
		List<PictogramElement> relatedBOs = new ArrayList<>();
		for (Object bo : changedBOs) {
			PictogramElement picElem = (PictogramElement)bo;
			if (picElem != null) {
				Object obo = getDiagramTypeProvider().getFeatureProvider().getBusinessObjectForPictogramElement(picElem);
				if (obo != null) {
					AbstractCamelModelElement bo1 = (AbstractCamelModelElement)obo;
					CamelDesignEditor editor = (CamelDesignEditor)getDiagramTypeProvider().getDiagramBehavior().getDiagramContainer();
					if (editor != null && editor.getModel() != null) {
						AbstractCamelModelElement bo2 = editor.getModel().findNode(bo1.getId());
						if (bo2 != null && bo1.getXmlNode() != null &&
								bo2.getXmlNode() != null && 
								!bo2.getXmlNode().isEqualNode(bo1.getXmlNode())) {
							relatedBOs.add(picElem);
						}
					}
				}
			}
		}
		return relatedBOs.toArray(new PictogramElement[relatedBOs.size()]);
	}
}

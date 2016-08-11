/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.features.delete;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;

/**
 * @author lhein
 */
public class RemoveFigureFeature extends DefaultRemoveFeature {

	/**
	 * @param fp
	 */
	public RemoveFigureFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultRemoveFeature#preRemove(org.eclipse.graphiti.features.context.IRemoveContext)
	 */
	@Override
	public void preRemove(IRemoveContext context) {
		super.preRemove(context);
		
		// now delete the BO from our model
		PictogramElement pe = context.getPictogramElement();
		Object[] businessObjectsForPictogramElement = getAllBusinessObjectsForPictogramElement(pe);
		if (businessObjectsForPictogramElement != null && businessObjectsForPictogramElement.length > 0) {
			Object bo = businessObjectsForPictogramElement[0];
			if (bo instanceof CamelElementConnection) {
				NodeUtils.deleteFlowFromModel((CamelElementConnection) bo);
			} else if (bo instanceof AbstractCamelModelElement) {
				NodeUtils.deleteBOFromModel(getFeatureProvider(), (AbstractCamelModelElement)bo);
			} else {
				CamelEditorUIActivator.pluginLog().logWarning("Cannot figure out Node or Flow from BO: " + bo);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultRemoveFeature#postRemove(org.eclipse.graphiti.features.context.IRemoveContext)
	 */
	@Override
	public void postRemove(IRemoveContext context) {
		super.postRemove(context);
		DiagramOperations.layoutDiagram(CamelUtils.getDiagramEditor());
	}
}

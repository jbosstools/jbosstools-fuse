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
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;

/**
 * @author lhein
 */
public class DeleteFigureFeature extends DefaultDeleteFeature {

	/**
	 * @param fp
	 */
	public DeleteFigureFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.features.DefaultDeleteFeature#deleteBusinessObject(java.lang.Object)
	 */
	@Override
	protected void deleteBusinessObject(Object bo) {
		super.deleteBusinessObject(bo);
		
		if (bo != null ) {
			if (bo instanceof CamelElementConnection) {
				deleteFlowFromModel((CamelElementConnection) bo);
			} else if (bo instanceof CamelModelElement) {
				deleteBOFromModel((CamelModelElement)bo);
			} else {
				CamelEditorUIActivator.pluginLog().logWarning("Cannot figure out Node or Flow from BO: " + bo);
			}
		}
		
		DiagramOperations.layoutDiagram(CamelUtils.getDiagramEditor());
	}

	private void deleteBOFromModel(CamelModelElement nodeToRemove) {
		// lets remove all connections
		if (nodeToRemove.getParent() != null) nodeToRemove.getParent().removeChildElement(nodeToRemove);
		if (nodeToRemove.getInputElement() != null) nodeToRemove.getInputElement().setOutputElement(null);
		if (nodeToRemove.getOutputElement() != null) nodeToRemove.getOutputElement().setInputElement(null);
	}

	private void deleteFlowFromModel(CamelElementConnection bo) {
		bo.disconnect();
	}
}

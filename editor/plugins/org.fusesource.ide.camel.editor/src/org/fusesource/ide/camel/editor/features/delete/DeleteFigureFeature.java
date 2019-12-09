/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.editor.features.delete;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.CamelUtils;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;

/**
 * @author lhein
 */
public class DeleteFigureFeature extends DefaultDeleteFeature {
	
	/**
	 * Used in test only
	 */
	public static final String SKIP_ASKING_DELETE_CONFIRMATION = "SKIP_ASKING_DELETE_CONFIRMATION";
	private AbstractCamelModelElement inputOfDeletedElement = null;
	private AbstractCamelModelElement outputOfDeletedElement = null;

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
				NodeUtils.deleteFlowFromModel((CamelElementConnection) bo);
			} else if (bo instanceof AbstractCamelModelElement) {
				NodeUtils.deleteBOFromModel(getFeatureProvider(), (AbstractCamelModelElement)bo);
			} else {
				CamelEditorUIActivator.pluginLog().logWarning("Cannot figure out Node or Flow from BO: " + bo);
			}
		}
	}
	
	@Override
	public void preDelete(IDeleteContext context) {
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(context.getPictogramElement());
		if(bo instanceof AbstractCamelModelElement){
			inputOfDeletedElement = ((AbstractCamelModelElement) bo).getInputElement();
			outputOfDeletedElement = ((AbstractCamelModelElement) bo).getOutputElement();
		}
		super.preDelete(context);
	}
	
	@Override
	public void postDelete(IDeleteContext context) {
		if(inputOfDeletedElement != null && outputOfDeletedElement != null){
			NodeUtils.reconnectNodes(getFeatureProvider(), inputOfDeletedElement, outputOfDeletedElement);
		}
		super.postDelete(context);
		DiagramOperations.layoutDiagram(CamelUtils.getDiagramEditor(), true);
	}
	
	/** 
	 * Overridden for test purpose
	 */
	@Override
	protected boolean getUserDecision(IDeleteContext context) {
		Object shouldSkipAskingDeleteConfirmation = context.getProperty(SKIP_ASKING_DELETE_CONFIRMATION);
		if("true".equals(shouldSkipAskingDeleteConfirmation)){
			return true;
		} else {
			return super.getUserDecision(context);
		}
	}
}

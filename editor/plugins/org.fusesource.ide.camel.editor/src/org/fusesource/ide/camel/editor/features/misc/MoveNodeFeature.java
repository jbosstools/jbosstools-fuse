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

package org.fusesource.ide.camel.editor.features.misc;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.fusesource.ide.camel.editor.features.create.ext.CreateFigureFeature;
import org.fusesource.ide.camel.editor.features.delete.DeleteFigureFeature;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.w3c.dom.Node;

/**
 * @author lhein
 */
public class MoveNodeFeature extends DefaultMoveShapeFeature {
	
	public MoveNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object modelToMove = getBusinessObjectForPictogramElement(pe);
		Object sourceContainer = getBusinessObjectForPictogramElement(context.getSourceContainer());
		Object targetContainer = getBusinessObjectForPictogramElement(context.getTargetContainer());
		if(modelToMove instanceof CamelRouteElement){
			return false;
		}
		return areDifferentContainers(sourceContainer, targetContainer) && canContain(targetContainer, modelToMove);
	}

	private boolean areDifferentContainers(Object sourceContainer, Object targetContainer) {
		return sourceContainer != null && !sourceContainer.equals(targetContainer);
	}
	
	private boolean canContain(Object targetContainer, Object movableModel) {
		if(targetContainer instanceof AbstractCamelModelElement && movableModel instanceof AbstractCamelModelElement){
			return NodeUtils.isValidChild((AbstractCamelModelElement)targetContainer, (AbstractCamelModelElement)movableModel);
		}
		return false;
	}
	
	@Override
	public void moveShape(IMoveShapeContext context) {
		PictogramElement pe = context.getPictogramElement();
		AbstractCamelModelElement modelToMove = (AbstractCamelModelElement)getBusinessObjectForPictogramElement(pe);
		String id = modelToMove.getId();
		String description = modelToMove.getDescription();
		Eip underlyingMetaModelObject = modelToMove.getUnderlyingMetaModelObject();
		Node xmlNode = modelToMove.getXmlNode();
		
		DeleteFigureFeature deleteFigureFeature = new DeleteFigureFeature(getFeatureProvider());
		IDeleteContext deleteContext = new DeleteContext(pe);
		deleteContext.putProperty(DeleteFigureFeature.SKIP_ASKING_DELETE_CONFIRMATION, "true"); //$NON-NLS-1$
		deleteFigureFeature.delete(deleteContext);
		
		CreateFigureFeature createFigureFeature = new CreateFigureFeature(getFeatureProvider(), id, description, underlyingMetaModelObject, xmlNode);
		CreateContext createContext = new CreateContext();
		createContext.setTargetContainer(context.getTargetContainer());
		createFigureFeature.create(createContext);
		
		
	}
	
}

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
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;
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
		Object modelToMoveObject = getBusinessObjectForPictogramElement(pe);
		if(modelToMoveObject instanceof AbstractCamelModelElement){
			AbstractCamelModelElement modelToMove = (AbstractCamelModelElement) modelToMoveObject;
			if(modelToMove instanceof CamelRouteElement){
				return false;
			}
			Object sourceContainer = getBusinessObjectForPictogramElement(context.getSourceContainer());
			Object targetContainer = getBusinessObjectForPictogramElement(context.getTargetContainer());
			Eip underlyingMetaModelObject = modelToMove.getUnderlyingMetaModelObject();
			return (isMovingToAnotherContainer(modelToMove, sourceContainer, targetContainer)
					|| isInsertingOnConnectionNotAdjacent(context, modelToMove, targetContainer)
					|| isAppendOrPrepending(context, modelToMove, targetContainer))
					&& new CreateFigureFeature(getFeatureProvider(), getName(), getDescription(), underlyingMetaModelObject).canCreate(createCreateContext(context));
		}
		return false;
	}

	private boolean isAppendOrPrepending(IMoveShapeContext context, AbstractCamelModelElement modelToMove, Object targetContainer) {
		if(context.getTargetConnection() == null && targetContainer instanceof AbstractCamelModelElement){
			return (((AbstractCamelModelElement) targetContainer).getInputElement() == null || ((AbstractCamelModelElement) targetContainer).getOutputElement() == null)
					&& canContain(((AbstractCamelModelElement) targetContainer).getParent(), modelToMove);
		}
		return false;
	}

	private boolean isInsertingOnConnectionNotAdjacent(IMoveShapeContext context, AbstractCamelModelElement modelToMove, Object targetContainer) {
		Connection targetConnection = context.getTargetConnection();
		if(targetConnection != null){
			AbstractCamelModelElement sourceOfConnection = NodeUtils.getNode(getFeatureProvider(), targetConnection.getStart());
			AbstractCamelModelElement targetOfConnection = NodeUtils.getNode(getFeatureProvider(), targetConnection.getEnd());
			return !modelToMove.equals(sourceOfConnection) && !modelToMove.equals(targetOfConnection) && canContain(targetContainer, modelToMove);
		}
		return false;
	}

	private boolean isMovingToAnotherContainer(AbstractCamelModelElement modelToMove, Object sourceContainer, Object targetContainer) {
		return areDifferentContainers(sourceContainer, targetContainer) && canContain(targetContainer, modelToMove);
	}

	private boolean areDifferentContainers(Object sourceContainer, Object targetContainer) {
		return sourceContainer != null && !sourceContainer.equals(targetContainer);
	}
	
	private boolean canContain(Object targetContainer, AbstractCamelModelElement movableModel) {
		if(targetContainer instanceof AbstractCamelModelElement){
			return NodeUtils.isValidChild((AbstractCamelModelElement)targetContainer, movableModel);
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
		CreateContext createContext = createCreateContext(context);
		createFigureFeature.create(createContext);
		
	}

	private CreateContext createCreateContext(IMoveShapeContext context) {
		CreateContext createContext = new CreateContext();
		createContext.setTargetContainer(context.getTargetContainer());
		createContext.setTargetConnection(context.getTargetConnection());
		return createContext;
	}
	
}

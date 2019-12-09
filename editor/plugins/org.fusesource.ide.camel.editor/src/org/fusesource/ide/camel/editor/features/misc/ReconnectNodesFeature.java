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
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.impl.ReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.fusesource.ide.camel.editor.CamelDesignEditor;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;

/**
 * @author lhein
 */
public class ReconnectNodesFeature extends DefaultReconnectionFeature {

	/**
	 * 
	 * @param fp
	 */
	public ReconnectNodesFeature(IFeatureProvider fp) {
		super(fp);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#canReconnect(org.eclipse.graphiti.features.context.IReconnectionContext)
	 */
	@Override
	public boolean canReconnect(IReconnectionContext context) {
		return super.canReconnect(context) && isReconnectValid(context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#postReconnect(org.eclipse.graphiti.features.context.IReconnectionContext)
	 */
	@Override
	public void postReconnect(IReconnectionContext context) {
		super.postReconnect(context);
		
		if (context.getReconnectType().equals(ReconnectionContext.RECONNECT_SOURCE)) {
			// dragging the source anchor
			// delete the old connection / add the new connection
			AbstractCamelModelElement target = NodeUtils.getNode(getFeatureProvider(), context.getConnection().getEnd());
			AbstractCamelModelElement oldSource = NodeUtils.getNode(getFeatureProvider(), context.getOldAnchor());
			AbstractCamelModelElement newSource = NodeUtils.getNode(getFeatureProvider(), context.getNewAnchor());
			
			if (oldSource.equals(target.getInputElement())) {
				target.setInputElement(null);
				oldSource.setOutputElement(null);
			}
			if (oldSource.equals(target.getOutputElement())) {
				target.setOutputElement(null);
				oldSource.setInputElement(null);
			}
			new CamelElementConnection(newSource, target);
		} else {
			// dragging the target anchor
			// delete the old connection / add the new connection
			AbstractCamelModelElement source = NodeUtils.getNode(getFeatureProvider(), context.getConnection().getStart());
			AbstractCamelModelElement oldTarget = NodeUtils.getNode(getFeatureProvider(), context.getOldAnchor());
			AbstractCamelModelElement newTarget = NodeUtils.getNode(getFeatureProvider(), context.getNewAnchor());
			
			if (oldTarget.equals(source.getOutputElement())) {
				source.setOutputElement(null);
				oldTarget.setInputElement(null);
			}
			if (oldTarget.equals(source.getInputElement())) {
				source.setInputElement(null);
				oldTarget.setOutputElement(null);
			}
			new CamelElementConnection(source, newTarget);
		}
		DiagramOperations.layoutDiagram((CamelDesignEditor)getDiagramBehavior().getDiagramContainer());
	}
	
	/**
	 * Returns the EClass belonging to the anchor, or null if not available.
	 */
	private AbstractCamelModelElement getNode(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor
					.getParent());
			if (obj instanceof AbstractCamelModelElement) {
				return (AbstractCamelModelElement) obj;
			}
		}
		return null;
	}
	
	private boolean isReconnectValid(IReconnectionContext context) {
		if (context.getReconnectType().equals(ReconnectionContext.RECONNECT_SOURCE)) {
			AbstractCamelModelElement newTarget = getNode(context.getConnection().getEnd());
			AbstractCamelModelElement source = getNode(context.getNewAnchor());
			return canElementsConnect(source, newTarget, context.getReconnectType());	
		} else {
			AbstractCamelModelElement source = getNode(context.getConnection().getStart());
			AbstractCamelModelElement newTarget = getNode(context.getNewAnchor());
			return canElementsConnect(source, newTarget, context.getReconnectType());
		}
	}
	
	/**
	 * checks wether 2 elements can be reconnected to each other
	 * 
	 * @param source
	 * @param newTarget
	 * @return
	 */
	public static boolean canElementsConnect(AbstractCamelModelElement source, AbstractCamelModelElement target) {
		return canElementsConnect(source, target, ReconnectionContext.RECONNECT_TARGET);
	}
	
	/**
	 * checks wether 2 elements can be reconnected to each other
	 * 
	 * @param source
	 * @param newTarget
	 * @return
	 */
	public static boolean canElementsConnect(AbstractCamelModelElement source, AbstractCamelModelElement target, String reconnectType) {
		if (ReconnectionContext.RECONNECT_SOURCE.equals(reconnectType)) {
			return 	source != null && 
					target != null && 
					source.equals(target) == false &&
					target.getLastInFlow().equals(source) == false &&
					source.getParent().equals(target.getParent()) && 
					source.getOutputElement() == null && 
					(source.getInputElement() == null || source.getInputElement().equals(target) == false) && // prevent circular connection
					source instanceof CamelRouteElement == false && 
					target instanceof CamelRouteElement == false;		
		} else {
			return 	source != null && 
					target != null &&
					source.equals(target) == false &&
					source.getParent().equals(target.getParent()) && 
					target.getInputElement() == null &&
					source instanceof CamelRouteElement == false && 
					target instanceof CamelRouteElement == false &&
					!target.equals(source.getFirstInFlow());		
		}
	}
}

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
import org.eclipse.graphiti.features.context.IReconnectionContext;
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
		CamelElementConnection f = new CamelElementConnection(source, newTarget);
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
		AbstractCamelModelElement source = getNode(context.getConnection().getStart());
		AbstractCamelModelElement newTarget = getNode(context.getNewAnchor());
		
		return canElementsConnect(source, newTarget);
	}
	
	/**
	 * checks wether 2 elements can be reconnected to each other
	 * 
	 * @param source
	 * @param newTarget
	 * @return
	 */
	public static boolean canElementsConnect(AbstractCamelModelElement source, AbstractCamelModelElement newTarget) {
		return 	source != null && 
				newTarget != null && 
				source.getParent().equals(newTarget.getParent()) && 
				newTarget.getInputElement() == null &&
				source instanceof CamelRouteElement == false && 
				newTarget instanceof CamelRouteElement == false &&
				!newTarget.equals(source.getFirstInFlow());		
	}
}

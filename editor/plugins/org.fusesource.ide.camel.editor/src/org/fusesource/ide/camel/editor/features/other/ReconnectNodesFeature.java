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
package org.fusesource.ide.camel.editor.features.other;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Flow;

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

	/**
	 * Returns the EClass belonging to the anchor, or null if not available.
	 */
	private AbstractNode getNode(Anchor anchor) {
		if (anchor != null) {
			Object obj = getBusinessObjectForPictogramElement(anchor
					.getParent());
			if (obj instanceof AbstractNode) {
				return (AbstractNode) obj;
			}
		}
		return null;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#postReconnect(org.eclipse.graphiti.features.context.IReconnectionContext)
	 */
	@Override
	public void postReconnect(IReconnectionContext context) {
		super.postReconnect(context);
		
		// delete the old connection / add the new connection
		AbstractNode source = getNode(context.getConnection().getStart());
		AbstractNode oldTarget = getNode(context.getOldAnchor());
		AbstractNode newTarget = getNode(context.getNewAnchor());
		
		source.removeConnection(oldTarget);
		Flow f = new Flow(source, newTarget);
		source.addConnection(f);
	}	
}

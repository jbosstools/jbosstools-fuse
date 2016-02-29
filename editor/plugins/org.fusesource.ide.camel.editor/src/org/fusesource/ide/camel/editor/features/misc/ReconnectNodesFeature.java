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
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

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

	
	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#postReconnect(org.eclipse.graphiti.features.context.IReconnectionContext)
	 */
	@Override
	public void postReconnect(IReconnectionContext context) {
		super.postReconnect(context);
		
		// delete the old connection / add the new connection
		AbstractCamelModelElement source = getNode(context.getConnection().getStart());
		AbstractCamelModelElement oldTarget = getNode(context.getOldAnchor());
		AbstractCamelModelElement newTarget = getNode(context.getNewAnchor());
		
		if (source.getOutputElement().equals(oldTarget)) {
			source.setOutputElement(null);
			oldTarget.setInputElement(null);
		}
		if (source.getInputElement().equals(oldTarget)) {
			source.setInputElement(null);
			oldTarget.setOutputElement(null);
		}
		CamelElementConnection f = new CamelElementConnection(source, newTarget);
	}	
}

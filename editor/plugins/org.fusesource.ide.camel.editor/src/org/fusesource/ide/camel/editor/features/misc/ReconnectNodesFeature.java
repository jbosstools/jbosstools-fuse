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
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelElementConnection;

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
	 * @see org.eclipse.graphiti.features.impl.DefaultReconnectionFeature#postReconnect(org.eclipse.graphiti.features.context.IReconnectionContext)
	 */
	@Override
	public void postReconnect(IReconnectionContext context) {
		super.postReconnect(context);
		
		// delete the old connection / add the new connection
		AbstractCamelModelElement source = NodeUtils.getNode(getFeatureProvider(), context.getConnection().getStart());
		AbstractCamelModelElement oldTarget = NodeUtils.getNode(getFeatureProvider(), context.getOldAnchor());
		AbstractCamelModelElement newTarget = NodeUtils.getNode(getFeatureProvider(), context.getNewAnchor());
		
		if (source.getOutputElement().equals(oldTarget)) {
			source.setOutputElement(null);
			oldTarget.setInputElement(null);
		}
		if (oldTarget.equals(source.getInputElement())) {
			source.setInputElement(null);
			oldTarget.setOutputElement(null);
		}
		CamelElementConnection f = new CamelElementConnection(source, newTarget);
	}	
}

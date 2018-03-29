/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.model.service.core.model;

import org.w3c.dom.Node;

public abstract class AbstractRestCamelModelElement extends AbstractCamelModelElement {

	/**
	 * @param parent The parent in the Camel Model
	 * @param underlyingNode The XML node that this Object is representing
	 */
	public AbstractRestCamelModelElement(AbstractCamelModelElement parent, Node underlyingNode) {
		super(parent, underlyingNode);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#shouldParseNode()
	 */
	@Override
	protected boolean shouldParseNode() {
		// we don't want to parse global config nodes and just reuse the xml node 
		// directly - see FUSETOOLS-1884 (except for Beans and REST elements)
		return false;
	}
}

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

import org.fusesource.ide.camel.model.service.core.model.eips.RestElementEIP;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author bfitzpat
 */
public class RestElement extends AbstractRestCamelModelElement {

	public static final String REST_TAG = "rest"; //$NON-NLS-1$

	/**
	 * @param parent
	 * @param underlyingNode
	 */
	public RestElement(AbstractCamelModelElement parent, Node underlyingNode) {
		super(parent, underlyingNode, true);
		setUnderlyingMetaModelObject(new RestElementEIP());
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#getKind(java.lang.String)
	 */
	@Override
	public String getKind(String name) {
		// due to the missing EIP as underlying meta model we have to tell AbstractCamelModelElement what
		// kind the attribute is ... so if we got other than ATTRIBUTE please adapt this methods logic!
		return NODE_KIND_ATTRIBUTE;
	}

	/**
	 * parses the children of this node
	 */
	@Override
	protected void parseChildren() {
		NodeList children = getXmlNode().getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node tmp = children.item(i);
			if (tmp.getNodeType() != Node.ELEMENT_NODE) continue;
			parseNode(tmp);
		}
	}

	private void parseNode(Node node) {
		RestVerbElement rve = new RestVerbElement(this, node);
		rve.initialize();
		addChildElement(rve);
	}
}

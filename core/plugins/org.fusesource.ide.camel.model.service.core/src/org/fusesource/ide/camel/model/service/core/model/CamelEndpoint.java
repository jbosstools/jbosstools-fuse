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
package org.fusesource.ide.camel.model.service.core.model;

import org.fusesource.ide.foundation.core.xml.XmlEscapeUtility;

/**
 * @author lhein
 */
public class CamelEndpoint extends AbstractCamelModelElement implements IFuseDetailsPropertyContributor {

	/**
	 * @param parent
	 * @param underlyingNode
	 */
	public CamelEndpoint(String uri) {
		super(null, null);
		setParameter("uri", XmlEscapeUtility.unescape(uri));
	}

	/**
	 * retrieves the uri of the endpoint
	 * 
	 * @return
	 */
	public String getUri() {
		return (String)getParameter("uri");
	}
	
	/**
	 * sets the uri of the endpoint
	 * 
	 * @param uri
	 */
	public void setUri(String uri) {
		setParameter("uri", XmlEscapeUtility.unescape(uri));
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#setParent(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void setParent(AbstractCamelModelElement parent) {
		super.setParent(parent);
		if (parent != null && parent.getXmlNode() != null && getXmlNode() != null) {
			boolean alreadyChild = false;
			for (int i = 0; i < parent.getXmlNode().getChildNodes().getLength(); i++) {
				if (parent.getXmlNode().getChildNodes().item(i).isEqualNode(getXmlNode())) {
					alreadyChild = true;
					break;
				}
			}
			if (!alreadyChild) {				
				parent.getXmlNode().appendChild(getXmlNode());	
			}
		}
	}
}

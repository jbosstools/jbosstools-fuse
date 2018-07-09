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

import org.fusesource.ide.camel.model.service.core.model.eips.RestVerbElementEIP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author bfitzpat
 */
public class RestVerbElement extends AbstractRestCamelModelElement {

	public static final String GET_VERB = "get"; //$NON-NLS-1$
	public static final String POST_VERB = "post"; //$NON-NLS-1$
	public static final String PUT_VERB = "put"; //$NON-NLS-1$
	public static final String PATCH_VERB = "patch"; //$NON-NLS-1$
	public static final String DELETE_VERB = "delete"; //$NON-NLS-1$
	public static final String HEAD_VERB = "head"; //$NON-NLS-1$
	public static final String TRACE_VERB = "trace"; //$NON-NLS-1$
	public static final String CONNECT_VERB = "connect"; //$NON-NLS-1$
	public static final String OPTIONS_VERB = "options"; //$NON-NLS-1$
	
	private static final String TO_ELEMENT_TAG = "to"; //$NON-NLS-1$
	
	/**
	 * @param parent
	 * @param underlyingNode
	 */
	public RestVerbElement(AbstractCamelModelElement parent, Node underlyingNode) {
		super(parent, underlyingNode, true);
		setUnderlyingMetaModelObject(new RestVerbElementEIP(getNodeTypeId()));
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
	
	@Override
	public void setParameter(String name, Object value) {
		if (RestVerbElementEIP.PROP_TO_URI.equals(name)) {
			setToUri((String) value);
		} else {
			setParameter(name, value, false);
		}
	}
	
	@Override
	public Object getParameter(String name) {
		if (RestVerbElementEIP.PROP_TO_URI.equals(name)) {
			return getToUri();
		} else {
			return super.getParameter(name);
		}
	}
	
	public String getToUri() {
		NodeList list = getXmlNode().getChildNodes();
		if (list != null && list.getLength() > 0) {
			for (int i = 0; i < list.getLength(); i++) {
				Node tmp = list.item(i);
				if (tmp instanceof Element && ((Element) tmp).getTagName().contentEquals("to")) {
					Node attr = tmp.getAttributes().getNamedItem(URI_PARAMETER_KEY);
					if (attr != null) {
						return attr.getNodeValue();
					}
				}
			}
		}
		return null;
	}

	private boolean foundChild(Node tmp, String value) {
		if (tmp instanceof Element && ((Element) tmp).getTagName().contentEquals(TO_ELEMENT_TAG)) {
			Node attr = tmp.getAttributes().getNamedItem(URI_PARAMETER_KEY);
			if (attr != null) {
				if (value == null || value.isEmpty()) {
					// remove from parent?
					getXmlNode().removeChild(tmp);
					notifyAboutDeletion(this);
				} else {
					if (!attr.getNodeValue().contentEquals(value)) {
						attr.setNodeValue(value);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public void setToUri(String value) {
		NodeList list = getXmlNode().getChildNodes();
		boolean foundTo = false;
		if (list != null && list.getLength() > 0) {
			for (int i = 0; i < list.getLength(); i++) {
				Node tmp = list.item(i);
				foundTo = foundChild(tmp, value);
				if (foundTo) { 
					break;
				}
			}
		}
		if (!foundTo) {
			Document xmlDoc = getXmlNode().getOwnerDocument();
			Element toElement = xmlDoc.createElement(TO_ELEMENT_TAG);
			toElement.setAttribute(URI_PARAMETER_KEY, value);
			getXmlNode().appendChild(toElement);
		}
	}
}

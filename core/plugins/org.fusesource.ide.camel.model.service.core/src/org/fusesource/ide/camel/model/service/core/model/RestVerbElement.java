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

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.eips.RestVerbElementEIP;
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
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#parseAttributes()
	 */
	@Override
	protected void parseAttributes() {
		Eip eip = getUnderlyingMetaModelObject();
		if (eip != null) {
			for (Parameter param : getUnderlyingMetaModelObject().getParameters()) {
				initAttribute(param.getName());
			}
		} else {
			CamelModelServiceCoreActivator.pluginLog().
				logWarning("ParseAttributes: Missing EIP for REST Verb. Ignored."); //$NON-NLS-1$
		}
	}
	
	private void initAttribute(String paramName) {
		String value = parseAttribute(paramName);
		if (value != null) {
			setParameter(paramName, value);
		}
	}	
	
	private String parseAttribute(String name) {
		Node tmp = getXmlNode().getAttributes().getNamedItem(name);
		if (tmp != null) {
			return tmp.getNodeValue();
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#shouldParseNode()
	 */
	@Override
	protected boolean shouldParseNode() {
		// we do want to parse REST Verb contents
		return true;
	}

	public String getToUri() {
		NodeList list = getXmlNode().getChildNodes();
		if (list != null && list.getLength() > 0) {
			for (int i = 0; i < list.getLength(); i++) {
				Node tmp = list.item(i);
				if (tmp instanceof Element && ((Element) tmp).getTagName().contentEquals(TO_ELEMENT_TAG)) {
					Node attr = tmp.getAttributes().getNamedItem(URI_PARAMETER_KEY);
					if (attr != null) {
						return attr.getNodeValue();
					}
				}
			}
		}
		return null;
	}

	public void setToUri(String value) {
		if (!getChildElements().isEmpty()) {
			CamelBasicModelElement cme = (CamelBasicModelElement) getChildElements().get(0); // grab first element
			cme.setParameter(URI_PARAMETER_KEY, value);
			return;
		}
		// may have issue where inner TO does not exist - this is an invalid state that would
		// only be created by manually hacking the XML source and is flagged as invalid by the
		// validator. This message is just to give some indication to the user
		CamelModelServiceCoreActivator.pluginLog().
			logError("Setting inner To URI attribute on REST operation failed due to missing To element."); //$NON-NLS-1$
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
		AbstractCamelModelElement acme = new CamelBasicModelElement(this, node);		
		acme.initialize();
		addChildElement(acme);
	}

}

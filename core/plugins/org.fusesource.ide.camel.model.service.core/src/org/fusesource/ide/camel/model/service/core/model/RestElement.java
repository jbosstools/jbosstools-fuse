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

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.eips.RestElementEIP;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author bfitzpat
 */
public class RestElement extends AbstractRestCamelModelElement {

	public static final String REST_TAG = "rest"; //$NON-NLS-1$
	private Map<String, AbstractCamelModelElement> restOperations = new HashMap<>();

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
			CamelModelServiceCoreActivator.pluginLog().logWarning("ParseAttributes: Missing EIP for REST element. Ignored.");
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
		// we do want to parse REST contents
		return true;
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
		this.restOperations.put(rve.getId(), rve);
	}

	/**
	 * @return the restOperations
	 */
	public Map<String, AbstractCamelModelElement> getRestOperations() {
		return this.restOperations;
	}

	/**
	 * deletes all rest operation definitions
	 */
	public void clearRestOperations() {
		this.restOperations.clear();
	}
	
	/**
	 * @param restOperations the restOperations to set
	 */
	public void setRestOperations(Map<String, AbstractCamelModelElement> restOperations) {
		this.restOperations = restOperations;
	}
	
	/**
	 * adds a rest element
	 * 
	 * @param def
	 */
	public void addRestOperation(AbstractCamelModelElement def) {
		if (restOperations.containsKey(def.getId())) {
			return;
		}
		restOperations.put(def.getId(), def);
		def.setParent(this);
		boolean childExists = false;
		for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
			if(def.getXmlNode() != null && getXmlNode().getChildNodes().item(i).isEqualNode(def.getXmlNode())) {
				childExists = true;
				break;
			}
		}
		if (def.getXmlNode() == null) {
			Node n = createElement(def.getNodeTypeId(), getXmlNode() != null ? getXmlNode().getPrefix() : null);
			def.setXmlNode(n);
			def.updateXMLNode();
		}
		if (!childExists) {
			getXmlNode().insertBefore(def.getXmlNode(), getFirstChild(getXmlNode()));
		}
	}

	/**
	 * removes the rest element from the context
	 * 
	 * @param def
	 */
	public void removeRestOperation(AbstractCamelModelElement def) {
		if (this.restOperations.containsKey(def.getId())) {
			this.restOperations.remove(def.getId());
			boolean childExists = false;
			for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
				if(getXmlNode().getChildNodes().item(i).isEqualNode(def.getXmlNode())) {
					childExists = true;
					break;
				}
			}
			if (childExists) {
				getXmlNode().removeChild(def.getXmlNode());
			}
			notifyAboutDeletion(def);
		}
	}
}

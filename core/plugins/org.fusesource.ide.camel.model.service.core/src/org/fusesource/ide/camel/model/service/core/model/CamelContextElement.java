/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
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

import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * this object represents the camel context. It can contains various endpoint 
 * definitions and the only children are routes.
 * 
 * @author lhein
 */
public class CamelContextElement extends CamelModelElement {
	
	public static final String ATTR_Id = "id";
	public static final String ATTR_UseMDCLogging = "useMDCLogging";
	public static final String ATTR_UseBreadcrumb = "useBreadcrumb";
	
	/**
	 * contains endpoint definitions stored using their ID value
	 */
	private Map<String, CamelModelElement> endpointDefinitions = new HashMap<String, CamelModelElement>();
	
	/**
	 * contains the dataformat definitions stored using their ID value
	 */
	private Map<String, CamelModelElement> dataformats = new HashMap<String, CamelModelElement>();
	
	/**
	 * 
	 */
	public CamelContextElement(CamelFile camelFile, Node underlyingNode) {
		super(camelFile, underlyingNode);
	}
	
	/**
	 * @return the endpointDefinitions
	 */
	public Map<String, CamelModelElement> getEndpointDefinitions() {
		return this.endpointDefinitions;
	}
	
	/**
	 * @param endpointDefinitions the endpointDefinitions to set
	 */
	public void setEndpointDefinitions(Map<String, CamelModelElement> endpointDefinitions) {
		this.endpointDefinitions = endpointDefinitions;
	}

	/**
	 * adds an endpoint definition
	 * 
	 * @param def
	 */
	public void addEndpointDefinition(CamelModelElement def) {
		if (this.endpointDefinitions.containsKey(def.getId())) return;
		this.endpointDefinitions.put(def.getId(), def);
		boolean childExists = false;
		for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
			if(def.getXmlNode() != null && getXmlNode().getChildNodes().item(i).isEqualNode(def.getXmlNode())) {
				childExists = true;
				break;
			}
		}
		if (def.getXmlNode() == null) {
			Node n = createElement("endpoint", getXmlNode() != null ? getXmlNode().getPrefix() : null);
			def.setXmlNode(n);
			def.updateXMLNode();
		}
		if (!childExists) {
			getXmlNode().insertBefore(def.getXmlNode(), getFirstChild(getXmlNode()));
		}
	}
	
	/**
	 * removes the endpoint from the context
	 * 
	 * @param def
	 */
	public void removeEndpointDefinition(CamelModelElement def) {
		if (this.endpointDefinitions.containsKey(def.getId())) {
			this.endpointDefinitions.remove(def.getId());
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
		}
	}
	
	/**
	 * deletes all endpoint definitions
	 */
	public void clearEndpointDefinitions() {
		this.endpointDefinitions.clear();
	}
	
	/**
	 * @return the dataformats
	 */
	public Map<String, CamelModelElement> getDataformats() {
		return this.dataformats;
	}
	
	/**
	 * @param dataformats the dataformats to set
	 */
	public void setDataformats(Map<String, CamelModelElement> dataformats) {
		this.dataformats = dataformats;
	}
	
	/**
	 * adds a dataformat to the context
	 * 
	 * @param df
	 */
	public void addDataFormat(CamelModelElement df) {
		if (this.dataformats.containsKey(df.getId())) return;
		this.dataformats.put((String)df.getId(), df);
		boolean childExists = false;
		Node dataFormatsNode = null;
		for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
			Node n = getXmlNode().getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			if (CamelUtils.getTranslatedNodeName(n).equals(DATA_FORMATS_NODE_NAME)) {
				dataFormatsNode = n;
				break;
			}
		}
		if (dataFormatsNode == null) {
			// first create a dataFormats node
			dataFormatsNode = createElement(DATA_FORMATS_NODE_NAME, this != null && this.getXmlNode() != null ? this.getXmlNode().getPrefix() : null);
			getXmlNode().insertBefore(dataFormatsNode, getXmlNode().getFirstChild());
		}
		for (int i=0; i<dataFormatsNode.getChildNodes().getLength(); i++) {
		    if(df.getXmlNode() != null && dataFormatsNode.getChildNodes().item(i).isEqualNode(df.getXmlNode())) {
				childExists = true;
				break;
			}
		}
		if (df.getXmlNode() == null) {
			Node n = createElement(df.getUnderlyingMetaModelObject().getName(), getXmlNode() != null ? getXmlNode().getPrefix() : null);
			df.setXmlNode(n);
			df.updateXMLNode();
		}
		if (!childExists) {
			dataFormatsNode.appendChild(df.getXmlNode());
		}
	}
	
	/**
	 * removes the dataformat from the context
	 * 
	 * @param df
	 */
	public void removeDataFormat(CamelModelElement df) {
		if (this.dataformats.containsKey(df.getId())) {
			this.dataformats.remove(df.getId());
			Node dataFormatsNode = null;
			for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
				Node n = getXmlNode().getChildNodes().item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE) continue;
				if (CamelUtils.getTranslatedNodeName(n).equals(DATA_FORMATS_NODE_NAME)) {
					dataFormatsNode = n;
					break;
				}
			}
			
			if (dataFormatsNode == null) dataFormatsNode = df.getXmlNode().getParentNode();
			
			Node nodeToDelete = null;
			if (dataFormatsNode != null) { 
				for (int i=0; i<dataFormatsNode.getChildNodes().getLength(); i++) {
					Node n = dataFormatsNode.getChildNodes().item(i);
					if(n.getNodeType() == Node.ELEMENT_NODE && n.isEqualNode(df.getXmlNode())) {
						nodeToDelete = n;
						break;
					}
				}
				if (nodeToDelete != null) {
					dataFormatsNode.removeChild(nodeToDelete);
					if (nodeToDelete.getParentNode() != null) nodeToDelete.getParentNode().removeChild(nodeToDelete);
					if (df.getXmlNode().getParentNode() != null) df.getXmlNode().getParentNode().removeChild(df.getXmlNode());
					int cnt = 0;
					for (int i=0; i<dataFormatsNode.getChildNodes().getLength(); i++) {
						Node n = dataFormatsNode.getChildNodes().item(i);
						if (n.getNodeType() != Node.ELEMENT_NODE) continue;
						cnt++;
					}
					if (cnt == 0) {
						// no more dataformats left...clean up the parent node
						dataFormatsNode.getParentNode().removeChild(dataFormatsNode);
					}
				}
			}
		}
	}
	
	/**
	 * deletes all data formats
	 */
	public void clearDataFormats() {
		this.dataformats.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#parseNode()
	 */
	@Override
	protected void parseNode() {
		super.parseNode();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#ensureUniqueID(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
	 */
	@Override
	public void ensureUniqueID(CamelModelElement elem) {
		super.ensureUniqueID(elem);
	}
	
	/**
	 * parses direct attributes of the node
	 */
	protected void parseAttributes() {
		Node tmp = this.getXmlNode().getAttributes().getNamedItem(ATTR_Id);
		if (tmp != null && tmp.getNodeValue() != null && tmp.getNodeValue().trim().length()>0) {
			setId(tmp.getNodeValue());
		} 
		tmp = this.getXmlNode().getAttributes().getNamedItem(ATTR_UseMDCLogging);
		if (tmp != null) {
			setParameter(ATTR_UseMDCLogging, tmp.getNodeValue());
			
		}
		tmp = this.getXmlNode().getAttributes().getNamedItem(ATTR_UseBreadcrumb);
		if (tmp != null) {
			setParameter(ATTR_UseBreadcrumb, tmp.getNodeValue());
			
		}
	}
	
	/**
	 * parses the children of this node
	 */
	protected void parseChildren() {
		NodeList children = getXmlNode().getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			Node tmp = children.item(i);
			if (tmp.getNodeType() != Node.ELEMENT_NODE) continue;
			if (CamelUtils.getTranslatedNodeName(tmp).equals(DATA_FORMATS_NODE_NAME)) {
				NodeList dfs = tmp.getChildNodes();
				for (int y=0; y<dfs.getLength(); y++) {
					Node tmp_df = dfs.item(y);
					if (tmp_df.getNodeType() != Node.ELEMENT_NODE) continue;
					CamelModelElement cme = new CamelModelElement(this, tmp_df);
					cme.initialize();
					this.dataformats.put(cme.getId(), cme);
				}
			} else if (CamelUtils.getTranslatedNodeName(tmp).equals(ENDPOINT_NODE_NAME)) {
				CamelModelElement cme = new CamelModelElement(this, tmp);
				cme.initialize();
				this.endpointDefinitions.put(cme.getId(), cme);
			} else if (CamelUtils.getTranslatedNodeName(tmp).equals(ROUTE_NODE_NAME)) {
				CamelRouteElement cme = new CamelRouteElement(this, tmp);
				cme.initialize();
				addChildElement(cme);
			} else {
				CamelModelServiceCoreActivator.pluginLog().logWarning("Unexpected child element of the context: " + CamelUtils.getTranslatedNodeName(tmp));
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#getCamelContext()
	 */
	@Override
	public CamelContextElement getCamelContext() {
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#getNodeTypeId()
	 */
	@Override
	public String getNodeTypeId() {
		return CAMEL_CONTEXT_NODE_NAME;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.CamelModelElement#supportsBreakpoint()
	 */
	@Override
	public boolean supportsBreakpoint() {
		return false;
	}
}

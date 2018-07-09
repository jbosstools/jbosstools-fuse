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
import java.util.List;
import java.util.Map;

import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * this object represents the camel context. It can contains various endpoint 
 * definitions and the only children are routes.
 * 
 * @author lhein
 */
public class CamelContextElement extends CamelRouteContainerElement {
	
	/**
	 * contains endpoint definitions stored using their ID value
	 */
	private Map<String, AbstractCamelModelElement> endpointDefinitions = new HashMap<>();
	
	/**
	 * contains the dataformat definitions stored using their ID value
	 */
	private Map<String, AbstractCamelModelElement> dataformats = new HashMap<>();
	
	/**
	 * Contains REST configuration elements
	 */
	private Map<String, AbstractCamelModelElement> restConfigurations = new HashMap<>();
	
	/**
	 * Contains REST elements
	 */
	private Map<String, AbstractCamelModelElement> restElements = new HashMap<>();
	
	/**
	 * 
	 */
	public CamelContextElement(CamelFile camelFile, Node underlyingNode) {
		super(camelFile, underlyingNode);
	}
	
	/**
	 * @return the endpointDefinitions
	 */
	public Map<String, AbstractCamelModelElement> getEndpointDefinitions() {
		return this.endpointDefinitions;
	}
	
	/**
	 * @param endpointDefinitions the endpointDefinitions to set
	 */
	public void setEndpointDefinitions(Map<String, AbstractCamelModelElement> endpointDefinitions) {
		this.endpointDefinitions = endpointDefinitions;
	}

	/**
	 * adds an endpoint definition
	 * 
	 * @param def
	 */
	public void addEndpointDefinition(AbstractCamelModelElement def) {
		if (endpointDefinitions.containsKey(def.getId())) {
			return;
		}
		endpointDefinitions.put(def.getId(), def);
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
	public void removeEndpointDefinition(AbstractCamelModelElement def) {
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
			notifyAboutDeletion(def);
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
	public Map<String, AbstractCamelModelElement> getDataformats() {
		return this.dataformats;
	}
	
	/**
	 * @param dataformats the dataformats to set
	 */
	public void setDataformats(Map<String, AbstractCamelModelElement> dataformats) {
		this.dataformats = dataformats;
	}
	
	/**
	 * adds a dataformat to the context
	 * 
	 * @param df
	 */
	public void addDataFormat(AbstractCamelModelElement df) {
		if (this.dataformats.containsKey(df.getId())) return;
		this.dataformats.put((String)df.getId(), df);
		boolean childExists = false;
		Node dataFormatsNode = null;
		for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
			Node n = getXmlNode().getChildNodes().item(i);
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			if (CamelUtils.getTagNameWithoutPrefix(n).equals(DATA_FORMATS_NODE_NAME)) {
				dataFormatsNode = n;
				break;
			}
		}
		if (dataFormatsNode == null) {
			// first create a dataFormats node
			dataFormatsNode = createElement(DATA_FORMATS_NODE_NAME, this != null && this.getXmlNode() != null ? this.getXmlNode().getPrefix() : null);
			final Node firstNotEndpointNode = getFirstNotEndpointNode();
			if (firstNotEndpointNode != null) {
				getXmlNode().insertBefore(dataFormatsNode, firstNotEndpointNode);
			} else {
				getXmlNode().appendChild(dataFormatsNode);
			}
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
	 * @return
	 */
	private Node getFirstNotEndpointNode() {
		final NodeList childNodes = getXmlNode().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node currentNode = childNodes.item(i);
			if (currentNode instanceof Element && !"endpoint".equals(currentNode.getLocalName())) {
				return currentNode;
			}
		}
		return null;
	}
	
	/**
	 * removes the dataformat from the context
	 * 
	 * @param df
	 */
	public void removeDataFormat(AbstractCamelModelElement df) {
		if (this.dataformats.containsKey(df.getId())) {
			this.dataformats.remove(df.getId());
			Node dataFormatsNode = null;
			for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
				Node n = getXmlNode().getChildNodes().item(i);
				if (n.getNodeType() != Node.ELEMENT_NODE) continue;
				if (CamelUtils.getTagNameWithoutPrefix(n).equals(DATA_FORMATS_NODE_NAME)) {
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
			notifyAboutDeletion(df);
		}
	}
	
	/**
	 * deletes all data formats
	 */
	public void clearDataFormats() {
		this.dataformats.clear();
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
		String tagNameWithoutPrefix = CamelUtils.getTagNameWithoutPrefix(node);
		if (DATA_FORMATS_NODE_NAME.equals(tagNameWithoutPrefix)) {
			NodeList dfs = node.getChildNodes();
			for (int y=0; y<dfs.getLength(); y++) {
				Node tmp_df = dfs.item(y);
				if (tmp_df.getNodeType() != Node.ELEMENT_NODE) continue;
				CamelBasicModelElement cme = new CamelBasicModelElement(this, tmp_df);
				cme.initialize();
				this.dataformats.put(cme.getId(), cme);
			}
		} else if (ENDPOINT_NODE_NAME.equals(tagNameWithoutPrefix)) {
			CamelBasicModelElement cme = new CamelBasicModelElement(this, node);
			cme.initialize();
			this.endpointDefinitions.put(cme.getId(), cme);
		} else if (ROUTE_NODE_NAME.equals(tagNameWithoutPrefix)) {
			CamelRouteElement cme = new CamelRouteElement(this, node);
			cme.initialize();
			addChildElement(cme);
		} else if (REST_CONFIGURATION_NODE_NAME.equals(tagNameWithoutPrefix)) {
			RestConfigurationElement cme = new RestConfigurationElement(this, node);
			cme.initialize();
			this.restConfigurations.put(cme.getId(), cme);
		} else if (REST_NODE_NAME.equals(tagNameWithoutPrefix)) {
			RestElement cme = new RestElement(this, node);
			cme.initialize();
			this.restElements.put(cme.getId(), cme);
		} else {
			CamelModelServiceCoreActivator.pluginLog().logWarning("Unexpected child element of the context: " + tagNameWithoutPrefix);
		}
	}
	
	@Override
	public List<AbstractCamelModelElement> findAllNodesWithId(String nodeId) {
		List<AbstractCamelModelElement> result = super.findAllNodesWithId(nodeId);
		if (getDataformats().containsKey(nodeId)) {
			result.add(getDataformats().get(nodeId));
		}
		if (getEndpointDefinitions().containsKey(nodeId)) {
			result.add(getEndpointDefinitions().get(nodeId));
		}
		if (getRestConfigurations().containsKey(nodeId)) {
			result.add(getRestConfigurations().get(nodeId));
		}
		if (getRestElements().containsKey(nodeId)) {
			result.add(getRestElements().get(nodeId));
		}
		return result;
	}

	/**
	 * searches the model for a node with the given id
	 * 
	 * @param nodeId
	 * @return the node or null
	 */
	@Override
	public AbstractCamelModelElement findNode(String nodeId) {
		AbstractCamelModelElement res = super.findNode(nodeId);
		if (res != null) {
			return res;
		}
		if (getDataformats().containsKey(nodeId)) {
			return getDataformats().get(nodeId);
		}
		if (getEndpointDefinitions().containsKey(nodeId)) {
			return getEndpointDefinitions().get(nodeId);
		}
		if (getRestConfigurations().containsKey(nodeId)) {
			return getRestConfigurations().get(nodeId);
		}
		if (getRestElements().containsKey(nodeId)) {
			return getRestElements().get(nodeId);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement#getRouteContainer()
	 */
	@Override
	public CamelRouteContainerElement getRouteContainer() {
		return this;
	}

	/**
	 * @return the restConfigurations
	 */
	public Map<String, AbstractCamelModelElement> getRestConfigurations() {
		return this.restConfigurations;
	}
	
	/**
	 * @param restConfigurations the restConfigurations to set
	 */
	public void setRestConfigurations(Map<String, AbstractCamelModelElement> restConfigurations) {
		this.restConfigurations = restConfigurations;
	}

	/**
	 * deletes all rest configuration definitions
	 */
	public void clearRestConfigurations() {
		this.restConfigurations.clear();
	}
	
	/**
	 * adds a rest configuration
	 * 
	 * @param def
	 */
	public void addRestConfiguration(AbstractCamelModelElement def) {
		if (restConfigurations.containsKey(def.getId())) {
			return;
		}
		restConfigurations.put(def.getId(), def);
		boolean childExists = false;
		for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
			if(def.getXmlNode() != null && getXmlNode().getChildNodes().item(i).isEqualNode(def.getXmlNode())) {
				childExists = true;
				break;
			}
		}
		if (def.getXmlNode() == null) {
			Node n = createElement(REST_CONFIGURATION_NODE_NAME, getXmlNode() != null ? getXmlNode().getPrefix() : null);
			def.setXmlNode(n);
			def.updateXMLNode();
		}
		if (!childExists) {
			getXmlNode().insertBefore(def.getXmlNode(), getRestConfigurationEntryPoint());
		}
	}
	
	private Node getRestConfigurationEntryPoint() {
		final NodeList childNodes = getXmlNode().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node currentNode = childNodes.item(i);
			if (currentNode instanceof Element && !DATA_FORMATS_NODE_NAME.equals(currentNode.getLocalName())) {
				return currentNode;
			}
		}
		return getFirstChild(getXmlNode());
	}
	
	private Node getRestElementEntryPoint() {
		final NodeList childNodes = getXmlNode().getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node currentNode = childNodes.item(i);
			if (currentNode instanceof Element && REST_CONFIGURATION_NODE_NAME.equals(currentNode.getLocalName())) {
				return currentNode;
			}
		}
		return getFirstChild(getXmlNode());
	}

	/**
	 * removes the rest configuration from the context
	 * 
	 * @param def
	 */
	public void removeRestConfiguration(AbstractCamelModelElement def) {
		if (this.restConfigurations.containsKey(def.getId())) {
			this.restConfigurations.remove(def.getId());
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
	
	/**
	 * @return the restElements
	 */
	public Map<String, AbstractCamelModelElement> getRestElements() {
		return this.restElements;
	}

	/**
	 * deletes all rest element definitions
	 */
	public void clearRestElements() {
		this.restElements.clear();
	}
	
	/**
	 * @param restElements the restElements to set
	 */
	public void setRestElements(Map<String, AbstractCamelModelElement> restElements) {
		this.restElements = restElements;
	}
	
	/**
	 * adds a rest element
	 * 
	 * @param def
	 */
	public void addRestElement(AbstractCamelModelElement def) {
		if (restElements.containsKey(def.getId())) {
			return;
		}
		restElements.put(def.getId(), def);
		boolean childExists = false;
		for (int i=0; i<getXmlNode().getChildNodes().getLength(); i++) {
			if(def.getXmlNode() != null && getXmlNode().getChildNodes().item(i).isEqualNode(def.getXmlNode())) {
				childExists = true;
				break;
			}
		}
		if (def.getXmlNode() == null) {
			Node n = createElement(REST_NODE_NAME, getXmlNode() != null ? getXmlNode().getPrefix() : null);
			def.setXmlNode(n);
			def.updateXMLNode();
		}
		if (!childExists) {
			getXmlNode().insertBefore(def.getXmlNode(), getRestElementEntryPoint().getNextSibling());
		}
	}

	/**
	 * removes the rest element from the context
	 * 
	 * @param def
	 */
	public void removeRestElement(AbstractCamelModelElement def) {
		if (this.restElements.containsKey(def.getId())) {
			this.restElements.remove(def.getId());
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

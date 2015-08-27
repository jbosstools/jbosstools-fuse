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
	}
	
	/**
	 * removes the endpoint from the context
	 * 
	 * @param def
	 */
	public void removeEndpointDefinition(CamelModelElement def) {
		this.endpointDefinitions.remove(def.getId());
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
	}
	
	/**
	 * removes the dataformat from the context
	 * 
	 * @param df
	 */
	public void removeDataFormat(CamelModelElement df) {
		this.dataformats.remove(df.getId());
	}
	
	/**
	 * deletes all data formats
	 */
	public void clearDataFormats() {
		this.dataformats.clear();
	}
	
	/**
	 * parses direct attributes of the node
	 */
	protected void parseAttributes() {
		Node tmp = this.getXmlNode().getAttributes().getNamedItem(ATTR_Id);
		if (tmp != null) {
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
			if (tmp.getNodeName().equals("#text")) continue;
			CamelModelElement cme = new CamelModelElement(this, tmp);
			if (cme.getUnderlyingMetaModelObject() != null && cme.getUnderlyingMetaModelObject().getName().equals("dataformat")) {
				this.dataformats.put(cme.getId(), cme);
			} else if (cme.getUnderlyingMetaModelObject() != null && cme.getUnderlyingMetaModelObject().getName().equals("endpoint")) {
				this.endpointDefinitions.put(cme.getId(), cme);
			} else {
				addChildElement(cme);
			}
		}
	}
}

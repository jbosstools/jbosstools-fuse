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
import java.util.UUID;

import org.eclipse.core.resources.IResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * this object represents the camel xml file. It can be of a schema type
 * like Spring or Blueprint. It can also contain Bean Definitions for things
 * like connection factories, property placeholder beans or loadbalancers etc.
 * 
 * The only children for the camel file is the camel context.
 * 
 * @author lhein
 */
public class CamelFile extends CamelModelElement {
	
	/**
	 * these maps contains endpoints and bean definitions stored using their ID value
	 */
	private Map<String, Node> globalDefinitions = new HashMap<String, Node>();

	/**
	 * the resource the camel file is stored in
	 */
	private IResource resource;
	
	/**
	 * Spring / Blueprint / Routes
	 */
	private CamelSchemaType schemaType;
	
	/**
	 * the xml document
	 */
	private Document document;

	/**
	 * 
	 */
	public CamelFile(IResource resource) {
		super(null, null);
		this.resource = resource;
	}

	/**
	 * @return the globalDefinitions
	 */
	public Map<String, Node> getGlobalDefinitions() {
		return this.globalDefinitions;
	}
	
	/**
	 * @param globalDefinitions the globalDefinitions to set
	 */
	public void setGlobalDefinitions(Map<String, Node> globalDefinitions) {
		this.globalDefinitions = globalDefinitions;
	}
	
	/**
	 * adds the given global definition to the context 
	 * 
	 * @param id
	 * @param def
	 * @return the id used for adding the definition or null if not added
	 */
	public String addGlobalDefinition(String id, Node def) {
		String usedId = id != null ? id : UUID.randomUUID().toString();
		if (id != null && this.globalDefinitions.containsKey(id)) return null;
		if (id == null && this.globalDefinitions.containsValue(def)) return null;
		this.globalDefinitions.put(usedId, def);
		return usedId;
	}
	
	/**
	 * removes the global definition from context 
	 * 
	 * @param id
	 */
	public void removeBeanDefinition(String id) {
		this.globalDefinitions.remove(id);
	}
	
	/**
	 * deletes all global definitions
	 */
	public void clearGlobalDefinitions() {
		this.globalDefinitions.clear();
	}
	
	/**
	 * @return the schemaType
	 */
	public CamelSchemaType getSchemaType() {
		return this.schemaType;
	}
	
	/**
	 * @param schemaType the schemaType to set
	 */
	public void setSchemaType(CamelSchemaType schemaType) {
		this.schemaType = schemaType;
	}
	
	/**
	 * @return the resource
	 */
	public IResource getResource() {
		return this.resource;
	}
	
	/**
	 * @param resource the resource to set
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}
	
	/**
	 * @return the document
	 */
	public Document getDocument() {
		return this.document;
	}
	
	/**
	 * @param document the document to set
	 */
	public void setDocument(Document document) {
		this.document = document;
	}
	
	/**
	 * checks whether this is a blueprint file or not
	 * 
	 * @return true if its a blueprint, false if no schema type detected or not blueprint
	 */
	public boolean isBlueprint() {
		return this.schemaType != null && schemaType.equals(CamelSchemaType.BLUEPRINT);
	}
	
	/**
	 * checks whether this is a blueprint file or not
	 * 
	 * @return true if its a blueprint, false if no schema type detected or not blueprint
	 */
	public boolean isSpring() {
		return this.schemaType != null && schemaType.equals(CamelSchemaType.SPRING);
	}
}

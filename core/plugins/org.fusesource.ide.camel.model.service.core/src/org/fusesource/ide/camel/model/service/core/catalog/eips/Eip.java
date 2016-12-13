/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.eips;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.service.core.catalog.ICamelCatalogElement;
import org.fusesource.ide.camel.model.service.core.catalog.IParameterContainer;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "eip")
public class Eip implements ICamelCatalogElement, IParameterContainer {
	
	private String name;
	private ArrayList<String> tags;
	private String title;
	private String description;
	private String clazz;
	private String kind;
	private String input;
	private String output;
	private ArrayList<Parameter> parameters;
	
	/**
	 * @return the parameters
	 */
	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	public ArrayList<Parameter> getParameters() {
		return this.parameters;
	}
	
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	/**
	 * returns the parameter with the given name
	 * 
	 * @param name
	 * @return the parameter or null if not found
	 */
	public Parameter getParameter(String name) {
		for (Parameter p : getParameters()) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * @return the input
	 */
	@XmlElement(name="input")
	public String getInput() {
		return this.input;
	}
	
	/**
	 * @param input the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}
	
	/**
	 * @return the output
	 */
	@XmlElement(name="output")
	public String getOutput() {
		return this.output;
	}
	
	/**
	 * @param output the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}
	
	/**
	 * @return the name
	 */
	@XmlElement(name = "name")
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the kind
	 */
	@XmlElement(name = "kind")
	public String getKind() {
		return this.kind;
	}
	
	/**
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	/**
	 * @return the clazz
	 */
	@XmlElement(name = "class")
	public String getClazz() {
		return this.clazz;
	}
	
	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * @return the description
	 */
	@XmlElement(name = "description")
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the title
	 */
	@XmlElement(name="title")
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the tags
	 */
	@XmlElementWrapper(name = "tags")
	@XmlElement(name = "tag")
	public ArrayList<String> getTags() {
		return this.tags;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	/**
	 * returns true if this element can have child elements
	 * 
	 * @return
	 */
	public boolean canHaveChildren() {
		for (Parameter p : getParameters()) {
			if (p.getType().equalsIgnoreCase("array") && p.getKind().equalsIgnoreCase(AbstractCamelModelElement.NODE_KIND_ELEMENT) && getInput().equalsIgnoreCase("true")) return true;
		}
		return false;
	}
	
	/**
	 * returns the list of allowed node type id values for child elements
	 * 
	 * @return	a list of allowed node type ids or an empty list if not a container
	 */
	public List<String> getAllowedChildrenNodeTypes() {
		ArrayList<String> allowedNodeTypes = new ArrayList<String>();
		if (canHaveChildren()) {
			for (Parameter p : getParameters()) {
				if (p.getType().equalsIgnoreCase("array") && p.getKind().equalsIgnoreCase(AbstractCamelModelElement.NODE_KIND_ELEMENT) && p.getOneOf() != null) {
					String oneOfList = p.getOneOf();
					String[] types = oneOfList.split(",");
					for (String type : types) {
						allowedNodeTypes.add(type.trim());
					}
				}
			}
		}
		return allowedNodeTypes;
	}
	
	/**
     * returns true if the EIP is allowed to be created on the Camel Context
     * 
     * @return
     */
    public boolean canBeAddedToCamelContextDirectly() {
    	return 	getName().equalsIgnoreCase(AbstractCamelModelElement.ROUTE_NODE_NAME) || 
				getName().equalsIgnoreCase("rest") || 
				getName().equalsIgnoreCase("restConfiguration"); 
    }
}

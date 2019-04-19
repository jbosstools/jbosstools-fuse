/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.eips;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fusesource.ide.camel.model.service.core.catalog.ICamelCatalogElement;
import org.fusesource.ide.camel.model.service.core.catalog.IParameterContainer;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelCatalogUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * @author lhein
 */
public class Eip implements ICamelCatalogElement, IParameterContainer {

	private static ObjectReader eipReader;
	
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_KIND = "kind";
	public static final String PROPERTY_JAVA_TYPE = "javaType";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_TITLE = "title";
	public static final String PROPERTY_LABEL = "label";
	public static final String PROPERTY_INPUT = "input";
	public static final String PROPERTY_OUTPUT = "output";

	@JsonProperty("model")
	private Map<String, String> model = new HashMap<>();
	@JsonProperty("properties")
	private Map<String, Parameter> properties = new HashMap<>();

	private List<String> tags = new ArrayList<>();
	private Map<String, Object> otherProperties = new HashMap<>();

	@JsonAnyGetter
	public Map<String, Object> any() {
		return otherProperties;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		otherProperties.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.fuse.model.IParameterContainer#getParameter(java.lang.String)
	 */
	@Override
	public Parameter getParameter(String name) {
		return properties.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.fuse.model.IParameterContainer#getParameters()
	 */
	@Override
	public List<Parameter> getParameters() {
		return new ArrayList<>(properties.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.fuse.model.ICamelCatalogElement#getName()
	 */
	@Override
	public String getName() {
		return this.model.get(PROPERTY_NAME);
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.model.put(PROPERTY_NAME, name);
	}

	/**
	 * @return the kind
	 */
	public String getKind() {
		return this.model.get(PROPERTY_KIND);
	}

	/**
	 * @param kind
	 */
	public void setKind(String kind) {
		this.model.put(PROPERTY_KIND, kind);
	}

	/**
	 * @return the input
	 */
	public String getInput() {
		String input = this.model.get(PROPERTY_INPUT);
		if (input != null && input.trim().length() > 0) {
			return input;
		}
		return Boolean.FALSE.toString();
	}

	/**
	 * @param input
	 */
	public void setInput(String input) {
		this.model.put(PROPERTY_INPUT, input);
	}

	/**
	 * @return the output
	 */
	public String getOutput() {
		String output = this.model.get(PROPERTY_OUTPUT);
		if (output != null && output.trim().length() > 0) {
			return output;
		}
		return Boolean.FALSE.toString();
	}

	/**
	 * @param output
	 */
	public void setOutput(String output) {
		this.model.put(PROPERTY_OUTPUT, output);
	}

	/**
	 * @return the clazz
	 */
	public String getClazz() {
		return this.model.get(PROPERTY_JAVA_TYPE);
	}

	/**
	 * @param clazz
	 */
	public void setClazz(String clazz) {
		this.model.put(PROPERTY_JAVA_TYPE, clazz);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.model.get(PROPERTY_DESCRIPTION);
	}

	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.model.put(PROPERTY_DESCRIPTION, description);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.model.get(PROPERTY_TITLE);
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.model.put(PROPERTY_TITLE, title);
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		if (this.tags == null || this.tags.isEmpty()) {
			this.tags = CamelCatalogUtils.initializeTags(this.model.get(PROPERTY_LABEL));
		}
		return this.tags;
	}

	/**
	 * @param tags
	 */
	public void setTags(List<String> tags) {
		String label;
		if (tags != null) {
			label = tags.stream().collect(Collectors.joining(","));
		} else {
			label = "";
		}
		this.model.put(PROPERTY_LABEL, label);
		this.tags = tags;
	}

	/**
	 * @return the model
	 */
	public Map<String, String> getModel() {
		return this.model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(Map<String, String> model) {
		this.model = model;
	}

	/**
	 * @return the properties
	 */
	public Map<String, Parameter> getProperties() {
		return this.properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<String, Parameter> properties) {
		this.properties = properties;
		PropertiesUtils.initializePropertyNames(this.properties);
	}
	
	/**
	 * checks whether there is a component data with the given key
	 * 
	 * @param key
	 *            the key to lookup
	 * @return true if existing, otherwise false
	 */
	public boolean containsCustomValueForKey(String key) {
		return this.model.containsKey(key);
	}

	/**
	 * returns the value for a custom key
	 * 
	 * @param key
	 *            the key of the value to lookup
	 * @return the value or null if not existing
	 */
	public String getCustomComponentModelValue(String key) {
		return this.model.get(key);
	}

	/**
	 * sets the component value for the given key
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setCustomComponentModelValue(String key, String value) {
		this.model.put(key, value);
	}

	/**
	 * returns true if this element can have child elements
	 * 
	 * @return
	 */
	public boolean canHaveChildren() {
		for (Parameter p : getParameters()) {
			if ("array".equalsIgnoreCase(p.getType())
					&& AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(p.getKind())
					&& "true".equalsIgnoreCase(getInput()))
				return true;
		}
		return false;
	}

	/**
	 * returns the list of allowed node type id values for child elements
	 * 
	 * @return a list of allowed node type ids or an empty list if not a
	 *         container
	 */
	public List<String> getAllowedChildrenNodeTypes() {
		ArrayList<String> allowedNodeTypes = new ArrayList<>();
		if (canHaveChildren()) {
			for (Parameter p : getParameters()) {
				if ("array".equalsIgnoreCase(p.getType())
						&& AbstractCamelModelElement.NODE_KIND_ELEMENT.equalsIgnoreCase(p.getKind())
						&& p.getOneOf() != null) {
					String[] types = p.getOneOf();
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
		return AbstractCamelModelElement.ROUTE_NODE_NAME.equalsIgnoreCase(getName())
				|| "rest".equalsIgnoreCase(getName()) || "restConfiguration".equalsIgnoreCase(getName());
	}

	/**
	 * creates the model from the given input stream
	 * 
	 * @param stream
	 *            the stream to parse
	 * @return the created model instance of null on errors
	 */
	public static Eip getJSONFactoryInstance(InputStream stream) {
		try {
			return getEipReader().readValue(stream);
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}
	
	private static ObjectReader getEipReader() {
		if(eipReader == null) {
			ObjectMapper mapper = new ObjectMapper();
			eipReader = mapper.readerFor(Eip.class);
		}
		return eipReader;
	}
}

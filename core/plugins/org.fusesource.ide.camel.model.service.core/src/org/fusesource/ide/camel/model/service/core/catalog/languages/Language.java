/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.languages;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.ICamelCatalogElement;
import org.fusesource.ide.camel.model.service.core.catalog.IParameterContainer;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;
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
public class Language implements ICamelCatalogElement, IParameterContainer {

	private static ObjectReader languageReader;

	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_KIND = "kind";
	public static final String PROPERTY_MODEL_NAME = "modelName";
	public static final String PROPERTY_TITLE = "title";
	public static final String PROPERTY_DESCRIPTION = "description";
	public static final String PROPERTY_LABEL = "label";
	public static final String PROPERTY_JAVA_TYPE = "javaType";
	public static final String PROPERTY_MODEL_JAVA_TYPE = "modelJavaType";
	public static final String PROPERTY_GROUPID = "groupId";
	public static final String PROPERTY_ARTIFACTID = "artifactId";
	public static final String PROPERTY_VERSION = "version";

	@JsonProperty("language")
	private Map<String, String> model = new HashMap<>();
	@JsonProperty("properties")
	private Map<String, Parameter> properties = new HashMap<>();

	private List<Dependency> dependencies = new ArrayList<>();
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
	 * sets the name
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
	 * @return the modelJavaType
	 */
	public String getModelJavaType() {
		return this.model.get(PROPERTY_MODEL_JAVA_TYPE);
	}

	/**
	 * @param modelJavaType
	 *            the modelJavaType to set
	 */
	public void setModelJavaType(String modelJavaType) {
		this.model.put(PROPERTY_MODEL_JAVA_TYPE, modelJavaType);
	}

	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return this.model.get(PROPERTY_MODEL_NAME);
	}

	/**
	 * @param modelName
	 *            the modelName to set
	 */
	public void setModelName(String modelName) {
		this.model.put(PROPERTY_MODEL_NAME, modelName);
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
	 * @return the dependency
	 */
	public List<Dependency> getDependencies() {
		if (this.dependencies == null || this.dependencies.isEmpty()) {
			this.dependencies = new ArrayList<>();
			CamelCatalogUtils.parseDependencies(dependencies, getModel());
		}
		return this.dependencies;
	}

	/**
	 * @param dependency
	 *            the dependency to set
	 */
	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
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
		PropertiesUtils.initializePropertyNames(properties);
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
	 * creates the model from the given input stream
	 * 
	 * @param stream
	 *            the stream to parse
	 * @return the created model instance of null on errors
	 */
	public static Language getJSONFactoryInstance(InputStream stream) {
		try {
			return getLanguageReader().readValue(stream);
		} catch (IOException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}
	
	private static ObjectReader getLanguageReader() {
		if(languageReader == null) {
			ObjectMapper mapper = new ObjectMapper();
			languageReader = mapper.readerFor(Language.class);
		}
		return languageReader;
	}
}

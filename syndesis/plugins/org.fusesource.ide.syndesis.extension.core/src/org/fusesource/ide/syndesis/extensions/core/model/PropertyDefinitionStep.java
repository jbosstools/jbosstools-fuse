/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.foundation.core.databinding.PojoModelObservable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lheinema
 */
public class PropertyDefinitionStep extends PojoModelObservable {
	
	private static final String PROPERTY_DESCRIPTION = "property.description";
	private static final String PROPERTY_NAME = "property.name";
	private static final String PROPERTY_PROPERTIES = "property.properties";
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("properties")
	private Map<String, SyndesisExtensionProperty> properties = new HashMap<>();
	
	private Map<String, Object> otherProperties = new HashMap<>();
	
	@JsonAnyGetter
	public Map<String, Object> any() {
		return otherProperties;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		otherProperties.put(name, value);
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		firePropertyChange(PROPERTY_DESCRIPTION, this.description, description);
		this.description = description;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		firePropertyChange(PROPERTY_NAME, this.name, name);
		this.name = name;
	}
	
	/**
	 * @return the properties
	 */
	public Map<String, SyndesisExtensionProperty> getProperties() {
		return this.properties;
	}
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, SyndesisExtensionProperty> properties) {
		firePropertyChange(PROPERTY_PROPERTIES, this.properties, properties);
		this.properties = properties;
	}
}

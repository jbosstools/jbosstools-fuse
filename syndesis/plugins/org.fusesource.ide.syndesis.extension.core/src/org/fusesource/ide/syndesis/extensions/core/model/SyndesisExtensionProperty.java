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
public class SyndesisExtensionProperty extends PojoModelObservable {
	
	private static final String PROPERTY_OTHER = "property.other";
	private static final String PROPERTY_KIND = "property.kind";
	private static final String PROPERTY_DISPLAYNAME = "property.displayname";
	private static final String PROPERTY_GROUP = "property.group";
	private static final String PROPERTY_LABEL = "property.label";
	private static final String PROPERTY_REQUIRED = "property.required";
	private static final String PROPERTY_TYPE = "property.type";
	private static final String PROPERTY_JAVATYPE = "property.javatype";
	private static final String PROPERTY_DEPRECATED = "property.deprecated";
	private static final String PROPERTY_SECRET = "property.secret";
	private static final String PROPERTY_COMPONENTPROPERTY = "property.componentproperty";
	private static final String PROPERTY_DESCRIPTION = "property.description";
	private static final String PROPERTY_TAGS = "property.tags";
	private static final String PROPERTY_ENUM = "property.enum";
	private static final String PROPERTY_CONNECTORVALUE = "property.connectorvalue";
	private static final String PROPERTY_DEFAULTVALUE = "property.defaultvalue";
	private static final String PROPERTY_CONFIGUREDPROPERTIES = "property.configuredproperties";
		
	@JsonProperty("kind")
	private String kind;
	
	@JsonProperty("displayName")
	private String displayName;
    
	@JsonProperty("group")
	private String group;
    
	@JsonProperty("label")
	private String label;
    
	@JsonProperty("required")
	private Boolean required;
    
	@JsonProperty("type")
	private String type;
    
	@JsonProperty("javaType")
	private String javaType;
    
	@JsonProperty("deprecated")
	private Boolean deprecated;
    
	@JsonProperty("connectorValue")
	private String connectorValue;
	
	@JsonProperty("secret")
	private Boolean secret;
    
	@JsonProperty("defaultValue")
	private String defaultValue;
	
	@JsonProperty("componentProperty")
	private Boolean componentProperty;
    
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("tags")
	private List<String> tags = new ArrayList<>();
	
	@JsonProperty("enum")
	private List<String> enums = new ArrayList<>();
	
	@JsonProperty("configuredProperties")
	private Map<String, String> configuredProperties = new HashMap<>();
	
	/**
	 * otherProperties is catching all not matched json data (see JsonAnyGetter/Setter annotation)
	 */
	private Map<String, Object> otherProperties = new HashMap<>();

	@JsonAnyGetter
	public Map<String, Object> any() {
		return otherProperties;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		firePropertyChange(PROPERTY_OTHER, this.otherProperties.get(name), value);
		otherProperties.put(name, value);
	}

	/**
	 * @return the componentProperty
	 */
	public Boolean getComponentProperty() {
		return this.componentProperty;
	}
	
	/**
	 * @param componentProperty the componentProperty to set
	 */
	public void setComponentProperty(Boolean componentProperty) {
		firePropertyChange(PROPERTY_COMPONENTPROPERTY, this.componentProperty, componentProperty);
		this.componentProperty = componentProperty;
	}
	
	/**
	 * @return the deprecated
	 */
	public Boolean getDeprecated() {
		return this.deprecated;
	}
	
	/**
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(Boolean deprecated) {
		firePropertyChange(PROPERTY_DEPRECATED, this.deprecated, deprecated);
		this.deprecated = deprecated;
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
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}
	
	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		firePropertyChange(PROPERTY_DISPLAYNAME, this.displayName, displayName);
		this.displayName = displayName;
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		return this.group;
	}
	
	/**
	 * @param group the group to set
	 */
	public void setGroup(String group) {
		firePropertyChange(PROPERTY_GROUP, this.group, group);
		this.group = group;
	}
	
	/**
	 * @return the javaType
	 */
	public String getJavaType() {
		return this.javaType;
	}
	
	/**
	 * @param javaType the javaType to set
	 */
	public void setJavaType(String javaType) {
		firePropertyChange(PROPERTY_JAVATYPE, this.javaType, javaType);
		this.javaType = javaType;
	}
	
	/**
	 * @return the kind
	 */
	public String getKind() {
		return this.kind;
	}
	
	/**
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		firePropertyChange(PROPERTY_KIND, this.kind, kind);
		this.kind = kind;
	}
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		firePropertyChange(PROPERTY_LABEL, this.label, label);
		this.label = label;
	}
	
	/**
	 * @return the required
	 */
	public Boolean getRequired() {
		return this.required;
	}
	
	/**
	 * @param required the required to set
	 */
	public void setRequired(Boolean required) {
		firePropertyChange(PROPERTY_REQUIRED, this.required, required);
		this.required = required;
	}
	
	/**
	 * @return the secret
	 */
	public Boolean getSecret() {
		return this.secret;
	}
	
	/**
	 * @param secret the secret to set
	 */
	public void setSecret(Boolean secret) {
		firePropertyChange(PROPERTY_SECRET, this.secret, secret);
		this.secret = secret;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		firePropertyChange(PROPERTY_TYPE, this.type, type);
		this.type = type;
	}
	
	/**
	 * @return the enums
	 */
	public List<String> getEnums() {
		return this.enums;
	}
	
	/**
	 * @param enums the enums to set
	 */
	public void setEnums(List<String> enums) {
		firePropertyChange(PROPERTY_ENUM, this.enums, enums);
		this.enums = enums;
	}
	
	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return this.tags;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<String> tags) {
		firePropertyChange(PROPERTY_TAGS, this.tags, tags);
		this.tags = tags;
	}
	
	/**
	 * @return the connectorValue
	 */
	public String getConnectorValue() {
		return this.connectorValue;
	}
	
	/**
	 * @param connectorValue the connectorValue to set
	 */
	public void setConnectorValue(String connectorValue) {
		firePropertyChange(PROPERTY_CONNECTORVALUE, this.connectorValue, connectorValue);
		this.connectorValue = connectorValue;
	}
	
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		firePropertyChange(PROPERTY_DEFAULTVALUE, this.defaultValue, defaultValue);
		this.defaultValue = defaultValue;
	}
	
	/**
	 * @return the configuredProperties
	 */
	public Map<String, String> getConfiguredProperties() {
		return this.configuredProperties;
	}
	
	/**
	 * @param configuredProperties the configuredProperties to set
	 */
	public void setConfiguredProperties(Map<String, String> configuredProperties) {
		firePropertyChange(PROPERTY_CONFIGUREDPROPERTIES, this.configuredProperties, configuredProperties);
		this.configuredProperties = configuredProperties;
	}
}

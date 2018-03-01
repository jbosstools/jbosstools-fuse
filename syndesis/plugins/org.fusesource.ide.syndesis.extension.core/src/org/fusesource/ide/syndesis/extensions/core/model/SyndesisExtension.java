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
package org.fusesource.ide.syndesis.extensions.core.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.foundation.core.databinding.PojoModelObservable;
import org.fusesource.ide.syndesis.extensions.core.internal.SyndesisExtensionsCoreActivator;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lheinema
 */
@JsonPropertyOrder({"schemaVersion", "name", "description", "icon", "extensionId", "version", "tags", "actions", "dependencies"})
public class SyndesisExtension extends PojoModelObservable {
	
	private static final String PROPERTY_OTHER = "property.other";
	private static final String PROPERTY_SYNDESISVERSION = "property.syndesisversion";
	private static final String PROPERTY_ID = "property.id";
	private static final String PROPERTY_EXTENSIONID = "property.extensionid";
	private static final String PROPERTY_VERSION = "property.version";
	private static final String PROPERTY_NAME = "property.name";
	private static final String PROPERTY_STATUS = "property.status";
	private static final String PROPERTY_DESCRIPTION = "property.description";
	private static final String PROPERTY_ACTIONS = "property.actions";
	private static final String PROPERTY_DEPENDENCIES = "property.dependencies";
	private static final String PROPERTY_ICON = "property.icon";
	private static final String PROPERTY_SCHEMAVERSION = "property.schemaversion";
	private static final String PROPERTY_EXTENSIONPROPERTIES = "property.extensionproperties";
	private static final String PROPERTY_EXTENSIONTYPE = "property.extensiontype";
	private static final String PROPERTY_METADATA = "property.metadata";
	private static final String PROPERTY_TAGS = "property.tags";
	private static final String PROPERTY_CONFIGUREDPROPERTIES = "property.configuredproperties";
	private static final String PROPERTY_CONNECTORCUSTOMIZER = "property.connectorcustomizer";
	
	enum Status {
		DRAFT ("Draft"),
		INSTALLED ("Installed"),
		DELETED ("Deleted");
		
		private String value;
		
		Status(String value) {
			this.value = value;
        }

		@Override
        public String toString() {
        	return this.value;
        }
	}

	enum Type {
		STEPS ("Steps"),
		CONNECTORS ("Connectors");
		
		private String value;
		
		Type(String value) {
			this.value = value;
		}
    	
		@Override
		public String toString() {
			return this.value;
		}
	}
	
	@JsonIgnore
	private String syndesisVersion;

	@JsonProperty("id")
	private String id;				
	
	@JsonProperty("extensionId")
	private String extensionId;		
	
	@JsonProperty("version")
	private String version;			
	
	@JsonProperty("name")
	private String name;			
	
	@JsonProperty("status")
	private String status;			

	@JsonProperty("extensionType")
	private String extensionType;
	
	@JsonProperty("schemaVersion")
	private String schemaVersion;
	
	@JsonProperty("description")
	private String description;		
	
	@JsonProperty("tags")
	private List<String> tags = new ArrayList<>();
	
	@JsonProperty("icon")
	private String icon;
	
	@JsonProperty("connectorCustomizers")
	private List<String> connectorCustomizers = new ArrayList<>();
	
	@JsonProperty("actions")
	private List<SyndesisAction> actions = new ArrayList<>();
	
	@JsonProperty("dependencies")
	private List<ExtensionDependency> dependencies = new ArrayList<>();
	
	@JsonProperty("properties")
	private Map<String, SyndesisExtensionProperty> extensionProperties = new HashMap<>();
	
	@JsonProperty("configuredProperties")
	private Map<String, String> configuredProperties = new HashMap<>();
	
	@JsonProperty("metadata")
	private Map<String, String> metadata = new HashMap<>();
	
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
	 * @return the actions
	 */
	public List<SyndesisAction> getActions() {
		return this.actions;
	}
	
	/**
	 * @param actions the actions to set
	 */
	public void setActions(List<SyndesisAction> actions) {
		firePropertyChange(PROPERTY_ACTIONS, this.actions, actions);
		this.actions = actions;
	}
	
	/**
	 * @return the dependencies
	 */
	public List<ExtensionDependency> getDependencies() {
		return this.dependencies;
	}
	
	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(List<ExtensionDependency> dependencies) {
		firePropertyChange(PROPERTY_DEPENDENCIES, this.dependencies, dependencies);
		this.dependencies = dependencies;
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
	 * @return the extensionId
	 */
	public String getExtensionId() {
		return this.extensionId;
	}
	
	/**
	 * @param extensionId the extensionId to set
	 */
	public void setExtensionId(String extensionId) {
		firePropertyChange(PROPERTY_EXTENSIONID, this.extensionId, extensionId);
		this.extensionId = extensionId;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		firePropertyChange(PROPERTY_ID, this.id, id);
		this.id = id;
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
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		firePropertyChange(PROPERTY_STATUS, this.status, status);
		this.status = status;
	}
	
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return this.icon;
	}
	
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		firePropertyChange(PROPERTY_ICON, this.icon, icon);
		this.icon = icon;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		firePropertyChange(PROPERTY_VERSION, this.version, version);
		this.version = version;
	}
	
	/**
	 * @return the schemaVersion
	 */
	public String getSchemaVersion() {
		return this.schemaVersion;
	}
	
	/**
	 * @param schemaVersion the schemaVersion to set
	 */
	public void setSchemaVersion(String schemaVersion) {
		firePropertyChange(PROPERTY_SCHEMAVERSION, this.schemaVersion, schemaVersion);
		this.schemaVersion = schemaVersion;
	}
	
	/**
	 * @return the syndesisVersion
	 */
	public String getSyndesisVersion() {
		return this.syndesisVersion;
	}
	
	/**
	 * @param syndesisVersion the syndesisVersion to set
	 */
	public void setSyndesisVersion(String syndesisVersion) {
		firePropertyChange(PROPERTY_SYNDESISVERSION, this.syndesisVersion, syndesisVersion);
		this.syndesisVersion = syndesisVersion;
	}
	
	/**
	 * @return the extensionProperties
	 */
	public Map<String, SyndesisExtensionProperty> getExtensionProperties() {
		return this.extensionProperties;
	}
	
	/**
	 * @param extensionProperties the extensionProperties to set
	 */
	public void setExtensionProperties(Map<String, SyndesisExtensionProperty> extensionProperties) {
		firePropertyChange(PROPERTY_EXTENSIONPROPERTIES, this.extensionProperties, extensionProperties);
		this.extensionProperties = extensionProperties;
	}
	
	/**
	 * @return the extensionType
	 */
	public String getExtensionType() {
		return this.extensionType;
	}
	
	/**
	 * @param extensionType the extensionType to set
	 */
	public void setExtensionType(String extensionType) {
		firePropertyChange(PROPERTY_EXTENSIONTYPE, this.extensionType, extensionType);
		this.extensionType = extensionType;
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
	
	/**
	 * @return the metadata
	 */
	public Map<String, String> getMetadata() {
		return this.metadata;
	}
	
	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Map<String, String> metadata) {
		firePropertyChange(PROPERTY_METADATA, this.metadata, metadata);
		this.metadata = metadata;
	}
	
	/**
	 * @return the connectorCustomizers
	 */
	public List<String> getConnectorCustomizers() {
		return this.connectorCustomizers;
	}
	
	/**
	 * @param connectorCustomizers the connectorCustomizers to set
	 */
	public void setConnectorCustomizers(List<String> connectorCustomizers) {
		firePropertyChange(PROPERTY_CONNECTORCUSTOMIZER, this.connectorCustomizers, connectorCustomizers);
		this.connectorCustomizers = connectorCustomizers;
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
	 * creates the model from the given input stream
	 * 
	 * @param stream
	 *            the stream to parse
	 * @return the created model instance of null on errors
	 */
	public static SyndesisExtension getJSONFactoryInstance(InputStream stream) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(stream, SyndesisExtension.class);
		} catch (IOException ex) {
			SyndesisExtensionsCoreActivator.pluginLog().logError(ex);
		}
		return null;
	}
}

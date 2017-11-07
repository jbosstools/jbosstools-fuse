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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.ide.syndesis.extensions.core.internal.SyndesisExtensionsCoreActivator;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lheinema
 */
public class SyndesisExtension {
	
	private transient String springBootVersion;
	private transient String camelVersion;
	private transient String syndesisVersion;

	@JsonProperty("id")
	private String id;				// assigned-by-syndesis-server (must not be hardcoded in packaged extensions)
	
	@JsonProperty("extensionId")
	private String extensionId;		// ${groupId}:${artifactId}
	
	@JsonProperty("version")
	private String version;			// ${version}
	
	@JsonProperty("name")
	private String name;			// Extension Name
	
	@JsonProperty("status")
	private String status;			// Draft|Installed|Deleted (must not be hardcoded in packaged extensions)
	
	@JsonProperty("description")
	private String description;		// Extension Description
	
	@JsonProperty("icon")
	private String icon;			// fa-puzzle-piece
	
	@JsonProperty("tags")
	private List<String> tags;
	
	@JsonProperty("actions")
	private List<SyndesisAction> actions;
	
	@JsonProperty("dependencies")
	private List<String> dependencies;	// [mvn:g/a/v, ..., ...]
	
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
	 * @return the actions
	 */
	public List<SyndesisAction> getActions() {
		return this.actions;
	}
	
	/**
	 * @param actions the actions to set
	 */
	public void setActions(List<SyndesisAction> actions) {
		this.actions = actions;
	}
	
	/**
	 * @return the dependencies
	 */
	public List<String> getDependencies() {
		return this.dependencies;
	}
	
	/**
	 * @param dependencies the dependencies to set
	 */
	public void setDependencies(List<String> dependencies) {
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
		this.extensionId = extensionId;
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
		this.icon = icon;
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
		this.status = status;
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
		this.tags = tags;
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
		this.version = version;
	}
	
	/**
	 * @return the camelVersion
	 */
	public String getCamelVersion() {
		return this.camelVersion;
	}
	/**
	 * @param camelVersion the camelVersion to set
	 */
	public void setCamelVersion(String camelVersion) {
		this.camelVersion = camelVersion;
	}
	/**
	 * @return the springBootVersion
	 */
	public String getSpringBootVersion() {
		return this.springBootVersion;
	}
	
	/**
	 * @param springBootVersion the springBootVersion to set
	 */
	public void setSpringBootVersion(String springBootVersion) {
		this.springBootVersion = springBootVersion;
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
		this.syndesisVersion = syndesisVersion;
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

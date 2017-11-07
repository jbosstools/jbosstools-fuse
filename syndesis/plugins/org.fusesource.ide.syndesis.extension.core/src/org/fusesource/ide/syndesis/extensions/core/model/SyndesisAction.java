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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lheinema
 */
public class SyndesisAction {
	@JsonProperty("id")
	private String id;			// ${actionId}
	
	@JsonProperty("name")
	private String name;		// Action Name
	
	@JsonProperty("actionType")
	private String actionType;	// extension
	
	@JsonProperty("description")
	private String description; // Action Description
	
	@JsonProperty("tags")
	private List<String> tags = new ArrayList<>();
	
	@JsonProperty("descriptor")
	private SyndesisActionDescriptor descriptor;
	
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
	 * @return the actionType
	 */
	public String getActionType() {
		return this.actionType;
	}
	
	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
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
	 * @return the descriptor
	 */
	public SyndesisActionDescriptor getDescriptor() {
		return this.descriptor;
	}
	
	/**
	 * @param descriptor the descriptor to set
	 */
	public void setDescriptor(SyndesisActionDescriptor descriptor) {
		this.descriptor = descriptor;
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
	 * @return the otherProperties
	 */
	public Map<String, Object> getOtherProperties() {
		return this.otherProperties;
	}
	
	/**
	 * @param otherProperties the otherProperties to set
	 */
	public void setOtherProperties(Map<String, Object> otherProperties) {
		this.otherProperties = otherProperties;
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
}

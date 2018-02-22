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

import org.fusesource.ide.foundation.core.databinding.PojoModelObservable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author lheinema
 */
@JsonPropertyOrder({ "id", "name", "description", "descriptor", "tags", "actionType" })
public class SyndesisAction extends PojoModelObservable {
	
	private static final String PROPERTY_OTHER = "property.other";
	private static final String PROPERTY_ID = "property.id";
	private static final String PROPERTY_NAME = "property.name";
	private static final String PROPERTY_ACTIONTYPE = "property.actiontype";
	private static final String PROPERTY_DESCRIPTION = "property.description";
	private static final String PROPERTY_DESCRIPTOR = "property.descriptor";
	private static final String PROPERTY_TAGS = "property.tags";
	private static final String PROPERTY_PATTERN = "property.pattern";
	
	enum ActionType {

		CONNECTOR ("connector"),
		STEP ("step");
		
		private String type;
		
		ActionType (String type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			return this.type;
		}
	}
	
	enum PatternType {
		FROM ("From"),
		TO ("To");
		
		private String type;
		
		private PatternType(String type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			return this.type;
		}
	}
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("actionType")
	private String actionType;
	
	@JsonProperty("tags")
	private List<String> tags = new ArrayList<>();
	
	@JsonProperty("pattern")
	private String pattern;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("descriptor")
	private SyndesisActionDescriptor descriptor;
	
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
	 * @return the actionType
	 */
	public String getActionType() {
		return this.actionType;
	}
	
	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(String actionType) {
		firePropertyChange(PROPERTY_ACTIONTYPE, this.actionType, actionType);
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
		firePropertyChange(PROPERTY_DESCRIPTION, this.description, description);
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
		firePropertyChange(PROPERTY_DESCRIPTOR, this.descriptor, descriptor);
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
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
	}
	
	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		firePropertyChange(PROPERTY_PATTERN, this.pattern, pattern);
		this.pattern = pattern;
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
}

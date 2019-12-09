/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeId;

/**
 * @author lhein
 */
public class Parameter {
	@JsonTypeId
	private String name;
	@JsonProperty
	private String type;
	@JsonProperty
	private String javaType;
	@JsonProperty
	private String kind;
	@JsonProperty
	private String originalFieldName;
	@JsonProperty
	private String deprecated;
	@JsonProperty
	private String description;
	@JsonProperty
	private String required;
	@JsonProperty
	private String defaultValue;
	@JsonProperty("enum")
	private String[] choice;
	@JsonProperty
	private String label;
	@JsonProperty
	private String[] oneOf;
	@JsonProperty
	private String group;

	private Map<String, Object> otherProperties = new HashMap<>();

	@JsonAnyGetter
	public Map<String, Object> any() {
		return otherProperties;
	}

	@JsonAnySetter
	public void set(String name, Object value) {
		otherProperties.put(name, value);
	}

	public String getOtherParameterValue(String paramName) {
		return otherProperties.get(paramName).toString();
	}
	
	public void setOtherParameterValue(String name, String value) {
		otherProperties.put(name, value);
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		return this.group;
	}

	/**
	 * @return the oneOf
	 */
	public String[] getOneOf() {
		return this.oneOf;
	}

	/**
	 * @return the choice
	 */
	public String[] getChoice() {
		return this.choice;
	}

	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * @return the deprecated
	 */
	public String getDeprecated() {
		return this.deprecated;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the javaType
	 */
	public String getJavaType() {
		return this.javaType;
	}

	/**
	 * @return the kind
	 */
	public String getKind() {
		return this.kind;
	}

	/**
	 * @return the original field name
	 */
	public String getOriginalFieldName() {
		return this.originalFieldName;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the required
	 */
	public String getRequired() {
		return this.required;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @param oneOf
	 *            the oneOf to set
	 */
	public void setOneOf(String[] oneOf) {
		this.oneOf = oneOf;
	}

	/**
	 * @param choice
	 *            the choice to set
	 */
	public void setChoice(String[] choice) {
		this.choice = choice;
	}

	/**
	 * @param defaultValue
	 *            the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @param deprecated
	 *            the deprecated to set
	 */
	public void setDeprecated(String deprecated) {
		this.deprecated = deprecated;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param javaType
	 *            the javaType to set
	 */
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	/**
	 * @param kind
	 *            the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return the original field name
	 */
	public void setOriginalFieldName(String original) {
		this.originalFieldName = original;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param required
	 *            the required to set
	 */
	public void setRequired(String required) {
		this.required = required;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}

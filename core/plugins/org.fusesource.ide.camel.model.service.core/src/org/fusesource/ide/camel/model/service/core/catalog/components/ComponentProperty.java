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
package org.fusesource.ide.camel.model.service.core.catalog.components;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lhein
 */
public class ComponentProperty {

	@JsonProperty
	private String name;
	@JsonProperty
	private String type;
	@JsonProperty
	private String javaType;
	@JsonProperty
	private String kind;
	@JsonProperty
	private String deprecated;
	@JsonProperty
	private String description;
	@JsonProperty
	private String defaultValue;

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
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
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
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}

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

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.foundation.core.databinding.PojoModelObservable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author lheinema
 */
@JsonPropertyOrder({ "type", "id" })
public class ExtensionDependency extends PojoModelObservable {
	
	private static final String PROPERTY_TYPE = "property.type";
	private static final String PROPERTY_ID = "property.id";
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("id")
	private String id;
	
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
	
	
}

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

import java.util.HashMap;
import java.util.Map;

import org.fusesource.ide.foundation.core.databinding.PojoModelObservable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lheinema
 */
public class ActionDataShape extends PojoModelObservable {
	
	private static final String PROPERTY_OTHER = "property.other";
	private static final String PROPERTY_NAME = "property.name";
	private static final String PROPERTY_DESCRIPTION = "property.description";
	private static final String PROPERTY_KIND = "property.kind";
	private static final String PROPERTY_TYPE = "property.type";
	private static final String PROPERTY_SPECIFICATION = "property.specification";
	
	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("kind")
	private String kind;

	@JsonProperty("type")
	private String type;

	@JsonProperty("specification")
	private String specification;
	
	private Map<String, Object> otherProperties = new HashMap<>();

	enum DataShapeKind {	    
		ANY ("any"),
		JAVA ("java"),
		JSON_SCHEMA ("json-schema"),
		NONE ("none");
		
		private String value;
		
		private DataShapeKind(String value) {
			this.value = value;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 */
		@Override
		public String toString() {
			return this.value;
		}
	}
	
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
	 * @return the specification
	 */
	public String getSpecification() {
		return this.specification;
	}
	
	/**
	 * @param specification the specification to set
	 */
	public void setSpecification(String specification) {
		firePropertyChange(PROPERTY_SPECIFICATION, this.specification, specification);
		this.specification = specification;
	}
}

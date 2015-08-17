/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.catalog;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "uriParameter")
public class Parameter {
	private String name;
	private String type;
	private String javaType;
	private String kind;
	private String originalFieldName;
	private String deprecated;
	private String description;
	private String required;
	private String defaultValue;
	private String choice;
	private String label;
	private String oneOf;
	
	/**
	 * @return the oneOf
	 */
	@XmlAttribute(name = "oneOf")
	public String getOneOf() {
		return this.oneOf;
	}
	
	/**
	 * @return the choice
	 */
	@XmlAttribute(name = "choice")
	public String getChoice() {
		return this.choice;
	}
	
	/**
	 * @return the defaultValue
	 */
	@XmlAttribute(name = "defaultValue")
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	/**
	 * @return the deprecated
	 */
	@XmlAttribute(name = "deprecated")
	public String getDeprecated() {
		return this.deprecated;
	}
	
	/**
	 * @return the description
	 */
	@XmlAttribute(name = "description")
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the javaType
	 */
	@XmlAttribute(name = "javaType")
	public String getJavaType() {
		return this.javaType;
	}
	
	/**
	 * @return the kind
	 */
	@XmlAttribute(name = "kind")
	public String getKind() {
		return this.kind;
	}
	
	/**
	 * @return the original field name
	 */
	@XmlAttribute(name = "originalFieldName")
	public String getOriginalFieldName() {
		return this.originalFieldName;
	}

	/**
	 * @return the label
	 */
	@XmlAttribute(name = "label")
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * @return the name
	 */
	@XmlAttribute(name = "name")
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the required
	 */
	@XmlAttribute(name = "required")
	public String getRequired() {
		return this.required;
	}
	
	/**
	 * @return the type
	 */
	@XmlAttribute(name = "type")
	public String getType() {
		return this.type;
	}
	
	/**
	 * @param oneOf the oneOf to set
	 */
	public void setOneOf(String oneOf) {
		this.oneOf = oneOf;
	}
	
	/**
	 * @param choice the choice to set
	 */
	public void setChoice(String choice) {
		this.choice = choice;
	}
	
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/**
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(String deprecated) {
		this.deprecated = deprecated;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @param javaType the javaType to set
	 */
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	
	/**
	 * @param kind the kind to set
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
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param required the required to set
	 */
	public void setRequired(String required) {
		this.required = required;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}

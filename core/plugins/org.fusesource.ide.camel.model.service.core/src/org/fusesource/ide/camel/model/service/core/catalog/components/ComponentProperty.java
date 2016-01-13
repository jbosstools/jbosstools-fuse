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
package org.fusesource.ide.camel.model.service.core.catalog.components;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "componentProperty")
public class ComponentProperty {

	private String name;
	private String type;
	private String javaType;
	private String kind;
	private String deprecated;
	private String description;
	private String defaultValue;
	
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
	 * @return the name
	 */
	@XmlAttribute(name = "name")
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the type
	 */
	@XmlAttribute(name = "type")
	public String getType() {
		return this.type;
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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}

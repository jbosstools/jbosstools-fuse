/******************************************************************************* 
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.catalog.generator.model.eip;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lhein
 *
 */
public class EIP {
	private String kind;
	private String name;
    private String title;
    private String description;
    private String javaType;
    private String label;
    private String input;
    private String output;
	@JsonProperty(required=false)
	private String deprecated;

	public String getDeprecated() {
		return deprecated;
	}

	public void setDeprecated(String deprecated) {
		this.deprecated = deprecated;
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
		this.kind = kind;
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
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
		this.description = this.description.replace("\"", "&quot;");
		this.description = this.description.replace("\r", "\\r");
		this.description = this.description.replace("\n", "\\n");
	}
	/**
	 * @return the javaType
	 */
	public String getJavaType() {
		return this.javaType;
	}
	/**
	 * @param javaType the javaType to set
	 */
	public void setJavaType(String javaType) {
		this.javaType = javaType;
		this.javaType = this.javaType.replaceAll("<", "&lt;");
		this.javaType = this.javaType.replaceAll(">", "&gt;");
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the input
	 */
	public String getInput() {
		return this.input;
	}
	/**
	 * @param input the input to set
	 */
	public void setInput(String input) {
		this.input = input;
	}
	/**
	 * @return the output
	 */
	public String getOutput() {
		return this.output;
	}
	/**
	 * @param output the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}
}

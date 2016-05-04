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

package org.fusesource.ide.catalog.generator.model.language;

/**
 * @author lhein
 *
 */
public class Language {
	private String title;
	private String name;
    private String kind;
    private String modelName;
    private String description;
    private String label;
    private String javaType;
    private String modelJavaType;
    private String groupId;
    private String artifactId;
    private String version;
    
    /**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}
    
    /**
	 * @return the artifactId
	 */
	public String getArtifactId() {
		return this.artifactId;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return this.groupId;
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
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}
	
	/**
	 * @return the modelJavaType
	 */
	public String getModelJavaType() {
		return this.modelJavaType;
	}
	
	/**
	 * @return the modelName
	 */
	public String getModelName() {
		return this.modelName;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @param artifactId the artifactId to set
	 */
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
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
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @param modelJavaType the modelJavaType to set
	 */
	public void setModelJavaType(String modelJavaType) {
		this.modelJavaType = modelJavaType;
	}
	
	/**
	 * @param modelName the modelName to set
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
}

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

package org.fusesource.ide.catalog.generator.model.component;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author lhein
 *
 */
public class Component {
	private String id;
	private String kind;
	private String scheme;
	private String syntax;
	private String title;
	private String description;
	private String label;  // tags
	private String javaType; // class
	private String groupId;
	private String artifactId;
	private String version;
	private String producerOnly;
	private String consumerOnly;
	private String extendsScheme;
	@JsonProperty(required=false)
	private String lenientProperties;
	@JsonProperty(required=false)
	private String alternativeSyntax;
	@JsonProperty(required=false)
	private String alternativeSchemes;
	@JsonProperty(required=false)
	private String deprecated;
	private String async;
	
	/**
	 * @return the alternativeSyntax
	 */
	public String getAlternativeSyntax() {
		return this.alternativeSyntax;
	}
	
	/**
	 * @param alternativeSyntax the alternativeSyntax to set
	 */
	public void setAlternativeSyntax(String alternativeSyntax) {
		this.alternativeSyntax = alternativeSyntax;
	}
	
	/**
	 * @return the deprecated
	 */
	public String getDeprecated() {
		return this.deprecated;
	}
	
	/**
	 * @param deprecated the deprecated to set
	 */
	public void setDeprecated(String deprecated) {
		this.deprecated = deprecated;
	}
	
	/**
	 * @return the lenientProperties
	 */
	public String getLenientProperties() {
		return this.lenientProperties;
	}
	
	/**
	 * @param lenientProperties the lenientProperties to set
	 */
	public void setLenientProperties(String lenientProperties) {
		this.lenientProperties = lenientProperties;
	}
	
	/**
	 * @return the extendsScheme
	 */
	public String getExtendsScheme() {
		return this.extendsScheme;
	}
	
	/**
	 * @param extendsScheme the extendsScheme to set
	 */
	public void setExtendsScheme(String extendsScheme) {
		this.extendsScheme = extendsScheme;
	}
	
	/**
	 * @return the consumerOnly
	 */
	public String getConsumerOnly() {
		return this.consumerOnly;
	}
	
	/**
	 * @param consumerOnly the consumerOnly to set
	 */
	public void setConsumerOnly(String consumerOnly) {
		this.consumerOnly = consumerOnly;
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
	 * @return the producerOnly
	 */
	public String getProducerOnly() {
		return this.producerOnly;
	}
	
	/**
	 * @param producerOnly the producerOnly to set
	 */
	public void setProducerOnly(String producerOnly) {
		this.producerOnly = producerOnly;
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
	 * @return the scheme
	 */
	public String getScheme() {
		return this.scheme;
	}
	
	/**
	 * @return the syntax
	 */
	public String getSyntax() {
		return this.syntax;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return this.version;
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
	 * @param scheme the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	
	/**
	 * @param syntax the syntax to set
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	public String getAsync() {
		return async;
	}

	public void setAsync(String async) {
		this.async = async;
	}
}

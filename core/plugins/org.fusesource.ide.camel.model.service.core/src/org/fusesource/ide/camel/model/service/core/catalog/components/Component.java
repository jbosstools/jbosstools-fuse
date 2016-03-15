/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.catalog.components;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.fusesource.ide.camel.model.service.core.catalog.Dependency;
import org.fusesource.ide.camel.model.service.core.catalog.ICamelCatalogElement;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author lhein
 */
@XmlRootElement(name = "component")
public class Component implements ICamelCatalogElement {
	
	private String id;
	private String clazz;
	private String title;
	private String description;
	private String syntax;
	private String kind;
	private String consumerOnly;
	private String producerOnly;
	private String scheme;
	private ArrayList<Dependency> dependencies;
	private ArrayList<String> tags;
	private ArrayList<ComponentProperty> componentProperties;
	private ArrayList<Parameter> uriParameters;
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.camel.model.service.core.catalog.ICamelCatalogElement#getName()
	 */
	@Override
	public String getName() {
		return getId();
	}
	
	/**
	 * @return the componentProperties
	 */
	@XmlElementWrapper(name = "componentProperties")
	@XmlElement(name = "componentProperty")
	public ArrayList<ComponentProperty> getComponentProperties() {
		return this.componentProperties;
	}
	
	/**
	 * @param componentProperties the componentProperties to set
	 */
	public void setComponentProperties(
			ArrayList<ComponentProperty> componentProperties) {
		this.componentProperties = componentProperties;
	}
	
	/**
	 * @return the uriParameters
	 */
	@XmlElementWrapper(name = "uriParameters")
	@XmlElement(name = "uriParameter")
	public ArrayList<Parameter> getUriParameters() {
		return this.uriParameters;
	}
	
	/**
	 * @param uriParameters the uriParameters to set
	 */
	public void setUriParameters(ArrayList<Parameter> uriParameters) {
		this.uriParameters = uriParameters;
	}
	
	/**
	 * @return the consumerOnly
	 */
	@XmlElement(name = "consumerOnly")
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
	 * @return the producerOnly
	 */
	@XmlElement(name = "producerOnly")
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
	 * @return the id
	 */
	@XmlElement(name="id")
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
	 * @return the scheme
	 */
	@XmlElement(name="scheme")
	public String getScheme() {
		return this.scheme;
	}
	
	/**
	 * @param scheme the scheme to set
	 */
	public void setScheme(String scheme) {
		this.scheme = scheme;
	}
	
	/**
	 * @return the kind
	 */
	@XmlElement(name = "kind")
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
	 * @return the clazz
	 */
	@XmlElement(name = "class")
	public String getClazz() {
		return this.clazz;
	}
	
	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * @return the syntax
	 */
	@XmlElement(name = "syntax")
	public String getSyntax() {
		return this.syntax;
	}
	
	/**
	 * @param syntax the syntax to set
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}
	
	/**
	 * @return the description
	 */
	@XmlElement(name = "description")
	public String getDescription() {
		return this.description;
	}

	/**
	 * @return the title
	 */
	@XmlElement(name="title")
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
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the tags
	 */
	@XmlElementWrapper(name = "tags")
	@XmlElement(name = "tag")
	public ArrayList<String> getTags() {
		return this.tags;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}
	
	/**
	 * @return the dependency
	 */
	@XmlElementWrapper(name = "dependencies")
	@XmlElement(name = "dependency")
	public ArrayList<Dependency> getDependencies() {
		return this.dependencies;
	}
	
	/**
	 * @param dependency the dependency to set
	 */
	public void setDependencies(ArrayList<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	/**
	 * checks if the component can handle the given scheme
	 * 
	 * @param scheme
	 * @return
	 */
	public boolean supportsScheme(String scheme) {
	    return getScheme().equals(scheme);
	}
	
	/**
	 * returns the label of the component if component itself doesn't provide
	 * a title
	 * 
	 * @return
	 */
	public String getSchemeTitle() {
		return getScheme();
	}
	
	/**
	 * duplicates the component
	 * 
	 * @return	a copy of the original component
	 */
	public Component duplicateFor(final String scheme, final String clazz) {
		Component dup = new Component() {
			/* (non-Javadoc)
			 * @see org.fusesource.ide.camel.model.service.core.catalog.components.Component#supportsScheme(java.lang.String)
			 */
			@Override
			public boolean supportsScheme(String testScheme) {
				return scheme.equals(testScheme);
			}
		};
		
		dup.setScheme(scheme);
		dup.setId(scheme);
		dup.setClazz(clazz);
		dup.setComponentProperties(getComponentProperties());
		dup.setConsumerOnly(getConsumerOnly());
		dup.setDescription(getDescription());
		dup.setKind(getKind());
		dup.setProducerOnly(getProducerOnly());
		dup.setSyntax(getSyntax().replaceFirst(String.format("%s:", getScheme()), String.format("%s:", scheme)));
		dup.setTags(getTags());
		dup.setTitle(getTitle());
		dup.setUriParameters(getUriParameters());
		
		return dup;
	}

	public String getDisplayTitle() {
		return Strings.isBlank(getTitle()) ? Strings.humanize(getSchemeTitle()) : getTitle();
	}
}

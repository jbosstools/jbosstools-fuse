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
package org.fusesource.ide.camel.model.connectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author lhein
 */
@XmlRootElement(name = "component")
public class Component {
	
	private String clazz;
	private String description;
	private String syntax;
	private String kind;
	private String consumerOnly;
	private String producerOnly;
	private ArrayList<ComponentScheme> schemes;
	private ArrayList<ComponentDependency> dependencies;
	private ArrayList<String> tags;
	private ArrayList<ComponentProperty> componentProperties;
	private ArrayList<UriParameter> uriParameters;
	
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
	public ArrayList<UriParameter> getUriParameters() {
		return this.uriParameters;
	}
	
	/**
	 * @param uriParameters the uriParameters to set
	 */
	public void setUriParameters(ArrayList<UriParameter> uriParameters) {
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
	 * returns the label of the component
	 * 
	 * @return
	 */
	public String getTitle() {
		Collections.sort(getSchemes(), new Comparator<ComponentScheme>() {
			/* (non-Javadoc)
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(ComponentScheme o1, ComponentScheme o2) {
				return o1.getScheme().compareTo(o2.getScheme());
			}
		});
		return getSchemes().get(0).getScheme();
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
	 * @return the schemes
	 */
	@XmlElementWrapper(name = "schemes")
	@XmlElement(name = "scheme")
	public ArrayList<ComponentScheme> getSchemes() {
	    if (this.schemes != null) {
    		Collections.sort(this.schemes, new Comparator<ComponentScheme>() {
    		    /* (non-Javadoc)
    		     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    		     */
    		    @Override
    		    public int compare(ComponentScheme o1, ComponentScheme o2) {
    		        return o1.getScheme().compareTo(o2.getScheme());
    		    }
    		});
	    }
	    return this.schemes;
	}
	
	/**
	 * @param schemes the schemes to set
	 */
	public void setSchemes(ArrayList<ComponentScheme> schemes) {
		this.schemes = schemes;
	}
	
	/**
	 * @return the dependency
	 */
	@XmlElementWrapper(name = "dependencies")
	@XmlElement(name = "dependency")
	public ArrayList<ComponentDependency> getDependencies() {
		return this.dependencies;
	}
	
	/**
	 * @param dependency the dependency to set
	 */
	public void setDependencies(ArrayList<ComponentDependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	/**
	 * checks if the component can handle the given scheme
	 * 
	 * @param scheme
	 * @return
	 */
	public boolean supportsScheme(String scheme) {
	    for (ComponentScheme p : schemes) {
	        if (p.getScheme().equalsIgnoreCase(scheme)) return true;
	    }
	    return false;
	}
}

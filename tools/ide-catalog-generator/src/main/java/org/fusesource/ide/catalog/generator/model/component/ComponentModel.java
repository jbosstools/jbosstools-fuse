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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lhein
 *
 */
public class ComponentModel {
	private Component component;
	private HashMap<String, HashMap> componentProperties;
	private HashMap<String, HashMap> properties;
	private ArrayList<UriParam> uriParams = new ArrayList<UriParam>();
	private ArrayList<ComponentParam> componentParams = new ArrayList<ComponentParam>();
	
	/**
	 * @return the component
	 */
	public Component getComponent() {
		return this.component;
	}
	
	/**
	 * @param component the component to set
	 */
	public void setComponent(Component component) {
		this.component = component;
	}
	
	/**
	 * @return the componentProperties
	 */
	public HashMap<String, HashMap> getComponentProperties() {
		return this.componentProperties;
	}
	
	/**
	 * @param componentProperties the componentProperties to set
	 */
	public void setComponentProperties(HashMap<String, HashMap> componentProperties) {
		this.componentProperties = componentProperties;
		generateComponentParamsModel();
	}
	
	/**
	 * @return the properties
	 */
	public HashMap<String, HashMap> getProperties() {
		return this.properties;
	}
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(HashMap<String, HashMap> properties) {
		this.properties = properties;
		generateUriParamsModel();
	}
	
	/**
	 * @return the uriParams
	 */
	public ArrayList<UriParam> getUriParams() {
		return this.uriParams;
	}
	
	/**
	 * @return the componentParams
	 */
	public ArrayList<ComponentParam> getComponentParams() {
		return this.componentParams;
	}
	
	/**
	 * used to generate the list of uri params for this component
	 */
	private void generateUriParamsModel() {
		uriParams.clear();
		ObjectMapper mapper = new ObjectMapper();
		Iterator<String> it = properties.keySet().iterator();
		while (it.hasNext()) {
			String paramName = it.next();
			UriParam p = mapper.convertValue(properties.get(paramName), UriParam.class);
			p.setName(paramName);
			uriParams.add(p);
		}
	}
	
	/**
	 * used to generate the list of component params for this component
	 */
	private void generateComponentParamsModel() {
		componentParams.clear();
		ObjectMapper mapper = new ObjectMapper();
		Iterator<String> it = componentProperties.keySet().iterator();
		while (it.hasNext()) {
			String paramName = it.next();
			ComponentParam p = mapper.convertValue(componentProperties.get(paramName), ComponentParam.class);
			p.setName(paramName);
			componentParams.add(p);
		}
	}
}

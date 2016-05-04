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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lhein
 *
 */
public class LanguageModel {
	private Language language;
	private HashMap<String, HashMap> properties;
	private ArrayList<LanguageProperty> params = new ArrayList<LanguageProperty>();

	/**
	 * @return the language
	 */
	public Language getLanguage() {
		return this.language;
	}
	
	/**
	 * @param language the language to set
	 */
	public void setLanguage(Language language) {
		this.language = language;
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
		generateParamsModel();
	}
	
	/**
	 * @return the uriParams
	 */
	public ArrayList<LanguageProperty> getParams() {
		return this.params;
	}
	
	/**
	 * used to generate the list of uri params for this component
	 */
	private void generateParamsModel() {
		params.clear();
		ObjectMapper mapper = new ObjectMapper();
		Iterator<String> it = properties.keySet().iterator();
		while (it.hasNext()) {
			String paramName = it.next();
			LanguageProperty p = mapper.convertValue(properties.get(paramName), LanguageProperty.class);
			p.setName(paramName);
			params.add(p);
		}
	}
}

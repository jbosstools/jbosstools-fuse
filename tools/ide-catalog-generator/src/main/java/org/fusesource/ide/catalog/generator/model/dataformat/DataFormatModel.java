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

package org.fusesource.ide.catalog.generator.model.dataformat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lhein
 *
 */
public class DataFormatModel {
	private DataFormat dataformat;
	private HashMap<String, HashMap> properties;
	private ArrayList<DataFormatProperty> params = new ArrayList<DataFormatProperty>();


	/**
	 * @return the dataformat
	 */
	public DataFormat getDataformat() {
		return this.dataformat;
	}
	
	/**
	 * @param dataformat the dataformat to set
	 */
	public void setDataformat(DataFormat dataformat) {
		this.dataformat = dataformat;
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
	public ArrayList<DataFormatProperty> getParams() {
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
			DataFormatProperty p = mapper.convertValue(properties.get(paramName), DataFormatProperty.class);
			p.setName(paramName);
			params.add(p);
		}
	}
}

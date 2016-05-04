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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author lhein
 *
 */
public class EIPModel {
	@JsonProperty("model")
	private EIP eip;
	private HashMap<String, HashMap> properties;
	private ArrayList<EIPProperty> params = new ArrayList<EIPProperty>();

	/**
	 * @return the eip
	 */
	public EIP getEip() {
		return this.eip;
	}
	
	/**
	 * @param eip the eip to set
	 */
	public void setEip(EIP eip) {
		this.eip = eip;
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
	public ArrayList<EIPProperty> getParams() {
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
			EIPProperty p = mapper.convertValue(properties.get(paramName), EIPProperty.class);
			p.setName(paramName);
			
			String originalVar = getOriginalVarName(eip.getJavaType(), p.getName());
			p.setOriginalVariableName(originalVar);
			params.add(p);
		}
	}
	
	private String getOriginalVarName(String javaType, String paramName) {
		// now look for variables in the class which have jaxb annotation with that name
		String varName = paramName;
		try {
			Class c = Class.forName(javaType);
			for (Field f : c.getDeclaredFields()) {
				Annotation[] annos = f.getDeclaredAnnotations();
				for (Annotation a : annos) {
					if (a instanceof javax.xml.bind.annotation.XmlElement) {
						javax.xml.bind.annotation.XmlElement e = (javax.xml.bind.annotation.XmlElement)a;
						if (e.name().equals(varName) && f.getName().equals(e.name()) == false) {
							return f.getName();
						}
					}
					if (a instanceof javax.xml.bind.annotation.XmlAttribute) {
						javax.xml.bind.annotation.XmlAttribute e = (javax.xml.bind.annotation.XmlAttribute)a;
						if (e.name().equals(varName) && f.getName().equals(e.name()) == false) {
							return f.getName();
						}
					}
				}
			}
		} catch (ClassNotFoundException cnfex) {
			cnfex.printStackTrace();
		}
		return varName;
	}
}

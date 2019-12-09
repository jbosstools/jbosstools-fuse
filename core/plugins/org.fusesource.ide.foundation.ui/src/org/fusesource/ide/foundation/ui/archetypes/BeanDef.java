/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.ui.archetypes;

import java.util.ArrayList;

/**
 * @author lhein
 *
 */
public class BeanDef {
	
	private String beanType;
	private String id;
	private String className;
	private ArrayList<BeanProp> properties;

	/**
	 * 
	 */
	public BeanDef(String beanType, String id, String className) {
		this.beanType = beanType;
		this.id = id;
		this.className = className;
		this.properties = new ArrayList<BeanProp>();
	}
	
	/**
	 * @return the beanType
	 */
	public String getBeanType() {
		return this.beanType;
	}
	
	/**
	 * @param beanType the beanType to set
	 */
	public void setBeanType(String beanType) {
		this.beanType = beanType;
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return this.className;
	}
	
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
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
	 * @return the properties
	 */
	public ArrayList<BeanProp> getProperties() {
		return this.properties;
	}
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(ArrayList<BeanProp> properties) {
		this.properties = properties;
	}
	
	public void addProperty(BeanProp prop) {
		if (!this.properties.contains(prop)) {
			this.properties.add(prop);
		}
	}
	
	public void removeProperty(BeanProp prop) {
		if (this.properties.contains(prop)) {
			this.properties.remove(prop);
		}
	}
}

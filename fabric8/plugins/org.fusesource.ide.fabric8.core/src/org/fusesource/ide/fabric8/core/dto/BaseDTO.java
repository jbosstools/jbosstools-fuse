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
package org.fusesource.ide.fabric8.core.dto;

import java.util.Map;

import org.fusesource.ide.fabric8.core.connector.Fabric8Facade;

/**
 * common parent class for all DTO classes
 * 
 * @author lhein
 */
public abstract class BaseDTO {

	protected static final String JSON_FIELD_ID = "id";
	
	protected Fabric8Facade fabric8;
	protected String id;
	protected Map<String, Object> jsonAttribs;

	/**
	 * creates the dto
	 * 
	 * @param fabric8
	 * @param jsonAttribs
	 */
	public BaseDTO(Fabric8Facade fabric8, Map<String, Object> jsonAttribs) {
		this.fabric8 = fabric8;
		this.id = (String)jsonAttribs.get(JSON_FIELD_ID);
		this.jsonAttribs = jsonAttribs;
	}

	/**
	 * returns the id of the version
	 * 
	 * @return	the version id as java.lang.String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * returns the attributes of the version
	 * 
	 * @return	a map containing the version attributes
	 */
	public Map<String, Object> getJsonAttributes() {
		return this.jsonAttribs;
	}

	/**
	 * creates the object on the runtime
	 */
	public abstract void create();
	
	/**
	 * deletes the object from the runtime
	 */
	public abstract void delete();
	
	/**
	 * updates the object on the runtime
	 */
	public abstract void update();
	
	/**
	 * 
	 * @param fieldName
	 * @return
	 */
	protected <T> T getFieldValue(String fieldName) {
		return (T) getJsonAttributes().get(fieldName);
	}
	
	/**
	 * sets the value of a field to a new value
	 * 
	 * @param fieldName
	 * @param value
	 */
	protected void setFieldValue(String fieldName, Object value) {
		getJsonAttributes().put(fieldName, value);
	}
}
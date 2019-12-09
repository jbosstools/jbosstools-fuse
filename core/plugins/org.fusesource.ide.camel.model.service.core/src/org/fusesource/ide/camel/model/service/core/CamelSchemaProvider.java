/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.fusesource.ide.camel.model.service.core;

/**
 * @author lhein
 *
 */
public class CamelSchemaProvider {
	
	private String blueprintSchema;
	private String springSchema;
	
	/**
	 * creates a schema provider
	 * 
	 * @param blueprintSchema	the blueprint schema as string
	 * @param springSchema		the spring schema as string
	 */
	public CamelSchemaProvider(String blueprintSchema, String springSchema) {
		this.blueprintSchema = blueprintSchema;
		this.springSchema = springSchema;
	}
	
	/**
	 * @return the blueprintSchema
	 */
	public String getBlueprintSchema() {
		return this.blueprintSchema;
	}
	
	/**
	 * @return the springSchema
	 */
	public String getSpringSchema() {
		return this.springSchema;
	}
}

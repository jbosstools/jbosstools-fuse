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

/**
 * @author lhein
 *
 */
public class BeanProp {
	
	private String name;
	private String value;
	private String ref;
	
	/**
	 * 
	 */
	public BeanProp(String name, String value, String ref) {
		this.name = name;
		this.value = value;
		this.ref = ref;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the ref
	 */
	public String getRef() {
		return this.ref;
	}
	
	/**
	 * @param ref the ref to set
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}

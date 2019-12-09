/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.properties;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.foundation.core.functions.ReturnType;


/**
 * A complex property which has a list of possible value types
 * 
 * @author jstrachan
 */
public class ComplexUnionPropertyDescriptor extends PropertyDescriptor implements ReturnType {

	private final UnionTypeValue[] valueTypes;
	private final Class<?> propertyType;

	/**
	 * creates a property descriptor for complex properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public ComplexUnionPropertyDescriptor(Object id, String displayName, Class<?> propertyType, UnionTypeValue[] valueTypes) {
		super(id, displayName);
		this.propertyType = propertyType;
		this.valueTypes = valueTypes;
	}

	public UnionTypeValue[] getValueTypes() {
		return valueTypes;
	}

	public Class<?> getPropertyType() {
		return propertyType;
	}

	@Override
	public Class<?> getReturnType() {
		return propertyType;
	}
}

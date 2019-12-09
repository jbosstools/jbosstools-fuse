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

/**
 * A possible kind of value of a {@link ComplexUnionPropertyDescriptor}
 * 
 */
public class UnionTypeValue {
	private final String id;
	private final Class<?> valueType;

	public UnionTypeValue(String id, Class<?> valueType) {
		this.id = id;
		this.valueType = valueType;
	}

	public String getId() {
		return id;
	}

	public Class<?> getValueType() {
		return valueType;
	}
}

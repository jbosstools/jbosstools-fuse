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

import java.util.List;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.foundation.core.functions.ReturnType;


/**
 * @author jstrachan
 */
public class ListPropertyDescriptor extends PropertyDescriptor implements ReturnType {

	/**
	 * creates a property descriptor for list properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public ListPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public Class<?> getElementType() {
		return String.class;
	}


	@Override
	public Class<?> getReturnType() {
		return List.class;
	}
}

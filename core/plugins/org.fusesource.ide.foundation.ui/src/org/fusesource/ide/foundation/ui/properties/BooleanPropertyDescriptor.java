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
 * @author jstrachan
 */
public class BooleanPropertyDescriptor extends PropertyDescriptor implements ReturnType<Boolean> {

	/**
	 * creates a property descriptor for boolean properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public BooleanPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	@Override
	public Class<Boolean> getReturnType() {
		return Boolean.class;
	}
}

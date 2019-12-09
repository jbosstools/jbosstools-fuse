/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
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
 * @author lheinema
 *
 */
public class NumberPropertyDescriptor extends PropertyDescriptor implements ReturnType<String> {
	
	/**
	 * creates a property descriptor for number properties
	 * 
	 * @param id	the id
	 * @param displayName	the display name
	 */
	public NumberPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.foundation.core.functions.ReturnType#getReturnType()
	 */
	@Override
	public Class<String> getReturnType() {
		return String.class;
	}
}

/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.rest;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.camel.editor.globalconfiguration.util.ValidationUtil;
import org.fusesource.ide.camel.editor.properties.bean.PropertyRequiredValidator;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author brianf
 *
 */
public class NewRestIdPropertyValidator extends PropertyRequiredValidator {
	
	private AbstractCamelModelElement parent;

	public NewRestIdPropertyValidator(Parameter prop, AbstractCamelModelElement parent) {
		super(prop);
		this.parent = parent;
	}

	@Override
	public IStatus validate(Object value) {
		IStatus superStatus = super.validate(value);
		if (Status.OK_STATUS.equals(superStatus)) {
			return ValidationUtil.validateIdInParent(parent, value);
		}
		return superStatus;
	}
	

}
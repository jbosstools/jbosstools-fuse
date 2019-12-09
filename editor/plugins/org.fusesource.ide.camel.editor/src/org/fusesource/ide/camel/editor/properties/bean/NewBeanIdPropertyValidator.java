/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.bean;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.validation.BeanValidationUtil;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author brianf
 *
 */
public class NewBeanIdPropertyValidator extends PropertyRequiredValidator {
	
	private AbstractCamelModelElement parent;

	public NewBeanIdPropertyValidator(Parameter prop, AbstractCamelModelElement parent) {
		super(prop);
		this.parent = parent;
	}

	@Override
	public IStatus validate(Object value) {
		IStatus superStatus = super.validate(value);
		if (Status.OK_STATUS.equals(superStatus)) {
			return BeanValidationUtil.validateIdInParent(parent, value);
		}
		return superStatus;
	}
	

}
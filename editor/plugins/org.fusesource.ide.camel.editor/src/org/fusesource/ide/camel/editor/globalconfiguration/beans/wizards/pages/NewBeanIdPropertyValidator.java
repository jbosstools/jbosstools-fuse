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
package org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.validation.BeanValidationUtil;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author brianf
 *
 */
public class NewBeanIdPropertyValidator extends BeanRequiredPropertyValidator {
	
	private AbstractCamelModelElement parent;

	public NewBeanIdPropertyValidator(String propName, AbstractCamelModelElement parent) {
		super(propName);
		this.parent = parent;
	}

	@Override
	public IStatus validate(Object value) {
		IStatus superStatus = super.validate(value);
		if (superStatus.equals(Status.OK_STATUS)) {
			return BeanValidationUtil.validateIdInParent(parent, value);
		}
		return superStatus;
	}
	

}
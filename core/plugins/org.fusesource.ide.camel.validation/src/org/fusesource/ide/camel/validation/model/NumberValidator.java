/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.validation.model;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;

/**
 * @author Aurelien Pupier
 *
 */
public class NumberValidator implements IValidator {

	private Parameter parameter;

	public NumberValidator(Parameter parameter) {
		this.parameter = parameter;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public IStatus validate(Object value) {
		// only check non-empty fields
		if (CamelComponentUtils.isNumberProperty(parameter)) {
			if (value != null && value.toString().trim().length() > 0) {
				try {
					Double.parseDouble(value.toString());
				} catch (NumberFormatException ex) {
					return ValidationStatus.error("The parameter " + parameter.getName() + " requires a numeric value.");
				}
			}
		}
		return ValidationStatus.ok();
	}

}

/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.bean;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 *
 */
public class PropertyRequiredValidator implements IValidator {
	
	private Parameter parameter;
	
	public PropertyRequiredValidator(Parameter prop) {
		this.parameter = prop;
	}
	
	@Override
	public IStatus validate(Object value) {
		if (value != null && value instanceof String && !Strings.isEmpty((String) value)) {
			return ValidationStatus.ok();
		}
		return ValidationStatus
				.error("Parameter " + parameter.getName() + " is mandatory and cannot be empty.");
	}
	

}
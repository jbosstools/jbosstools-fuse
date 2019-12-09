/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.fusesource.ide.camel.validation.model;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.camel.validation.l10n.Messages;

/**
 * @author Aurelien Pupier
 *
 */
public class RequiredPropertyValidator implements IValidator {

	private Parameter parameter;
	public RequiredPropertyValidator(Parameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public IStatus validate(Object value) {
		if (PropertiesUtils.isRequired(parameter) && ((value == null || value.toString().trim().isEmpty()) && parameter.getDefaultValue() == null)) {
			return ValidationStatus.error(NLS.bind(Messages.RequiredPropertyValidator_messageMissingParameter, parameter.getName()));
		}
		return ValidationStatus.ok();
	}

}

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
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;
import org.fusesource.ide.camel.validation.l10n.Messages;
import org.fusesource.ide.foundation.core.util.CamelPlaceHolderUtil;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author Aurelien Pupier
 *
 */
public class NumberValidator implements IValidator<Object> {

	private Parameter parameter;

	public NumberValidator(Parameter parameter) {
		this.parameter = parameter;
	}
	
	@Override
	public IStatus validate(Object value) {
		if (Strings.isNonEmptyAndNotOnlySpaces(value) && !new CamelPlaceHolderUtil().isPlaceHolder(value)) {
			String javaType = parameter.getJavaType();
			if (CamelComponentUtils.isIntegerTypeProperty(javaType)) {
				return handlePotentialDuration(value);
			} else if(CamelComponentUtils.isDoubleTypeProperty(javaType)) {
				return validateDouble(value);
			} else if(CamelComponentUtils.isLongTypeProperty(javaType)) {
				return handlePotentialDuration(value);
			} else if(CamelComponentUtils.isFloatTypeProperty(javaType)) {
				return validateFloat(value);
			}
		}
		return ValidationStatus.ok();
	}

	private IStatus validateFloat(Object value) {
		try {
			Float.parseFloat(value.toString());
		} catch(NumberFormatException nfe) {
			return ValidationStatus.error(NLS.bind(Messages.numberValidatorMessageErrorForFloat, parameter.getName()), nfe);
		}
		return ValidationStatus.ok();
	}

	private IStatus validateDouble(Object value) {
		try {
			Double.parseDouble(value.toString());
		} catch(NumberFormatException nfe) {
			return ValidationStatus.error(NLS.bind(Messages.numberValidatorMessageErrorForDouble, parameter.getName()), nfe);
		}
		return ValidationStatus.ok();
	}

	private IStatus handlePotentialDuration(Object value) {
		try {
			PropertiesUtils.validateDuration(value.toString());
		} catch (IllegalArgumentException ex) {
			return ValidationStatus.error(NLS.bind(Messages.numberValidatorMessageError, parameter.getName()), ex);
		}
		return ValidationStatus.ok();
	}
}

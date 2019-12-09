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
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

/**
 * @author Aurelien Pupier
 *
 */
public class RefOrDataFormatUnicityChoiceValidator implements IValidator {

	private AbstractCamelModelElement cme;
	private Parameter parameter;

	public RefOrDataFormatUnicityChoiceValidator(AbstractCamelModelElement cme, Parameter parameter) {
		this.cme = cme;
		this.parameter = parameter;
	}

	@Override
	public IStatus validate(Object value) {
		final String parameterName = parameter.getName();
		if ("ref".equalsIgnoreCase(parameterName)) {
			if (isRefEmpty(value)) {
				for (Parameter otherParameter : PropertiesUtils.getPropertiesFor(cme)) {
					if (isDataFormatToCheck(otherParameter)) {
						if (cme.getParameter(otherParameter.getName()) != null) {
							return ValidationStatus.error("Please choose only ONE of Ref and " + otherParameter.getName());
						}
					}
				}
			}
		} else if (isDataFormatToCheck(parameter)) {
			if (value != null) {
				final Object refValue = cme.getParameter("ref");
				if (isRefEmpty(refValue)) {
					return ValidationStatus.error("Please choose only ONE of " + parameter.getName() + " and Ref.");
				}
			}
		}
		return ValidationStatus.ok();
	}

	/**
	 * @param value
	 * @return
	 */
	private boolean isRefEmpty(Object value) {
		return value != null && value instanceof String && !((String) value).isEmpty();
	}

	/**
	 * @param otherParameter
	 * @return
	 */
	private boolean isDataFormatToCheck(Parameter otherParameter) {
		return cme.isElementKind(otherParameter) && cme.isDataFormatDefinition(otherParameter);
	}

}

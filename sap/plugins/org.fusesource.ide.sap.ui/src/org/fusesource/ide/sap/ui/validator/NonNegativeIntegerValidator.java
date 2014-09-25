/*******************************************************************************
* Copyright (c) 2014 Red Hat, Inc.
* Distributed under license by Red Hat, Inc. All rights reserved.
* This program is made available under the terms of the
* Eclipse Public License v1.0 which accompanies this distribution,
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Red Hat, Inc. - initial API and implementation
* William Collins punkhornsw@gmail.com
******************************************************************************/ 
package org.fusesource.ide.sap.ui.validator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.sap.ui.Messages;

public class NonNegativeIntegerValidator implements IValidator {
	
	private String parameterName;
	
	public NonNegativeIntegerValidator() {
	}

	public NonNegativeIntegerValidator(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public String getMessage() {
		return parameterName == null ? Messages.NonNegativeIntegerValidator_ValueMustBeANonNegativeIntegerString : NLS.bind(Messages.NonNegativeIntegerValidator_ParameterMustBeANonNegativeIntegerString, parameterName);
	}

	@Override
	public IStatus validate(Object value) {
		if (value instanceof String) {
			String strValue = (String) value;
			if (strValue == null || strValue.length() == 0) 
				return ValidationStatus.ok();
			try {
				int intValue = Integer.parseInt(strValue);
				if (intValue < 0 )
					return ValidationStatus.error(getMessage());
				return ValidationStatus.ok();
			} catch (NumberFormatException e) {
				return ValidationStatus.error(getMessage());
			}
		}
		return ValidationStatus.error(getMessage());
	}

}

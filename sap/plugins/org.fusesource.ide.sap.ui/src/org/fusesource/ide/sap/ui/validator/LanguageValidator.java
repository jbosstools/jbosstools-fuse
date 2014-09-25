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
import org.fusesource.ide.sap.ui.Messages;

public class LanguageValidator implements IValidator {

	@Override
	public IStatus validate(Object value) {
		if (value instanceof String) {
			String strValue = (String) value;
			if (strValue == null || strValue.length() == 0) 
				return ValidationStatus.ok();
			if (strValue.length() != 2) {
				return ValidationStatus.error(Messages.LanguageValidator_LogonLanguageMustBeATwoDigitSapLanguageCodeString);
			}
			return ValidationStatus.ok();
		}
		return ValidationStatus.error(Messages.LanguageValidator_LogonLanguageMustBeATwoDigitSapLanguageCodeString);
	}

}

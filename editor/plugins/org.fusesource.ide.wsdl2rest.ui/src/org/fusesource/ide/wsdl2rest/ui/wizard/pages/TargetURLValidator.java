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
package org.fusesource.ide.wsdl2rest.ui.wizard.pages;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;

/**
 * Validates the URL to make sure it is well formed.
 * @author brianf
 */
public class TargetURLValidator implements IValidator {
	/**
	 * Local Apache URL Validator to handle the actual validation
	 */
	UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public IStatus validate(Object value) {
		if (((value instanceof String) && ((String) value).length() > 0)) {
			if (!urlValidator.isValid((String)value)) {
				return ValidationStatus.error(UIMessages.wsdl2RestWizardSecondPageValidatorServiceAddressMustBeValid);
			}
		}
		return ValidationStatus.ok();
	}
}
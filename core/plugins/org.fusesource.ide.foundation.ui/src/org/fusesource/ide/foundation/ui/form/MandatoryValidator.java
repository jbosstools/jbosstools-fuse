/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.foundation.ui.form;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;

/**
 * Validates that a property is mandatory
 */
public class MandatoryValidator implements IValidator {

	private String labelText;

	public MandatoryValidator(String labelText) {
		this.labelText = labelText;
	}

	@Override
	public IStatus validate(Object value) {
		if (isValid(value)) {
			return ValidationStatus.ok();
		} else {
			String message = NLS.bind(Messages.mandatoryValidationMessage, labelText);
			return ValidationStatus.error(message);
		}
	}

	protected boolean isValid(Object value) {
		String s = String.valueOf(value);
		boolean valid = s.trim().length() > 0;
		return valid;
	}
}
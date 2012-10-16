package org.fusesource.ide.commons.ui.form;

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
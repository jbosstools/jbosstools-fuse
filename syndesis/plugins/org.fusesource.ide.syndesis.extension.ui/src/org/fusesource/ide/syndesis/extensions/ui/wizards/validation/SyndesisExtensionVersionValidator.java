/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.syndesis.extensions.ui.wizards.validation;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.syndesis.extensions.core.util.SyndesisExtensionsUtil;
import org.fusesource.ide.syndesis.extensions.ui.internal.Messages;

/**
 * @author lheinema
 */
public class SyndesisExtensionVersionValidator implements IValidator {

	@Override
	public IStatus validate(Object value) {
		String version = (String) value;
		if (Strings.isBlank(version)) {
			return ValidationStatus.error(Messages.newProjectWizardExtensionDetailsPageErrorMissingExtensionVersion);
		} else if (!SyndesisExtensionsUtil.isValidSyndesisExtensionVersion(version)) {
			return ValidationStatus.error(Messages.newProjectWizardExtensionDetailsPageErrorInvalidExtensionVersion);
		} 
	    return ValidationStatus.ok();
	}
}

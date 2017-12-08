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
package org.fusesource.ide.wsdl2rest.ui.wizard.pages;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;

/**
 * Validate that the named project exists in the workbench.
 * @author brianf
 */
class ProjectNameValidator implements IValidator {

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public IStatus validate(Object value) {
		if (!((value instanceof String) && ((String) value).length() > 0)) {
			return ValidationStatus.error(UIMessages.wsdl2RestWizardFirstPageValidatorProjectNameRequired);
		}
		try {
			IProject testProject = ResourcesPlugin.getWorkspace().getRoot().getProject((String)value);
			if (!testProject.exists()) {
				return ValidationStatus.error(UIMessages.wsdl2RestWizardFirstPageValidatorProjectMustBeInWorkspace);
			}
		} catch (Exception ex) {
			return ValidationStatus.error(UIMessages.wsdl2RestWizardFirstPageValidatorProjectMustBeInWorkspace);
		}
		return ValidationStatus.ok();
	}
}
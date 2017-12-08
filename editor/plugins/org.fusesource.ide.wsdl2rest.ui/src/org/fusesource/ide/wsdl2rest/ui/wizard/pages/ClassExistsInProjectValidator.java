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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;

/**
 * @author brianf
 *
 */
public class ClassExistsInProjectValidator implements IValidator {
	/**
	 * 
	 */
	private final Wsdl2RestWizardSecondPage wsdl2RestWizardSecondPage;

	/**
	 * @param wsdl2RestWizardSecondPage
	 */
	ClassExistsInProjectValidator(Wsdl2RestWizardSecondPage wsdl2RestWizardSecondPage) {
		this.wsdl2RestWizardSecondPage = wsdl2RestWizardSecondPage;
	}

	@Override
	public IStatus validate(Object value) {
		if (((value instanceof String) && ((String) value).length() > 0)) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(this.wsdl2RestWizardSecondPage.getOptionsFromWizard().getProjectName());
			IJavaProject javaProject = JavaCore.create(project);
			IType javaClass;
			try {
				javaClass = javaProject == null ? null : javaProject.findType((String) value);
				if (javaClass == null) {
					return ValidationStatus.error(UIMessages.wsdl2RestWizardSecondPageValidatorJavaClassMustExist);
				}
			} catch (JavaModelException e) {
				return ValidationStatus.error(UIMessages.wsdl2RestWizardSecondPageValidatorJavaClassMustExist, e);
			}
		}
		return ValidationStatus.ok();
	}
}
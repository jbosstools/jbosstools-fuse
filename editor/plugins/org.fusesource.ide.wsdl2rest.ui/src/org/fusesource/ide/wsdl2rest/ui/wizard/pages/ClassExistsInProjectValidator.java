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
	 * Reference back to the page where this is used.
	 */
	private final Wsdl2RestWizardBasePage referencedPage;

	/**
	 * Constructor
	 * @param wsdl2RestWizardSecondPage
	 */
	public ClassExistsInProjectValidator(Wsdl2RestWizardSecondPage referencedPage) {
		this.referencedPage = referencedPage;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
	 */
	@Override
	public IStatus validate(Object value) {
		if (((value instanceof String) && (!((String) value).isEmpty()))) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
					this.referencedPage.getOptionsFromWizard().getProjectName());
			IJavaProject javaProject = JavaCore.create(project);
			try {
				IType javaClass = javaProject == null ? null : javaProject.findType((String) value);
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
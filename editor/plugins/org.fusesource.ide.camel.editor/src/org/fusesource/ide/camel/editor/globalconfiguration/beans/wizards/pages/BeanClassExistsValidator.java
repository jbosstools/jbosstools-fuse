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
package org.fusesource.ide.camel.editor.globalconfiguration.beans.wizards.pages;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author brianf
 *
 */
public class BeanClassExistsValidator implements IValidator {
	
	private IProject project;

	public BeanClassExistsValidator(IProject project) {
		this.project = project;
	}

	@Override
	public IStatus validate(Object value) {
		String className = (String) value;
		if (className == null || className.isEmpty()) {
			return ValidationStatus.error("Bean class name is mandatory.");
		}
		IJavaProject javaProject = JavaCore.create(this.project);
        IType javaClass;
		try {
			javaClass = javaProject == null ? null : javaProject.findType(className);
			if (javaClass == null) {
				return ValidationStatus.error("Bean class must exist in the Fuse project.");
			}
		} catch (JavaModelException e) {
			return ValidationStatus.error("Bean class must exist in the Fuse project.", e);
		}
		return ValidationStatus.ok();
	}
}
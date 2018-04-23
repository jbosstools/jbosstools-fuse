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

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;

/**
 * Validates that a given path exists in the selected project.
 * @author brianf
 */
public class PathValidator implements IValidator {

	/**
	 * Verify that the path exists in the selected project.
	 * @param path
	 * @return
	 */
	private boolean isPathAccessible(String path) {
		Path testPath = new Path(path);
		IResource container = 
				ResourcesPlugin.getWorkspace().getRoot().findMember(testPath);
		while (container == null && !testPath.isEmpty()) {
			testPath = (Path) testPath.removeLastSegments(1);
			container = ResourcesPlugin.getWorkspace().getRoot().findMember(testPath);
			if (container != null && container.exists()) {
				break;
			}
		}
		return container != null && container.exists();
	}

	/**
	 * If part of the path exists in the selected project, this will check if it's 
	 * going to have to be created when the user clicks Finish. If it doesn't already
	 * exist, we will provide a warning that it will be created.
	 * @param path
	 * @return
	 */
	private boolean isPathAccessibleAbsolute(String path) {
		Path testPath = new Path(path);
		IResource container = 
				ResourcesPlugin.getWorkspace().getRoot().findMember(testPath);
		return container != null && container.exists();
	}

	@Override
	public IStatus validate(Object value) {
		if (value instanceof String) {
			String stringValue = (String)value;
			if(stringValue.isEmpty()){
				return ValidationStatus.error(UIMessages.wsdl2RestWizardSecondPageValidatorPathRequired);
			}
			if (!isPathAccessible(stringValue)) {
				return ValidationStatus.error(UIMessages.wsdl2RestWizardSecondPageValidatorPathMustBeAccessible);
			}
			if (!isPathAccessibleAbsolute(stringValue)) {
				return ValidationStatus.warning(UIMessages.wsdl2RestWizardSecondPageValidatorPathWarning);
			}
		}
		return ValidationStatus.ok();   		
	}
}
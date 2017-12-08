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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.fusesource.ide.wsdl2rest.ui.internal.UIMessages;

/**
 * @author brianf
 *
 */
public class PathValidator implements IValidator {

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

	@Override
	public IStatus validate(Object value) {
		if (!((value instanceof String) && ((String) value).length() > 0)) {
			return ValidationStatus.error(UIMessages.wsdl2RestWizardSecondPageValidatorPathRequired);
		}
		if (!isPathAccessible((String) value)) {
			return ValidationStatus.error(UIMessages.wsdl2RestWizardSecondPageValidatorPathMustBeAccessible);
		}
		return ValidationStatus.ok();   		
	}
}
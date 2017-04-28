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
package org.fusesource.ide.camel.editor.properties.bean;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 *
 */
public class PropertyMethodValidator implements IValidator {
	
	private IObservableMap<?, ?> modelMap;
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private IProject project;
	
	public PropertyMethodValidator(IObservableMap<?, ?> modelMap, IProject project) {
		this.modelMap = modelMap;
		this.project = project;
	}
	
	@Override
	public IStatus validate(Object value) {
		Object control = modelMap.get(CamelBean.PROP_CLASS);
		if (control != null) {
			String className = (String) control;
			String methodName = (String) value;
			IJavaProject jproject = beanConfigUtil.getJavaProject(this.project);
			if (!Strings.isEmpty(methodName) && jproject != null) {
				try {
					return validateMethod(jproject, className, methodName);
				} catch (JavaModelException e) {
					CamelEditorUIActivator.pluginLog().logError("Error validating method for class " + className,
							e);
				}
			}
		}
		return ValidationStatus.ok();
	}
	
	private IStatus validateMethod(IJavaProject jproject, String className, String methodName) throws JavaModelException{
		IType foundClass = jproject.findType(className);
		boolean foundMethod = beanConfigUtil.hasMethod(methodName, foundClass);
		if (!foundMethod) {
			return ValidationStatus
					.error("Method " + methodName + " must exist in class " + className + ".");
		}
		return ValidationStatus.ok();
	}

}
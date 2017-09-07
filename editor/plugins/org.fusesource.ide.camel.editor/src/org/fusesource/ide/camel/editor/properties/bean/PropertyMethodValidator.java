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
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 *
 */
public class PropertyMethodValidator implements IValidator {
	
	private IObservableMap<?, ?> modelMap;
	private BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private IProject project;
	private String factoryBeanTag = null;
	private AbstractCamelModelElement parent = null;
	private IJavaProject jproject;
	
	public PropertyMethodValidator(IObservableMap<?, ?> modelMap, IProject project) {
		this.modelMap = modelMap;
		this.project = project;
		jproject = beanConfigUtil.getJavaProject(this.project);
	}
	
	public PropertyMethodValidator(IObservableMap<?, ?> modelMap, IProject project, AbstractCamelModelElement element) {
		this(modelMap, project);
		this.parent = element;
		this.factoryBeanTag = beanConfigUtil.getFactoryBeanTag(element.getXmlNode());
	}

	@Override
	public IStatus validate(Object value) {
		Object control = modelMap.get(GlobalBeanEIP.PROP_CLASS);
		if (control != null) {
			String className = (String) control;
			String referencedClassName = null;
			if (Strings.isEmpty(className) && !Strings.isEmpty(factoryBeanTag)) {
				String beanRefId = (String) modelMap.get(factoryBeanTag);
				referencedClassName = beanConfigUtil.getClassNameFromReferencedCamelBean(parent, beanRefId);
			}
			String methodName = (String) value;
			if (!Strings.isEmpty(methodName) && (!Strings.isEmpty(className) || !Strings.isEmpty(referencedClassName)) && jproject != null) {
				if (!Strings.isEmpty(className)) {
					return validateWrapper(className, methodName);
				}
				if (!Strings.isEmpty(referencedClassName)) {
					return validateWrapper(referencedClassName, methodName);
				}
			}
		}
		return ValidationStatus.ok();
	}
	
	private IStatus validateWrapper(String className, String methodName) {
		try {
			return validateMethod(className, methodName);
		} catch (JavaModelException e) {
			CamelEditorUIActivator.pluginLog().logError(UIMessages.propertyMethodValidatorMethodValidationError + className,
					e);
		}
		return ValidationStatus.ok();
	}
	
	private IStatus validateMethod(String className, String methodName) throws JavaModelException{
		IType foundClass = jproject.findType(className);
		boolean foundMethod = beanConfigUtil.hasMethod(methodName, foundClass);
		if (!foundMethod) {
			return ValidationStatus
					.error(UIMessages.propertyMethodValidatorMethodValidatorErrorPt2 + methodName + UIMessages.propertyMethodValidatorMethodValidatorErrorPt3 + className + "."); //$NON-NLS-3$
		}
		return ValidationStatus.ok();
	}

}
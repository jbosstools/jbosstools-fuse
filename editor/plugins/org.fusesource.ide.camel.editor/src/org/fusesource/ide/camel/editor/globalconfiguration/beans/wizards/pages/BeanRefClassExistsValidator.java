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

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Text;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 *
 */
public class BeanRefClassExistsValidator implements IValidator {

	private IObservableMap<?, ?> modelMap = null;
	private IProject project;
	private AbstractCamelModelElement parent;
	private Text beanClassText = null;
	protected BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private IJavaProject javaProject = null;

	public BeanRefClassExistsValidator(IProject project) {
		this.project = project;
		javaProject = JavaCore.create(this.project);
	}

	public BeanRefClassExistsValidator(IProject project, AbstractCamelModelElement element) {
		this(project);
		this.parent = element;
	}

	public BeanRefClassExistsValidator(IProject project, AbstractCamelModelElement element, Text control) {
		this(project, element);
		this.beanClassText = control;
	}

	public BeanRefClassExistsValidator(IProject project, AbstractCamelModelElement element, IObservableMap<?, ?> modelMap) {
		this (project, element);
		this.modelMap = modelMap;
	}

	public void setControl(Text textControl) {
		this.beanClassText = textControl;
	}

	private IStatus classExistsInProject(String className) {
		IType javaClass;
		try {
			javaClass = javaProject == null ? null : javaProject.findType(className);
			if (javaClass == null) {
				return ValidationStatus.error(UIMessages.beanClassExistsValidatorErrorBeanClassMustExist);
			}
		} catch (JavaModelException e) {
			return ValidationStatus.error(UIMessages.beanClassExistsValidatorErrorBeanClassMustExist, e);
		}
		return ValidationStatus.ok();
	}

	@Override
	public IStatus validate(Object value) {
		String beanRefId = (String) value;
		if(!Strings.isEmpty(beanRefId)) {
			String className = null;
			if (beanClassText != null && !beanClassText.isDisposed()) {
				className = beanClassText.getText();
			} else if (modelMap != null) {
				Object control = modelMap.get(GlobalBeanEIP.PROP_CLASS);
				if (control != null) {
					className = (String) control;
				}
			} 

			String referencedClassName = beanConfigUtil.getClassNameFromReferencedCamelBean(parent, beanRefId);
			IStatus firstStatus = classExistsInProject(referencedClassName);
			if (firstStatus != ValidationStatus.ok() && !Strings.isEmpty(className)) {
				return classExistsInProject(className);
			}
		}
		return ValidationStatus.ok();
	}
}
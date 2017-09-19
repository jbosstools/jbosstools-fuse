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
import org.eclipse.swt.widgets.Combo;
import org.fusesource.ide.camel.editor.globalconfiguration.beans.BeanConfigUtil;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.eips.GlobalBeanEIP;
import org.fusesource.ide.foundation.core.util.Strings;

/**
 * @author brianf
 *
 */
public class BeanClassExistsValidator implements IValidator {

	private IObservableMap<?, ?> modelMap = null;
	private IProject project;
	private AbstractCamelModelElement parent = null;
	private Combo beanRefIdCombo = null;
	protected BeanConfigUtil beanConfigUtil = new BeanConfigUtil();
	private String factoryBeanTag = null;
	private IJavaProject javaProject = null;

	public BeanClassExistsValidator(IProject project) {
		this.project = project;
		javaProject = JavaCore.create(this.project);
	}

	public BeanClassExistsValidator(IProject project, AbstractCamelModelElement element) {
		this(project);
		this.parent = element;
		this.factoryBeanTag = beanConfigUtil.getFactoryBeanTag(element.getXmlNode());
	}

	public BeanClassExistsValidator(IProject project, AbstractCamelModelElement element, Combo refCombo) {
		this (project, element);
		this.beanRefIdCombo = refCombo;
	}

	public BeanClassExistsValidator(IProject project, AbstractCamelModelElement element, IObservableMap<?, ?> modelMap) {
		this (project, element);
		this.modelMap = modelMap;
	}

	public void setControl(Combo control) {
		this.beanRefIdCombo = control;
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

	private String getBeanReferenceId() {
		String beanRefId = null;
		if (beanRefIdCombo != null && !beanRefIdCombo.isDisposed()) {
			beanRefId = beanRefIdCombo.getText();
		} else if (modelMap != null) {
			Object control = modelMap.get(GlobalBeanEIP.PROP_CLASS);
			if (control != null) {
				String className = (String) control;
				if (Strings.isEmpty(className) && !Strings.isEmpty(factoryBeanTag)) {
					beanRefId = (String) modelMap.get(factoryBeanTag);
				}
			}
		} else if (parent != null && parent.getParameter(factoryBeanTag) != null) {
			beanRefId = (String) parent.getParameter(factoryBeanTag);
		}
		return beanRefId;
	}

	@Override
	public IStatus validate(Object value) {
		String className = (String) value;
		if (!Strings.isEmpty(className)) {
			String beanRefId = getBeanReferenceId();
			IStatus firstStatus = classExistsInProject(className);
			if (Strings.isEmpty(className) && firstStatus != ValidationStatus.ok() && !Strings.isEmpty(beanRefId)) {
				String referencedClassName = beanConfigUtil.getClassNameFromReferencedCamelBean(parent, beanRefId);
				return classExistsInProject(referencedClassName);
			} else {
				return firstStatus;
			}
		} else {
			return ValidationStatus.ok();
		}
	}

}
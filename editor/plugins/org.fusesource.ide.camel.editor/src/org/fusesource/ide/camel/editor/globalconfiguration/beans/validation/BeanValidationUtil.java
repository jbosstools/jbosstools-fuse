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
package org.fusesource.ide.camel.editor.globalconfiguration.beans.validation;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;
import org.fusesource.ide.camel.model.service.core.util.PropertiesUtils;

/**
 * @author brianf
 *
 */
public class BeanValidationUtil {
	
	private BeanValidationUtil() {
		// empty constructor
	}

	public static IStatus validateIdInParent(AbstractCamelModelElement parent, Object value) {
		String id = (String) value;
		if (parent instanceof CamelBean) {
			// we don't want the bean, we need the whole configuration
			parent = parent.getCamelFile();
		}
		if (parent.findAllNodesWithId(id).size() > 1) {
			return ValidationStatus.error(UIMessages.newBeanIdValidatorErrorBeanIDAlreadyUsed);
		}
		return ValidationStatus.ok();
	}
	
	public static IStatus validateRequiredParameter(Parameter parameter, Object value) {
		if (PropertiesUtils.isRequired(parameter) && (value == null || value.toString().trim().length() < 1)) {
			return ValidationStatus.error(NLS.bind(UIMessages.propertyRequiredValidatorMandatoryParameterEmptyPt, parameter.getName()));
		}
		return ValidationStatus.ok();
	}
}

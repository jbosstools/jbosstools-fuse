/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.bean;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.validation.model.RefOrDataFormatUnicityChoiceValidator;

/**
 * @author brianf
 *
 */
public class PropertyRefValidator extends PropertyRequiredValidator {
	
	private AbstractCamelModelElement parent;
	private Parameter prop;

	public PropertyRefValidator(Parameter prop, AbstractCamelModelElement parent) {
		super(prop);
		this.parent = parent;
		this.prop = prop;
	}

	@Override
	public IStatus validate(Object value) {
		IStatus superStatus = super.validate(value);
		if (Status.OK_STATUS.equals(superStatus)) {
			if (isNotEmptyString(value)
					&& parent.getRouteContainer().findNode((String)value) == null &&
					!parent.getCamelFile().getGlobalDefinitions().containsKey((String)value)) {
				// no ref found - could be something the server provides
				return ValidationStatus.warning("Parameter " + prop.getName() + " does not point to an existing reference inside the context.");
			}
			return new RefOrDataFormatUnicityChoiceValidator(parent, prop).validate(value);
		}
		return superStatus;
	}
	
	private boolean isNotEmptyString(Object value) {
		return value != null && value instanceof String && value.toString().trim().length()>0;
	}

}
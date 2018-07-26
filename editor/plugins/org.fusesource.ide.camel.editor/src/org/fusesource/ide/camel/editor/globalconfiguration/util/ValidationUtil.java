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
package org.fusesource.ide.camel.editor.globalconfiguration.util;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.fusesource.ide.camel.editor.internal.UIMessages;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author brianf
 *
 */
public class ValidationUtil {
	
	private ValidationUtil() {
		// empty constructor
	}

	public static IStatus validateIdInParent(AbstractCamelModelElement parent, Object value) {
		String id = (String) value;
		AbstractCamelModelElement elementToSearchIn = parent;
		if (elementToSearchIn.getRouteContainer().findAllNodesWithId(id).size() > 1) {
			return ValidationStatus.error(UIMessages.validationIDAlreadyUsed);
		}
		return ValidationStatus.ok();
	}
}

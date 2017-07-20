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

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * Code from Tillmann Seidel 
 * https://eclipsesource.com/blogs/2012/08/22/improving-reuse-of-jface-data-binding-validators/
 */
public class CompoundValidator implements IValidator {
    private final IValidator[] validators;

    public CompoundValidator(final IValidator... validators) {
        this.validators = validators;
    }

    public IStatus validate(final Object value) {
        IStatus result = ValidationStatus.ok();
        for (IValidator validator : validators) {
            IStatus status = validator.validate(value);

            if (status.getSeverity() > result.getSeverity()) {
                result = status;
            }
        }
        return result;
    }
}

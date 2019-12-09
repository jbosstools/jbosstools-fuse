/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.foundation.core.util;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * A way to break up Eclipse Data Binding framework validators into a bit
 * more modular, easy-to-digest chunks.
 *
 * This came from
 * http://eclipsesource.com/blogs/2012/08/22/improving-reuse-of-jface-data-binding-validators/
 *
 */
public class CompoundValidator implements IValidator {

    private final IValidator[] validators;

    public CompoundValidator(final IValidator... validators) {
        this.validators = validators;
    }

    @Override
    public IStatus validate(final Object value) {
    	IStatus result = ValidationStatus.ok();
    	for (IValidator validator : validators) {
    		if(validator != null){
    			IStatus status = validator.validate(value);

    			if (status.getSeverity() > result.getSeverity()) {
    				result = status;
    			}
    		}
    	}
        return result;
    }
}

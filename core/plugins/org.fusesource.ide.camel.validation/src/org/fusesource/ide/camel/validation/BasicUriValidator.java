/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.validation;

import java.net.URI;
import java.net.URISyntaxException;

import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class BasicUriValidator implements ValidationSupport {
    
    /*
     * (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.validation.ValidationSupport#validate(org.fusesource.ide.camel.model.service.core.model.CamelModelElement)
     */
    @Override
    public ValidationResult validate(AbstractCamelModelElement node) {
        ValidationResult res = new ValidationResult();
        
        final String underlyingModelName = node.getUnderlyingMetaModelObject().getName();
		if (underlyingModelName.equalsIgnoreCase("from") ||
        	underlyingModelName.equalsIgnoreCase("to")) {
            try {
                new URI(node.getParameter("uri").toString());
            } catch (URISyntaxException ex) {
                res.addError(ex.getMessage());
            }
        }
        
        return res;
    }
}

/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.validation;

import java.net.URI;
import java.net.URISyntaxException;

import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Endpoint;

/**
 * @author lhein
 */
public class BasicUriValidator implements ValidationSupport {
    
    /*
     * (non-Javadoc)
     * @see org.fusesource.ide.camel.editor.validation.BasicNodeValidator#validate(org.fusesource.ide.camel.model.AbstractNode)
     */
    @Override
    public ValidationResult validate(AbstractNode node) {
        ValidationResult res = new ValidationResult();
        
        if (node instanceof Endpoint) {
            Endpoint ep = (Endpoint)node;
            try {
                new URI(ep.getUri());
            } catch (URISyntaxException ex) {
                res.addError(ex.getMessage());
            }
        }
        
        return res;
    }
}

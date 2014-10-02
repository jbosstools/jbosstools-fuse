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
package org.fusesource.ide.camel.model;

/**
 * @author lhein
 *
 */
public class DefinedEndpoint extends Endpoint {
    /**
     * 
     */
    public DefinedEndpoint() {
        super();
    }
    
    public DefinedEndpoint(Endpoint endpoint) {
        super(endpoint);
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.Endpoint#getCategoryName()
     */
    @Override
    public String getCategoryName() {
        return "Endpoints";
    }
}

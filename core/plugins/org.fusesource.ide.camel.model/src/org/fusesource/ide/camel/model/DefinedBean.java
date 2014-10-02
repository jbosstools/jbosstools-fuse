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

import org.fusesource.ide.camel.model.generated.Bean;

/**
 * @author lhein
 *
 */
public class DefinedBean extends Bean {
    /**
     * 
     */
    public DefinedBean() {
        super();
    }
    
    public DefinedBean(Bean endpoint) {
        setRef(endpoint.getRef());
        setMethod(endpoint.getMethod());
        setBeanType(endpoint.getBeanType());
        setCache(endpoint.getCache());
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.Endpoint#getCategoryName()
     */
    @Override
    public String getCategoryName() {
        return "Endpoints";
    }
}

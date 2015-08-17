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

import org.fusesource.ide.camel.model.catalog.CamelModelFactory;
import org.fusesource.ide.camel.model.generated.UniversalEIPNode;

/**
 * @author lhein
 *
 */
public class DefinedBean extends UniversalEIPNode {
    /**
     * 
     */
    public DefinedBean() {
        super(CamelModelFactory.getModelForVersion(CamelModelFactory.getCamelVersion(null)).getEipModel().getEIPByClass("bean"));
    }
    
    // Takes a UniversalEIPNode representing a 'bean' element
    public DefinedBean(UniversalEIPNode endpoint) {
    	this();
    	setShortPropertyValue("ref", endpoint.getShortPropertyValue("ref"));
    	setShortPropertyValue("method", endpoint.getShortPropertyValue("method"));
    	setShortPropertyValue("beanType", endpoint.getShortPropertyValue("beanType"));
    	setShortPropertyValue("cache", endpoint.getShortPropertyValue("cache"));
    }
    
    /* (non-Javadoc)
     * @see org.fusesource.ide.camel.model.Endpoint#getCategoryName()
     */
    @Override
    public String getCategoryName() {
        return "Endpoints";
    }
}

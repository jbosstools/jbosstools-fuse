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
package org.jboss.tools.fuse.transformation.core;

/**
 * A Variable represents a key and value mapping, where the variable name is 
 * used as the source in a mapping and is replaced with the variable value.
 */
public interface Variable {

    /**
     * Get the value of this Variable.
     * 
     * @return value of the variable
     */
    public String getValue();
    
    /**
     * Set the variable value.
     * 
     * @param value variable value.
     */
    public void setValue(String value);
    
    /**
     * Get the name of this Variable.
     * 
     * @return name of the variable
     */
    public String getName();
    
    /**
     * Set the variable name.
     * 
     * @param name variable name
     */
    public void setName(String name);
}

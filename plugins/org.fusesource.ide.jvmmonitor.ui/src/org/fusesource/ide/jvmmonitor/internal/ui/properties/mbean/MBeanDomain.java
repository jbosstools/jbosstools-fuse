/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.util.HashMap;
import java.util.Map;

/**
 * The MBean domain.
 */
public class MBeanDomain {

    /** The domain name. */
    private String domainName;

    /** The MBean types. */
    private Map<String, MBeanType> types;

    /**
     * The constructor.
     * 
     * @param domainName
     *            The domain name
     */
    public MBeanDomain(String domainName) {
        this.domainName = domainName;
        types = new HashMap<String, MBeanType>();
    }

    /**
     * Gets the MBean type.
     * 
     * @param typeName
     *            The type name
     * @return The MBean type
     */
    public MBeanType getMBeanType(String typeName) {
        return types.get(typeName);
    }

    /**
     * Gets the MBean type.
     * 
     * @return The MBean type
     */
    public MBeanType[] getMBeanTypes() {
        return types.values().toArray(new MBeanType[0]);
    }

    /**
     * Puts the MBean type.
     * 
     * @param typeName
     *            The MBean type name
     * @param type
     *            The MBean type
     */
    public void putMBeanType(String typeName, MBeanType type) {
        types.put(typeName, type);
    }

    /**
     * Gets the domain name.
     * 
     * @return The domain name
     */
    public String getDomainName() {
        return domainName;
    }
}
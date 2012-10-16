/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import javax.management.ObjectName;

import org.eclipse.swt.graphics.RGB;

/**
 * The MBean attribute.
 */
public class MBeanAttribute {

    /** The object name. */
    private ObjectName objectName;

    /** The attribute name. */
    private String attributeName;

    /** The RGB. */
    private RGB rgb;

    /**
     * The constructor.
     * 
     * @param objectName
     *            The object name
     * @param attributeName
     *            The attribute name
     * @param rgb
     *            The RGB
     */
    public MBeanAttribute(ObjectName objectName, String attributeName, RGB rgb) {
        this.objectName = objectName;
        this.attributeName = attributeName;
        this.rgb = rgb;
    }

    /**
     * Gets the object name
     * 
     * @return the object name
     */
    public ObjectName getObjectName() {
        return objectName;
    }

    /**
     * Gets the attribute name
     * 
     * @return the attribute name
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Gets the RGB.
     * 
     * @return the RGB
     */
    public RGB getRgb() {
        return rgb;
    }

    /**
     * Sets the RGB.
     * 
     * @param rgb
     *            the RGB
     */
    protected void setRgb(RGB rgb) {
        this.rgb = rgb;
    }
}
/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.mbean;

import java.util.Date;
import java.util.List;

import javax.management.ObjectName;

/**
 * The monitored MXBean attribute.
 */
public interface IMonitoredMXBeanAttribute {

    /**
     * Gets the object name.
     * 
     * @return The object name
     */
    ObjectName getObjectName();

    /**
     * Gets the attribute name.
     * 
     * @return The attribute name
     */
    String getAttributeName();

    /**
     * Gets the RGB.
     * 
     * @return The RGB
     */
    int[] getRGB();

    /**
     * Gets the dates.
     * 
     * @return The dates
     */
    List<Date> getDates();

    /**
     * Gets the values.
     * 
     * @return The values
     */
    List<Number> getValues();

    /**
     * Sets the RGB.
     * 
     * @param r
     *            The red component
     * @param g
     *            The greed component
     * @param b
     *            The blue component
     */
    void setRGB(int r, int g, int b);

    /**
     * Clears the data.
     */
    void clear();
}
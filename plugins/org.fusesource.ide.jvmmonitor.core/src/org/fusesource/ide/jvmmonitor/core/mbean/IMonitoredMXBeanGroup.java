/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.mbean;

import java.util.List;

import javax.management.ObjectName;

import org.fusesource.ide.jvmmonitor.core.JvmCoreException;


/**
 * The monitored MXBean Group.
 */
public interface IMonitoredMXBeanGroup {

    /**
     * Gets the group name.
     * 
     * @return The group name
     */
    String getName();

    /**
     * Sets the group name.
     * 
     * @param name
     *            The group name
     */
    void setName(String name);

    /**
     * Gets the axis unit.
     * 
     * @return The axis unit
     */
    AxisUnit getAxisUnit();

    /**
     * Sets the axis unit.
     * 
     * @param axisUnit
     *            The axis unit
     */
    void setAxisUnit(AxisUnit axisUnit);

    /**
     * Gets the attributes.
     * 
     * @return The attributes
     */
    List<IMonitoredMXBeanAttribute> getAttributes();

    /**
     * Gets the monitored attribute.
     * 
     * @param objectName
     *            The object name
     * @param attributeName
     *            The attribute name
     * @return The monitored attribute
     */
    IMonitoredMXBeanAttribute getAttribute(ObjectName objectName,
            String attributeName);

    /**
     * Adds the monitored attribute.
     * 
     * @param objectName
     *            The object name
     * @param attributeName
     *            The attribute name
     * @param rgb
     *            The RGB
     * @throws JvmCoreException
     */
    void addAttribute(String objectName, String attributeName, int[] rgb)
            throws JvmCoreException;

    /**
     * Removes the monitored attribute.
     * 
     * @param objectName
     *            The object name
     * @param attributeName
     *            The attribute name
     */
    void removeAttribute(String objectName, String attributeName);

    /**
     * Clears the monitored attributes.
     */
    void clearAttributes();

    /** The axis unit. */
    public enum AxisUnit {

        /** The megabytes. */
        MBytes,

        /** The percent. */
        Percent,

        /** The count. */
        Count,

        /** The no unit. */
        None;
    }
}

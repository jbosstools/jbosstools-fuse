/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import java.util.ArrayList;
import java.util.List;

import javax.management.ObjectName;

import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanAttribute;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup;
import org.fusesource.ide.jvmmonitor.core.mbean.MBeanServerEvent;
import org.fusesource.ide.jvmmonitor.core.mbean.MBeanServerEvent.MBeanServerState;


/**
 * The monitored MXBean Group.
 */
public class MonitoredMXBeanGroup implements IMonitoredMXBeanGroup {

    /** The MBean server. */
    private MBeanServer mBeanServer;

    /** The group name. */
    private String name;

    /** The axis unit. */
    private AxisUnit axisUnit;

    /** The attributes. */
    private List<IMonitoredMXBeanAttribute> attributes;

    /**
     * The constructor.
     * 
     * @param mBeanServer
     *            The MBean server
     * @param name
     *            The group name
     * @param axisUnit
     *            The axis unit
     */
    public MonitoredMXBeanGroup(MBeanServer mBeanServer, String name,
            AxisUnit axisUnit) {
        this.mBeanServer = mBeanServer;
        this.name = name;
        this.axisUnit = axisUnit;
        attributes = new ArrayList<IMonitoredMXBeanAttribute>();
    }

    /*
     * @see IMonitoredMXBeanGroup#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * @see IMonitoredMXBeanGroup#setName(String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /*
     * @see IMonitoredMXBeanGroup#getAxisUnit()
     */
    @Override
    public AxisUnit getAxisUnit() {
        return axisUnit;
    }

    /*
     * @see IMonitoredMXBeanGroup#setAxisUnit(IMonitoredMXBeanGroup.AxisUnit)
     */
    @Override
    public void setAxisUnit(AxisUnit axisUnit) {
        this.axisUnit = axisUnit;
    }

    /*
     * @see IMonitoredMXBeanGroup#getAttributes()
     */
    @Override
    public List<IMonitoredMXBeanAttribute> getAttributes() {
        return attributes;
    }

    /*
     * @see IMonitoredMXBeanGroup#getAttribute(ObjectName, String)
     */
    @Override
    public IMonitoredMXBeanAttribute getAttribute(ObjectName objectName,
            String attributeName) {
        for (IMonitoredMXBeanAttribute attribute : attributes) {
            if (attribute.getObjectName().equals(objectName)
                    && attribute.getAttributeName().equals(attributeName)) {
                return attribute;
            }
        }
        return null;
    }

    /*
     * @see IMonitoredMXBeanGroup#addAttribute(String, String, int[])
     */
    @Override
    public void addAttribute(String objectNameString, String attributeName,
            int[] rgb) throws JvmCoreException {
        for (IMonitoredMXBeanAttribute attribute : attributes) {
            if (attribute.getAttributeName().equals(attributeName)
                    && attribute.getObjectName().getCanonicalName()
                            .equals(objectNameString)) {
                return;
            }
        }

        addAttribute(objectNameString, attributeName, rgb, true);
    }

    /*
     * @see IMonitoredMXBeanGroup#removeAttribute(String, String)
     */
    @Override
    public void removeAttribute(String objectName, String attributeName) {
        IMonitoredMXBeanAttribute targetAttribute = null;
        for (IMonitoredMXBeanAttribute attribute : attributes) {
            if (attribute.getObjectName().getCanonicalName().equals(objectName)
                    && attribute.getAttributeName().equals(attributeName)) {
                targetAttribute = attribute;
                break;
            }
        }

        if (targetAttribute != null) {
            attributes.remove(targetAttribute);
            mBeanServer
                    .fireMBeanServerChangeEvent(new MBeanServerEvent(
                            MBeanServerState.MonitoredAttributeRemoved,
                            targetAttribute));
        }
    }

    /*
     * @see IMonitoredMXBeanGroup#clearAttributes()
     */
    @Override
    public void clearAttributes() {
        for (IMonitoredMXBeanAttribute attribute : attributes) {
            attribute.clear();
        }
    }

    /**
     * Adds the attribute.
     * 
     * @param objectNameString
     *            The object name
     * @param attributeName
     *            The attribute name
     * @param rgb
     *            The RGB
     * @param fireEvent
     *            True to fire event
     * @throws JvmCoreException
     */
    protected void addAttribute(String objectNameString, String attributeName,
            int[] rgb, boolean fireEvent) throws JvmCoreException {
        ObjectName objectName = mBeanServer.getObjectName(objectNameString);
        MonitoredMXBeanAttribute attribute = new MonitoredMXBeanAttribute(
                objectName, attributeName, rgb);
        attributes.add(attribute);

        if (fireEvent) {
            mBeanServer.fireMBeanServerChangeEvent(new MBeanServerEvent(
                    MBeanServerState.MonitoredAttributeAdded, attribute));
        }
    }
}

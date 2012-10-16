/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.mbean;

import java.util.List;
import java.util.Set;

import javax.management.Attribute;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.ide.jvmmonitor.core.IHeapElement;
import org.fusesource.ide.jvmmonitor.core.IThreadElement;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.core.mbean.IMonitoredMXBeanGroup.AxisUnit;

/**
 * The MBean server wrapping <tt>MBeanServerConnection</tt>.
 */
public interface IMBeanServer {

    /**
     * Queries the names.
     * 
     * @param objectName
     *            The object name, or <tt>null</tt> to get all names
     * @return The set of object names
     * @throws JvmCoreException
     */
    Set<ObjectName> queryNames(ObjectName objectName) throws JvmCoreException;

    /**
     * Gets the attribute.
     * 
     * @param objectName
     *            The object name
     * @param attributeName
     *            The attribute name
     * @return The attribute, or <tt>null</tt> if not connected
     * @throws JvmCoreException
     */
    Object getAttribute(ObjectName objectName, String attributeName)
            throws JvmCoreException;

    /**
     * Sets the attribute.
     * 
     * @param objectName
     *            The object name
     * @param attribute
     *            The attribute
     * @throws JvmCoreException
     */
    void setAttribute(ObjectName objectName, Attribute attribute)
            throws JvmCoreException;

    /**
     * Gets the monitored attribute groups.
     * 
     * @return The monitored attribute groups
     */
    List<IMonitoredMXBeanGroup> getMonitoredAttributeGroups();

    /**
     * Adds the monitored attribute group.
     * 
     * @param name
     *            The attribute group name
     * @param axisUnit
     *            The axis unit
     * @return The added attribute group
     */
    IMonitoredMXBeanGroup addMonitoredAttributeGroup(String name,
            AxisUnit axisUnit);

    /**
     * Removes the monitored attribute group.
     * 
     * @param name
     *            The monitored attribute group name
     */
    void removeMonitoredAttributeGroup(String name);

    /**
     * Gets the MBean info.
     * 
     * @param objectName
     *            The object name
     * @return The MBean info
     * @throws JvmCoreException
     */
    MBeanInfo getMBeanInfo(ObjectName objectName) throws JvmCoreException;

    /**
     * Gets the MBean notification.
     * 
     * @return The MBean notification
     */
    IMBeanNotification getMBeanNotification();

    /**
     * Runs the garbage collector.
     * 
     * @throws JvmCoreException
     */
    void runGarbageCollector() throws JvmCoreException;

    /**
     * Invokes the method.
     * 
     * @param objectName
     *            The object name
     * @param method
     *            The method name
     * @param params
     *            the parameters, or <tt>null</tt> if having no arguments
     * @param signatures
     *            The signatures, or <tt>null</tt> if having no arguments
     * @return The return value, or <tt>null</tt> if not connected
     * @throws JvmCoreException
     */
    Object invoke(ObjectName objectName, String method, Object[] params,
            String[] signatures) throws JvmCoreException;

    /**
     * Gets the thread names.
     * 
     * @return The thread names
     * @throws JvmCoreException
     */
    String[] getThreadNames() throws JvmCoreException;

    /**
     * Gets the thread cache in JVM model without accessing to target JVM. To
     * get the up-to-dated thread data, {@link #refreshThreadCache()} has to be
     * invoked.
     * 
     * @return The thread cache
     */
    IThreadElement[] getThreadCache();

    /**
     * Refreshes the thread cache in JVM model accessing to target JVM.
     * 
     * @throws JvmCoreException
     */
    void refreshThreadCache() throws JvmCoreException;

    /**
     * Gets the heap cache in JVM model without accessing to target JVM. To get
     * the up-to-dated heap data, {@link #refreshHeapCache()} has to be invoked.
     * 
     * @return The heap cache
     */
    IHeapElement[] getHeapCache();

    /**
     * Refreshes the heap cache in JVM model accessing to target JVM.
     * 
     * @throws JvmCoreException
     */
    void refreshHeapCache() throws JvmCoreException;

    /**
     * Clears the heap delta.
     */
    void clearHeapDelta();

    /**
     * Dumps the heap data as hprof file.
     * 
     * @param hprofFileName
     *            The hprof file name used when monitoring JVM on remote host
     * @param transfer
     *            <tt>true</tt> to transfer hprof file to local host
     * @param monitor
     *            The progress monitor
     * @return The file store, or <tt>null</tt> if target JVM is running on
     *         remote host and hprof file is not transfered to local host
     * @throws JvmCoreException
     */
    IFileStore dumpHprof(String hprofFileName, boolean transfer,
            IProgressMonitor monitor) throws JvmCoreException;

    /**
     * Dumps the heap data.
     * 
     * @return The file store
     * @throws JvmCoreException
     */
    IFileStore dumpHeap() throws JvmCoreException;

    /**
     * Dumps the threads.
     * 
     * @return The file store
     * @throws JvmCoreException
     */
    IFileStore dumpThreads() throws JvmCoreException;

    /**
     * Sets the update period.
     * 
     * @param updatePeriod
     *            The update period
     */
    void setUpdatePeriod(Integer updatePeriod);

    /**
     * Adds the MBean server change listener.
     * 
     * @param listener
     *            The MBean server change listener
     */
    void addServerChangeListener(IMBeanServerChangeListener listener);

    /**
     * removes the MBean server change listener.
     * 
     * @param listener
     *            The MBean server change listener
     */
    void removeServerChangeListener(IMBeanServerChangeListener listener);

    JMXServiceURL getJmxUrl();

    MBeanServerConnection getConnection();

	JMXConnector getConnector();
}

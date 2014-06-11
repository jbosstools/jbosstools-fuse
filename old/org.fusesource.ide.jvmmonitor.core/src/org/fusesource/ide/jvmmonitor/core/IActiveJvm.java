/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

import org.fusesource.ide.jvmmonitor.core.cpu.ICpuProfiler;
import org.fusesource.ide.jvmmonitor.core.mbean.IMBeanServer;

/**
 * The active JVM that means the JVM is running. If JVM is terminated, it
 * becomes {@link ITerminatedJvm}.
 */
public interface IActiveJvm extends IJvm {

    /**
     * Connects the target JVM with JVM Monitor via JMX. The client can check
     * with {@link #isConnectionSupported()} whether the target JVM supports
     * connection.
     * 
     * @param updatePeriod
     *            The update period
     * @throws JvmCoreException
     *             if connecting JVM fails
     */
    void connect(int updatePeriod) throws JvmCoreException;

    /**
     * Disconnects the target JVM from JVM Monitor.
     */
    void disconnect();

    /**
     * Gets the state indicating if the target JVM is connected with JVM
     * Monitor.
     * 
     * @return true if the target JVM is connected with JVM Monitor
     */
    boolean isConnected();

    /**
     * Gets the state indicating if the target JVM supports the connection.
     * 
     * @return true if the target JVM supports the connection
     */
    boolean isConnectionSupported();

    /**
     * Gets the error state message.
     * 
     * @return The error state message
     */
    String getErrorStateMessage();

    /**
     * Gets the state indicating if the target JVM is running on remote host.
     * 
     * @return true if the target JVM is running on remote host
     */
    boolean isRemote();

    /**
     * Gets the CPU profiler.
     * 
     * @return The CPU profiler
     */
    ICpuProfiler getCpuProfiler();

    /**
     * Gets the SWT resource monitor.
     * 
     * @return The SWT resource monitor
     */
    ISWTResourceMonitor getSWTResourceMonitor();

    /**
     * Gets the MBean server.
     * 
     * @return The MBean server
     */
    IMBeanServer getMBeanServer();
}

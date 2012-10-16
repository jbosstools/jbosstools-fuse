/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

import java.util.List;

/**
 * The host where the target JVM is running.
 */
public interface IHost {

    /** The local host name. */
    static final String LOCALHOST = "localhost"; //$NON-NLS-1$

    /**
     * Gets the host name.
     * 
     * @return The host name
     */
    String getName();

    /**
     * Gets the list of JVMs including not only active JVMs but also terminated
     * JVMs.
     * 
     * @return The list of JVMs
     */
    List<IJvm> getJvms();

    /**
     * Gets the list of terminated JVMs.
     * 
     * @return The list of terminated JVMs
     */
    List<ITerminatedJvm> getTerminatedJvms();

    /**
     * Gets the list of active JVMs.
     * 
     * @return The list of active JVMs
     */
    List<IActiveJvm> getActiveJvms();

    /**
     * Adds the remote active JVM.
     * 
     * @param port
     *            The port
     * @param userName
     *            The user name
     * @param password
     *            The password
     * @param updatePeriod
     *            The update period
     * @return The active JVM
     * @throws JvmCoreException
     */
    IActiveJvm addRemoteActiveJvm(int port, String userName, String password,
            int updatePeriod) throws JvmCoreException;

    /**
     * Adds the local active JVM.
     * 
     * @param pid
     *            The process id of JVM.
     * @param mainClass
     *            The main class
     * @param url
     *            The JMX URL, or <tt>null</tt> if target JVM doesn't support
     *            attach
     * @param errorStateMessage
     *            The error state message
     * @return The active JVM
     * @throws JvmCoreException
     */
    IActiveJvm addLocalActiveJvm(int pid, String mainClass, String url,
            String errorStateMessage) throws JvmCoreException;

    /**
     * Removes the JVM.
     * 
     * @param pid
     *            The process ID of JVM.
     */
    void removeJvm(int pid);

    /**
     * Return <tt>true</tt> if this is local host.
     * 
     * @return True if this is local host.
     */
    boolean isLocalHost();
}

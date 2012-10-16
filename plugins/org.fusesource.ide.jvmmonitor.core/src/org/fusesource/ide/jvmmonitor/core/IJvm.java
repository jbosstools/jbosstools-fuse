/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

import java.util.List;

/**
 * The JVM that has common interface for both active JVM and terminated JVM.
 */
public interface IJvm {

    /** The suffix for internal directory to store snapshots. */
    static final String DIR_SUFFIX = ".jvm"; //$NON-NLS-1$

    /** The properties file that are created at internal directory. */
    static final String PROPERTIES_FILE = "properties.xml"; //$NON-NLS-1$

    /** The property key for PID used in <tt>properties.xml</tt>. */
    static final String PID_PROP_KEY = "Pid"; //$NON-NLS-1$

    /** The property key for port used in <tt>properties.xml</tt>. */
    static final String PORT_PROP_KEY = "Port"; //$NON-NLS-1$

    /** The property key for main class used in <tt>properties.xml</tt>. */
    static final String MAIN_CLASS_PROP_KEY = "MainClass"; //$NON-NLS-1$

    /** The property key for host used in <tt>properties.xml</tt>. */
    static final String HOST_PROP_KEY = "Host"; //$NON-NLS-1$

    /**
     * Gets the process ID.
     * 
     * @return The process ID, or <tt>-1</tt> if not specified
     */
    int getPid();

    /**
     * Gets the port.
     * 
     * @return The port, or <tt>-1</tt> if not specified
     */
    int getPort();

    /**
     * Gets the main class name.
     * 
     * @return The main class name
     */
    String getMainClass();

    /**
     * Gets the host.
     * 
     * @return the host.
     */
    IHost getHost();

    /**
     * Gets the list of snapshots.
     * 
     * @return The list of snapshots
     */
    List<ISnapshot> getShapshots();

    /**
     * Deletes the given snapshot.
     * 
     * @param snapshot
     *            The snapshot
     */
    void deleteSnapshot(ISnapshot snapshot);
}

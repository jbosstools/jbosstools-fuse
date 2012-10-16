/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

import java.lang.Thread.State;

/**
 * The thread element. The client gets the threads as an array of thread
 * elements.
 * 
 * @see java.lang.management.ThreadInfo
 */
public interface IThreadElement extends IStackTraceProvider {

    /**
     * Gets the thread name.
     * 
     * @return The thread name
     */
    String getThreadName();

    /**
     * Gets the thread state.
     * 
     * @return The thread state
     */
    State getThreadState();

    /**
     * Gets the blocked time.
     * 
     * @return The blocked time
     */
    long getBlockedTime();

    /**
     * Gets the blocked count
     * 
     * @return The blocked count
     */
    long getBlockedCount();

    /**
     * Gets the waited time
     * 
     * @return The waited time
     */
    long getWaitedTime();

    /**
     * Gets the waited count.
     * 
     * @return The waited count
     */
    long getWaitedCount();

    /**
     * Gets the lock name.
     * 
     * @return The lock name
     */
    String getLockName();

    /**
     * Gets the lock owner name.
     * 
     * @return The lock owner name
     */
    String getLockOwnerName();

    /**
     * Gets the state if the thread is suspended.
     * 
     * @return True if thread is suspended
     */
    boolean isSuspended();

    /**
     * Gets the state indicating if the thread is deadlocked.
     * 
     * @return True if the thread is deadlocked
     */
    boolean isDeadlocked();

    /**
     * Gets the CPU usage in percentage.
     * 
     * @return The CPU usage in percentage
     */
    double getCpuUsage();
}
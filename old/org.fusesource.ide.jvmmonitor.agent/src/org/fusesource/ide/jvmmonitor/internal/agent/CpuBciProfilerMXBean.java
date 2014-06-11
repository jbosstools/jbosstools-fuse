/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import javax.management.MXBean;

/**
 * The MXBean to control CPU BCI (bytecode instrumentation) profiler.
 */
@SuppressWarnings("nls")
@MXBean
public interface CpuBciProfilerMXBean {

    /** The MXBean name. */
    final static String PROFILER_MXBEAN_NAME = "org.fusesource.ide.jvmmonitor:type=CPU BCI Profiler";

    /**
     * Transforms the classes to prepare profiling or clean up.
     */
    void transformClasses();

    /**
     * Gets the status of transforming classes.
     * 
     * @return The status of transforming classes
     */
    TransformStatusCompositeData getTransformStatus();

    /**
     * Interrupts transforming classes.
     */
    void interruptTransform();

    /**
     * Sets the state indicating if profiler is running.
     * 
     * @param run
     *            True to run profiler
     */
    void setRunning(boolean run);

    /**
     * Gets the state indicating if profiler is running.
     * 
     * @return <tt>true</tt> if profiler is running
     */
    boolean isRunning();

    /**
     * Clears the profile data.
     */
    void clear();

    /**
     * Dumps the profile data to file.
     */
    void dumpToFile();

    /**
     * Dumps the profile data.
     * 
     * @return The profile data
     */
    String dump();

    /**
     * Gets the directory where dump file is created.
     * 
     * @return The directory where dump file is created
     */
    String getDumpDir();

    /**
     * Sets the directory where dump file is created.
     * 
     * @param path
     *            The directory where dump file is created
     */
    void setDumpDir(String path);

    /**
     * Sets the filter.
     * 
     * @param key
     *            The key that can be <tt>jvmmonitor.ignored.packages</tt>,
     *            <tt>jvmmonitor.profiled.packages</tt> or
     *            <tt>jvmmonitor.profiled.classloaders</tt>
     * @param values
     *            The values separated by comma
     */
    void setFilter(String key, String values);

    /**
     * Gets the profiled packages.
     * 
     * @return The profiled packages
     */
    String[] getProfiledPackages();

    /**
     * Gets the ignored packages.
     * 
     * @return The ignored packages
     */
    String[] getIgnoredPackages();

    /**
     * Gets the profiled class loaders.
     * 
     * @return The profiled class loaders
     */
    String[] getProfiledClassloaders();

    /**
     * Gets the version.
     * 
     * @return The version
     */
    String getVersion();
}
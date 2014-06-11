/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.cpu;

import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;

/**
 * The CPU profiler.
 */
public interface ICpuProfiler {

    /**
     * Sets the profiler type.
     * 
     * @param type
     *            The profiler type
     */
    void setProfilerType(ProfilerType type);

    /**
     * Gets the profiler type.
     * 
     * @return The profiler type
     */
    ProfilerType getProfilerType();

    /**
     * Transforms the classes to prepare CPU BCI profiling or clean up.
     * 
     * @param monitor
     *            The progress monitor
     * @throws JvmCoreException
     * @throws InterruptedException
     */
    void transformClasses(IProgressMonitor monitor) throws JvmCoreException,
            InterruptedException;

    /**
     * Resumes the CPU profiling.
     * 
     * @throws JvmCoreException
     */
    void resume() throws JvmCoreException;

    /**
     * Suspends the CPU profiling.
     * 
     * @throws JvmCoreException
     */
    void suspend() throws JvmCoreException;

    /**
     * Clears the CPU profiling data.
     * 
     * @throws JvmCoreException
     */
    void clear() throws JvmCoreException;

    /**
     * Dumps the CPU profiling data.
     * 
     * @return The file store
     * @throws JvmCoreException
     */
    IFileStore dump() throws JvmCoreException;

    /**
     * Gets the CPU model.
     * 
     * @return The CPU model
     */
    ICpuModel getCpuModel();

    /**
     * Refreshes the BCI profiling data cache in JVM model accessing to target
     * JVM.
     * 
     * @param monitor
     *            The progress monitor
     * @throws JvmCoreException
     */
    void refreshBciProfileCache(IProgressMonitor monitor)
            throws JvmCoreException;

    /**
     * Sets the profiled packages.
     * 
     * @param packages
     *            The profiled packages
     * @throws JvmCoreException
     */
    void setProfiledPackages(Set<String> packages) throws JvmCoreException;

    /**
     * Gets the profiled packages.
     * 
     * @return The profiled packages
     * @throws JvmCoreException
     */
    Set<String> getProfiledPackages() throws JvmCoreException;

    /**
     * Gets the profiler state of given profiler type.
     * 
     * @param type
     *            The profiler type
     * @return The state
     */
    ProfilerState getState(ProfilerType type);

    /**
     * Gets the profiler state of currently selected profiler.
     * 
     * @return The profiler state
     */
    ProfilerState getState();

    /**
     * Gets the sampling period.
     * 
     * @return The sampling period
     */
    Integer getSamplingPeriod();

    /**
     * Sets the sampling period.
     * 
     * @param samplingPeriod
     *            The sampling period
     */
    void setSamplingPeriod(Integer samplingPeriod);

    /**
     * The profiler state.
     */
    enum ProfilerState {

        /** The unknown state. */
        UNKNOWN,

        /** The agent is not loaded. */
        AGENT_NOT_LOADED,

        /** The invalid version. */
        INVALID_VERSION,

        /** The profiler is ready. */
        READY,

        /** The profiler is running. */
        RUNNING
    }

    /**
     * The profiler type.
     */
    enum ProfilerType {

        /** The profiler with sampling. */
        SAMPLING,

        /** The profiler with bytecode instrumentation. */
        BCI
    }
}

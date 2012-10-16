/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

/**
 * The JVM model event.
 */
public class JvmModelEvent {

    /** The JVM model change state. */
    public final State state;

    /** The JVM. */
    public final IJvm jvm;

    /** The source object where event occurred. */
    public final Object source;

    /**
     * The constructor.
     * 
     * @param state
     *            The JVM model change state
     * @param jvm
     *            The JVM
     */
    public JvmModelEvent(State state, IJvm jvm) {
        this(state, jvm, null);
    }

    /**
     * The constructor.
     * 
     * @param state
     *            The JVM model change state
     * @param jvm
     *            The JVM
     * @param source
     *            The source object in which event occurred
     */
    public JvmModelEvent(State state, IJvm jvm, Object source) {
        this.state = state;
        this.jvm = jvm;
        this.source = source;
    }

    /** The JVM model change state. */
    public enum State {

        /** The host has been added. */
        HostAdded,

        /** The host has been removed. */
        HostRemoved,

        /** The JVM has been added. */
        JvmAdded,

        /** The JVM has been removed. */
        JvmRemoved,

        /** The JVM has been connected. */
        JvmConnected,

        /** The JVM has been disconnected. */
        JvmDisconnected,

        /** The JVM has been modified. */
        JvmModified,

        /** The snapshot has been taken. */
        ShapshotTaken,

        /** The snapshot has been removed. */
        ShapshotRemoved,

        /** The CPU profiler configuration has been changed. */
        CpuProfilerConfigChanged;
    }
}

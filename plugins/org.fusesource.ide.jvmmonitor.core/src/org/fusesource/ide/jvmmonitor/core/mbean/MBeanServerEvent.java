/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.mbean;

/**
 * The MBean server event.
 */
public class MBeanServerEvent {

    /** The MBean server change state. */
    public final MBeanServerState state;

    /** The source. */
    public final Object source;

    /**
     * The constructor.
     * 
     * @param state
     *            The MBean server state
     * @param source
     *            The source
     */
    public MBeanServerEvent(MBeanServerState state, Object source) {
        this.state = state;
        this.source = source;
    }

    /** The MBean server change state. */
    public enum MBeanServerState {

        /** The monitored attribute has been added. */
        MonitoredAttributeAdded,

        /** The monitored attribute has been removed. */
        MonitoredAttributeRemoved,

        /** The monitored attribute group has been added. */
        MonitoredAttributeGroupAdded,

        /** The monitored attribute group has been removed. */
        MonitoredAttributeGroupRemoved,
    }
}

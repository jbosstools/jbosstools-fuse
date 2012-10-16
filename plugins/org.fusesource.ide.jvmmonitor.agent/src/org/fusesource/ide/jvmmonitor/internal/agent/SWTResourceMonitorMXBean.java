/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import javax.management.MXBean;

/**
 * The MXBean to monitor SWT resources.
 */
@SuppressWarnings("nls")
@MXBean
public interface SWTResourceMonitorMXBean {

    /** The SWT resource monitor MXBean name. */
    final static String SWT_RESOURCE_MONITOR_MXBEAN_NAME = "org.fusesource.ide.jvmmonitor:type=SWT Resource Monitor";

    /**
     * Sets the tracking state.
     * 
     * @param tracking
     *            <tt>true</tt> to enable tracking
     */
    void setTracking(boolean tracking);

    /**
     * Gets the tracking state.
     * 
     * @return <tt>true</tt> if tracking is enabled
     */
    boolean isTracking();

    /**
     * Gets the resources.
     * 
     * @return The resources
     */
    SWTResourceCompositeData[] getResources();
    
    /**
     * Clears the tracked resources.
     */
    void clear();
}

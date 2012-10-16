/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

/**
 * The SWT resource monitor.
 */
public interface ISWTResourceMonitor {

    /**
     * Sets the tracking state.
     * 
     * @param tracking
     *            <tt>true</tt> to enable tracking
     * @throws JvmCoreException
     */
    void setTracking(boolean tracking) throws JvmCoreException;

    /**
     * Gets the tracking state.
     * 
     * @return <tt>true</tt> if tracking is enabled
     * @throws JvmCoreException
     */
    boolean isTracking() throws JvmCoreException;

    /**
     * Refreshes the resources cache.
     * 
     * @throws JvmCoreException
     */
    void refreshResourcesCache() throws JvmCoreException;

    /**
     * Gets the resources.
     * 
     * @return The resources
     */
    ISWTResourceElement[] getResources();

    /**
     * Clears the tracked resources.
     * 
     * @throws JvmCoreException
     */
    void clear() throws JvmCoreException;

    /**
     * Gets the state indicating if SWT resource monitor is supported.
     * 
     * @return <tt>true</tt> if SWT resource monitor is supported
     */
    boolean isSupported();
}

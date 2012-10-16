/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.mbean;

/**
 * The MBean server change listener.
 */
public interface IMBeanServerChangeListener {

    /**
     * Notifies that MBean server has been changed.
     * 
     * @param event
     *            The MBean server change event
     */
    void serverChanged(MBeanServerEvent event);
}
/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.cpu;

/**
 * The CPU model change listener.
 */
public interface ICpuModelChangeListener {

    /**
     * Notifies that the CPU model has been changed.
     * 
     * @param event
     *            The CPU model change event
     */
    void modelChanged(CpuModelEvent event);
}

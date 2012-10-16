/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core.cpu;

/**
 * The CPU model event.
 */
public class CpuModelEvent {

    /** The CPU model change state. */
    public final CpuModelState state;

    /**
     * The constructor.
     * 
     * @param state
     *            The CPU model change state
     */
    public CpuModelEvent(CpuModelState state) {
        this.state = state;
    }

    /** 
     * The CPU model change state. 
     */
    public enum CpuModelState {
    
        /** The CPU model has been changed. */
        CpuModelChanged,
    
        /** The focused method has been changed. */
        FocusedMethodChanged,
    
        /** The callers/callees target has been changed. */
        CallersCalleesTargetChanged;
    }
}

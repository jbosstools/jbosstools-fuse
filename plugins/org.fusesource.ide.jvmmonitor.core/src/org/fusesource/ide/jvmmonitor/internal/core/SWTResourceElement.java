/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import org.fusesource.ide.jvmmonitor.core.ISWTResourceElement;

/**
 * The SWT resource element.
 */
public class SWTResourceElement implements ISWTResourceElement {

    /** The SWT resource name. */
    private String name;

    /** The stack trace elements. */
    private StackTraceElement[] stackTraceElements;

    /**
     * The constructor.
     * 
     * @param name
     *            The SWT resource name
     * @param stackTraceElements
     *            The stack trace elements
     */
    public SWTResourceElement(String name,
            StackTraceElement[] stackTraceElements) {
        this.name = name;
        this.stackTraceElements = stackTraceElements;
    }

    /*
     * @see ISWTResourceElement#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * @see ISWTResourceElement#getStackTraceElements()
     */
    @Override
    public StackTraceElement[] getStackTraceElements() {
        return stackTraceElements;
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}

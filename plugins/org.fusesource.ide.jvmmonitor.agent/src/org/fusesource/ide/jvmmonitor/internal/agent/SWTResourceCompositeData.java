/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.beans.ConstructorProperties;
import java.util.List;

/**
 * The SWT resource that is converted into <tt>CompositeData</tt>.
 */
public class SWTResourceCompositeData {

    /** The resource name that is given by Resource.toString(). */
    private String name;

    /** The stack traces to show how the resource was created. */
    private List<StackTraceElementCompositeData> stackTrace;

    /**
     * The constructor.
     * 
     * @param name
     *            The resource name
     * @param stackTrace
     *            The stack trace
     */
    @ConstructorProperties({ "name", "stackTrace" })
    public SWTResourceCompositeData(String name,
            List<StackTraceElementCompositeData> stackTrace) {
        this.name = name;
        this.stackTrace = stackTrace;
    }

    /**
     * Gets the resource name.
     * 
     * @return The resource name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the stack trace.
     * 
     * @return The stack trace
     */
    public List<StackTraceElementCompositeData> getStackTrace() {
        return stackTrace;
    }
}

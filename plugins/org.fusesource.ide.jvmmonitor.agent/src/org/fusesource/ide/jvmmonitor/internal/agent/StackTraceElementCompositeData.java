/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.agent;

import java.beans.ConstructorProperties;

/**
 * The wrapper for stack trace element to add the constructor properties
 * annotation, in order to be converted into <tt>CompositeData</tt>.
 */
public class StackTraceElementCompositeData {

    /** The stack trace element. */
    private StackTraceElement element;

    /**
     * The constructor.
     * 
     * @param element
     *            The stack trace element
     */
    @ConstructorProperties({ "className", "fileName", "lineNumber",
            "methodName", "nativeMethod" })
    public StackTraceElementCompositeData(StackTraceElement element) {
        this.element = element;
    }

    /**
     * Gets the class name.
     * 
     * @return The class name
     */
    public String getClassName() {
        return element.getClassName();
    }

    /**
     * Gets the file name.
     * 
     * @return The file name
     */
    public String getFileName() {
        return element.getFileName();
    }

    /**
     * Gets the line number.
     * 
     * @return The line number
     */
    public int getLineNumber() {
        return element.getLineNumber();
    }

    /**
     * Gets the method name.
     * 
     * @return The method name
     */
    public String getMethodName() {
        return element.getMethodName();
    }

    /**
     * Gets the native method.
     * 
     * @return The native method
     */
    public boolean getNativeMethod() {
        return element.isNativeMethod();
    }
}

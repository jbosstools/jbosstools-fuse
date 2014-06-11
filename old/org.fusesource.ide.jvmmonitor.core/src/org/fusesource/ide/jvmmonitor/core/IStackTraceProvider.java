/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

/**
 * The stack trace provider.
 */
public interface IStackTraceProvider {

    /**
     * Gets the stack trace elements.
     * 
     * @return The stack trace elements
     */
    StackTraceElement[] getStackTraceElements();
}

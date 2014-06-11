/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

/**
 * The heap element. The client gets the heap histogram as an array of heap
 * elements.
 */
public interface IHeapElement {

    /**
     * Gets the class name of instance.
     * 
     * @return The class name of instance
     */
    String getClassName();

    /**
     * Gets the memory size of instance.
     * 
     * @return The memory size of instance
     */
    long getSize();

    /**
     * Gets the count of instance.
     * 
     * @return The count of instance
     */
    long getCount();

    /**
     * Gets the base memory size.
     * 
     * @return The base memory size
     */
    long getBaseSize();
}
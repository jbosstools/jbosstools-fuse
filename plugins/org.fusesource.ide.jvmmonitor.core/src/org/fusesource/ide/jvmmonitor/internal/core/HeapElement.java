/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.core;

import org.fusesource.ide.jvmmonitor.core.IHeapElement;

/**
 * The heap element.
 */
public class HeapElement implements IHeapElement {

    /** The class name. */
    private String className;

    /** The total memory size of instances. */
    private long size;

    /** The total count of instances. */
    private long count;

    /** The base memory size to calculate the delta. */
    private long baseSize;

    /***
     * The constructor.
     * 
     * @param className
     *            The class name
     * @param size
     *            The total memory size of instances
     * @param count
     *            The total count of instances
     * @param baseSize
     *            The base size
     */
    public HeapElement(String className, long size, long count,
            long baseSize) {
        this.className = className;
        this.size = size;
        this.count = count;
        this.baseSize = baseSize;
    }

    /***
     * The constructor.
     * 
     * @param className
     *            The class name
     * @param size
     *            The total memory size of instances
     * @param count
     *            The total count of instances
     */
    public HeapElement(String className, long size, long count) {
        this(className, size, count, 0);
    }

    /*
     * @see IHeapListElement#getClassName()
     */
    @Override
    public String getClassName() {
        return className;
    }

    /*
     * @see IHeapListElement#getSize()
     */
    @Override
    public long getSize() {
        return size;
    }

    /*
     * @see IHeapListElement#getCount()
     */
    @Override
    public long getCount() {
        return count;
    }

    /*
     * @see IHeapListElement#getBaseSize()
     */
    @Override
    public long getBaseSize() {
        return baseSize;
    }


    /**
     * Resets the base memory size.
     */
    public void resetBaseSize() {
        baseSize = size;
    }

    /*
     * @see Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(className).append('\t');
        buffer.append(size).append('\t');
        buffer.append(count);
        return buffer.toString();
    }

    /**
     * Sets the size and count.
     * 
     * @param size
     *            The total memory size of instances
     * @param count
     *            The total count of instances
     */
    public void setSizeAndCount(long size, long count) {
        this.size = size;
        this.count = count;
    }

    /**
     * Dumps the heap data to given string buffer.
     * 
     * @param buffer
     *            The string buffer
     */
    public void dump(StringBuffer buffer) {
        buffer.append("\t<class "); //$NON-NLS-1$
        buffer.append("name=\"").append(className).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("size=\"").append(size).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("count=\"").append(count).append("\" "); //$NON-NLS-1$ //$NON-NLS-2$
        buffer.append("baseSize=\"").append(baseSize).append("\"/>\n"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}

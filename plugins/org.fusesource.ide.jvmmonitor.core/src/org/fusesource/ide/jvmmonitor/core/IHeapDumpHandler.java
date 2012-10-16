/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.core;

/**
 * The heap dump handler. A client plug-in that contributes to the extension
 * point <tt>org.fusesource.ide.jvmmonitor.core.heapDumpHandler</tt> will implement this
 * interface.
 */
public interface IHeapDumpHandler {

    /**
     * Dumps the heap histogram that contains instance count and size for each
     * class. The classes are sorted by instance size, and the number of classes
     * is restricted up to the number of classes given by
     * {@link #getMaxClassesNumber()}.
     * <p>
     * e.g.
     * 
     * <pre>
     *  num     #instances         #bytes  class name
     * ----------------------------------------------
     *    1:         18329        2104376  &lt;constMethodKlass&gt;
     *    2:         18329        1479904  &lt;methodKlass&gt;
     *    3:          2518        1051520  [B
     *    4:         11664         989856  [C
     *    5:         11547         277128  java.lang.String
     * </pre>
     * 
     * @param pid
     *            The process ID
     * @param isLive
     *            True to dump only live objects
     * @return The heap histogram string
     * @throws JvmCoreException
     */
    String dumpHeap(int pid, boolean isLive) throws JvmCoreException;

    /**
     * Gets the max classes number.
     * 
     * @return The max classes number
     */
    int getMaxClassesNumber();
}

/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.thread;

import org.eclipse.swt.SWT;

/**
 * The thread column.
 */
public enum ThreadColumn {

    /** The thread name. */
    THREAD(Messages.threadColumnLabel, 200, SWT.LEFT,
            Messages.threadColumnToolTip),

    /** The state. */
    STATE(Messages.stateColumnLabel, 130, SWT.LEFT, Messages.stateColumnToolTip),

    /** The CPU usage. */
    CPU(Messages.cpuColumnLabel, 70, SWT.LEFT, Messages.cpuColumnToolTip),

    /** The blocked time. */
    BLOCKED_TIME(Messages.blockedTimeColumnLabel, 100, SWT.LEFT,
            Messages.blockedTimeColumnToolTip),

    /** The blocked count. */
    BLOCKED_COUNT(Messages.blockedCountColumnLabel, 80, SWT.LEFT,
            Messages.blockedCountColumnToolTip),

    /** The waited time. */
    WAITED_TIME(Messages.waitedTimeColumnLabel, 100, SWT.LEFT,
            Messages.waitedTimeColumnToolTip),

    /** The waited count. */
    WAITED_COUNT(Messages.waitedCountColumnLabel, 80, SWT.LEFT,
            Messages.waitedCountColumnToolTip),

    /** The lock name. */
    LOCK(Messages.lockColumnLabel, 140, SWT.LEFT, Messages.lockColumnToolTip),

    /** The lock owner name. */
    LOCK_OWNWER(Messages.lockOwnerColumnLabel, 140, SWT.LEFT,
            Messages.lockOwnerColumnToolTip);

    /** The label. */
    public final String label;

    /** The default column width. */
    public final int defalutWidth;

    /** The initial alignment. */
    public final int initialAlignment;

    /** The tool tip. */
    public final String toolTip;

    /**
     * The constructor.
     * 
     * @param label
     *            the column label
     * @param defalutWidth
     *            the default column width
     * @param alignment
     *            the initial alignment
     * @param toolTip
     *            the tooltip text
     */
    private ThreadColumn(String label, int defalutWidth, int alignment,
            String toolTip) {
        this.label = label;
        this.defalutWidth = defalutWidth;
        this.initialAlignment = alignment;
        this.toolTip = toolTip;
    }

    /**
     * Gets the column with given column name.
     * 
     * @param columnName
     *            The column name
     * @return The column
     */
    protected static ThreadColumn getColumn(String columnName) {
        for (ThreadColumn column : ThreadColumn.values()) {
            if (columnName.equals(column.label)) {
                return column;
            }
        }
        throw new IllegalStateException();
    }
}

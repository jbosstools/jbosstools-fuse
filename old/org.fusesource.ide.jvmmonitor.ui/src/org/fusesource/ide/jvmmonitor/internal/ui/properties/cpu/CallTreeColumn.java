/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.swt.SWT;

/**
 * The call tree column.
 */
public enum CallTreeColumn {

    /** The call tree column. */
    CALL_TREE(Messages.callTreeColumnLabel, 560, SWT.LEFT,
            Messages.callTreeColumnToolTip),

    /** The method invocation time in milliseconds. */
    TIME_MS(Messages.timeInMsLabel, 100, SWT.LEFT, Messages.timeInMsToolTip),

    /** The method invocation time in percentage. */
    TIME_PERCENTAGE(Messages.timeInPercentageLabel, 100, SWT.LEFT,
            Messages.timeInPercentageToolTip),

    /** The method invocation time in milliseconds. */
    SELFTIME_MS(Messages.selfTimeInMsLabel, 100, SWT.LEFT,
            Messages.selfTimeInMsToolTip),

    /** The method invocation time in percentage. */
    SELFTIME_PERCENTAGE(Messages.selfTimeInPercentageLabel, 100, SWT.LEFT,
            Messages.selfTimeInPercentageToolTip),

    /** The method invocation count. */
    COUNT(Messages.countLabel, 70, SWT.LEFT, Messages.countToolTip);

    /** The label for series type. */
    public final String label;

    /** The default column width. */
    public final int defalutWidth;

    /** The alignment. */
    public final int alignment;

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
     *            the alignment
     * @param toolTip
     *            the tooltip text
     */
    private CallTreeColumn(String label, int defalutWidth, int alignment,
            String toolTip) {
        this.label = label;
        this.defalutWidth = defalutWidth;
        this.alignment = alignment;
        this.toolTip = toolTip;
    }

    /**
     * Gets the column corresponding to the given column name.
     * 
     * @param columnName
     *            The column name
     * @return The column
     */
    protected static CallTreeColumn getColumn(String columnName) {
        for (CallTreeColumn column : CallTreeColumn.values()) {
            if (column.label.equals(columnName)) {
                return column;
            }
        }
        throw new IllegalStateException();
    }
}

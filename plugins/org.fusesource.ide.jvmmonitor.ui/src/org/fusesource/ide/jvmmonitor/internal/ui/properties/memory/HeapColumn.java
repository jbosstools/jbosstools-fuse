/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.eclipse.swt.SWT;

/**
 * The heap column.
 */
public enum HeapColumn {

    /** The class name. */
    CLASS(Messages.classColumnLabel, 420, SWT.LEFT,
            Messages.classColumnToolTip),

    /** The size. */
    SIZE(Messages.sizeColumnLabel, 140, SWT.LEFT, Messages.sizeColumnToolTip),

    /** The count. */
    COUNT(Messages.countColumnLabel, 140, SWT.LEFT, Messages.countColumnToolTip),

    /** The delta. */
    DELTA(Messages.deltaColumnLabel, 140, SWT.LEFT, Messages.deltaColumnToolTip);

    /** The label. */
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
    private HeapColumn(String label, int defalutWidth, int alignment,
            String toolTip) {
        this.label = label;
        this.defalutWidth = defalutWidth;
        this.alignment = alignment;
        this.toolTip = toolTip;
    }
    
    /**
     * Gets the column with given column name.
     * 
     * @param columnName
     *            The column name
     * @return The column
     */
    public static HeapColumn getColumn(String columnName) {
        for (HeapColumn column : HeapColumn.values()) {
            if (columnName.equals(column.label)) {
                return column;
            }
        }
        throw new IllegalStateException();
    }
}

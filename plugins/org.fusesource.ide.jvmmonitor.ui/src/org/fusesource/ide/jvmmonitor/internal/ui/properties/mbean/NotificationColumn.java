/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import org.eclipse.swt.SWT;

/**
 * The thread column.
 */
public enum NotificationColumn {

    /** The message. */
    MESSAGE(Messages.messageColumnLabel, 300, SWT.LEFT, true,
            Messages.messageColumnToolTip),

    /** The date. */
    DATE(Messages.dateColumnLabel, 170, SWT.LEFT, true,
            Messages.dateColumnToolTip),

    /** The sequence number. */
    SEQUENCE_NUMBER(Messages.sequenceNumberColumnLabel, 70, SWT.LEFT, false,
            Messages.sequenceNumberColumnToolTip),

    /** The notification type. */
    TYPE(Messages.notificationTypeColumnLabel, 150, SWT.LEFT, false,
            Messages.notificationTypeColumnToolTip);

    /** The label. */
    public final String label;

    /** The default column width. */
    public final int defalutWidth;

    /** The initial alignment. */
    public final int initialAlignment;

    /** The initial visibility. */
    public final boolean initialVisibility;

    /** The tool tip. */
    public final String toolTip;

    /**
     * The constructor.
     * 
     * @param label
     *            The column label
     * @param defalutWidth
     *            The default column width
     * @param alignment
     *            The initial alignment
     * @param visibility
     *            The initial visibility
     * @param toolTip
     *            The tooltip text
     */
    private NotificationColumn(String label, int defalutWidth, int alignment,
            boolean visibility, String toolTip) {
        this.label = label;
        this.defalutWidth = defalutWidth;
        this.initialAlignment = alignment;
        this.initialVisibility = visibility;
        this.toolTip = toolTip;
    }

    /**
     * Gets the column with given column name.
     * 
     * @param columnName
     *            The column name
     * @return The column
     */
    protected static NotificationColumn getColumn(String columnName) {
        for (NotificationColumn column : NotificationColumn.values()) {
            if (columnName.equals(column.label)) {
                return column;
            }
        }
        throw new IllegalStateException();
    }
}

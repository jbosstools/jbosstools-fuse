/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.swt.SWT;

/**
 * The hot spot column.
 */
public enum HotSpotsColumn {

    /** The hot spot column. */
    HOT_SPOT(Messages.hotSpotColumnLabel, 560, SWT.LEFT,
            Messages.hotSpotColumnToolTip),

    /** The method invocation time. */
    SELFTIME_MS(Messages.selfTimeInMsLabel, 100, SWT.LEFT,
            Messages.selfTimeInMsToolTip),

    /** The method invocation time. */
    SELFTIME_PERCENTAGE(Messages.selfTimeInPercentageLabel, 100, SWT.LEFT,
            Messages.selfTimeInPercentageToolTip),

    /** The method invocation count. */
    COUNT(Messages.countLabel, 70, SWT.LEFT, Messages.countToolTip);

    /** The displayed label. */
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
    private HotSpotsColumn(String label, int defalutWidth, int alignment,
            String toolTip) {
        this.label = label;
        this.defalutWidth = defalutWidth;
        this.alignment = alignment;
        this.toolTip = toolTip;
    }
}

/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import org.eclipse.swt.SWT;

/**
 * The properties column.
 */
public enum PropertiesColumn {

    /** The property column. */
    PROPERTY(Messages.propertyColumnLabel, 250, Messages.propertyColumnToolTip),

    /** The value column. */
    VALUE(Messages.valueColumnLabel, 200, Messages.valueColumnToolTip);

    /** The label. */
    public final String label;

    /** The default column width. */
    public final int defalutWidth;

    /** The tooltip. */
    public final String toolTip;

    /** The alignment. */
    public final int alignment;

    /**
     * The constructor.
     * 
     * @param label
     *            The column label
     * @param defalutWidth
     *            The default column width
     * @param toolTip
     *            The tooltip text
     */
    private PropertiesColumn(String label, int defalutWidth, String toolTip) {
        this.label = label;
        this.defalutWidth = defalutWidth;
        this.toolTip = toolTip;
        this.alignment = SWT.LEFT;
    }
}
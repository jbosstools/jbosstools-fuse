/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import org.eclipse.swt.SWT;

/**
 * The MBean attribute column.
 */
public enum MBeanAttributeColumn {
    
    /** The attribute. */
    Attribute(Messages.attributeColumnLabel, 300, SWT.LEFT,
            Messages.attributeColumnToolTip),

    /** The object name. */
    ObjectName(Messages.objectNameColumnLabel, 300, SWT.LEFT,
            Messages.objectNameColumnToolTip);

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
    private MBeanAttributeColumn(String label, int defalutWidth, int alignment,
            String toolTip) {
        this.label = label;
        this.defalutWidth = defalutWidth;
        this.alignment = alignment;
        this.toolTip = toolTip;
    }
}

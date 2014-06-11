/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;

/**
 * The hot spots comparator.
 */
public class HotSpotsComparator extends ViewerComparator {

    /** The column type. */
    public enum ColumnType {

        /** The methods. */
        Methods,

        /** The time in milliseconds. */
        TimeMs,

        /** The time in percentage. */
        TimePercentage,

        /** The invocation count. */
        Count;
    }

    /** the sort direction */
    private int sortDirection;

    /** the column index */
    private ColumnType columnType;

    /**
     * The constructor.
     * 
     * @param columnType
     *            the column type
     */
    public HotSpotsComparator(ColumnType columnType) {
        this.columnType = columnType;
        if (columnType == ColumnType.Methods) {
            sortDirection = SWT.UP;
        } else {
            sortDirection = SWT.DOWN;
        }
    }

    /*
     * @see ViewerComparator#compare(Viewer, Object, Object)
     */
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        int result = 0;

        if (!(e1 instanceof IMethodNode) || !(e2 instanceof IMethodNode)) {
            return result;
        }

        IMethodNode method1 = (IMethodNode) e1;
        IMethodNode method2 = (IMethodNode) e2;

        if (columnType == ColumnType.Methods) {
            result = method1.getName().compareTo(method2.getName());
        } else if (columnType == ColumnType.TimeMs) {
            result = (method1.getSelfTime() > method2.getSelfTime()) ? 1 : -1;
        } else if (columnType == ColumnType.TimePercentage) {
            result = (method1.getSelfTimeInPercentage() > method2
                    .getSelfTimeInPercentage()) ? 1 : -1;
        } else if (columnType == ColumnType.Count) {
            result = (method1.getInvocationCount() > method2
                    .getInvocationCount()) ? 1 : -1;
        }

        if (sortDirection == SWT.DOWN) {
            result *= -1;
        }
        return result;
    }

    /**
     * Reverses the sort direction.
     */
    protected void reverseSortDirection() {
        sortDirection = (sortDirection == SWT.UP) ? SWT.DOWN : SWT.UP;
    }

    /**
     * Gets the sort direction.
     * 
     * @return the sort direction
     */
    protected int getSortDirection() {
        return sortDirection;
    }
}

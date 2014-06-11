/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.core.IHeapElement;

/**
 * The heap comparator.
 */
public class HeapComparator extends ViewerComparator {

    /** the sort direction */
    private int sortDirection;

    /** the column index */
    private int columnIndex;

    /**
     * The constructor.
     * 
     * @param columnIndex
     *            the column index
     */
    public HeapComparator(int columnIndex) {
        this.columnIndex = columnIndex;
        if (columnIndex == 0) {
            sortDirection = SWT.UP;
        } else {
            sortDirection = SWT.DOWN;
        }
    }

    /*
     * @see ViewerComparator#compare(Viewer, Object, Object)
     */
    @Override
    public int compare(Viewer treeViewer, Object e1, Object e2) {
        int result = 0;

        if (!(e1 instanceof IHeapElement) || !(e2 instanceof IHeapElement)
                || !(treeViewer instanceof TreeViewer)) {
            return result;
        }

        IHeapElement element1 = (IHeapElement) e1;
        IHeapElement element2 = (IHeapElement) e2;

        Tree tree = ((TreeViewer) treeViewer).getTree();
        if (columnIndex == getColumnIndex(tree, HeapColumn.CLASS)) {
            result = super.compare(treeViewer, element1.getClassName(),
                    element2.getClassName());
        } else if (columnIndex == getColumnIndex(tree, HeapColumn.SIZE)) {
            long size1 = element1.getSize();
            long size2 = element2.getSize();
            result = (size1 > size2) ? 1 : -1;
        } else if (columnIndex == getColumnIndex(tree, HeapColumn.COUNT)) {
            long count1 = element1.getCount();
            long count2 = element2.getCount();
            result = (count1 > count2) ? 1 : -1;
        } else if (columnIndex == getColumnIndex(tree, HeapColumn.DELTA)) {
            long delta1 = element1.getSize() - element1.getBaseSize();
            long delta2 = element2.getSize() - element2.getBaseSize();
            result = (delta1 > delta2) ? 1 : -1;
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

    /**
     * Gets the column index.
     * 
     * @return the column index
     */
    protected int getColumnIndex() {
        return columnIndex;
    }

    /**
     * Gets the column index corresponding to the given column.
     * 
     * @param tree
     *            The tree
     * @param column
     *            The thread column
     * @return The column index
     */
    private int getColumnIndex(Tree tree, HeapColumn column) {
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }
}

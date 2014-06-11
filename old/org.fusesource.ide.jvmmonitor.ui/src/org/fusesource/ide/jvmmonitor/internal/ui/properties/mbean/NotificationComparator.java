/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.Notification;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;

/**
 * The notification comparator.
 */
public class NotificationComparator extends ViewerComparator {

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
    public NotificationComparator(int columnIndex) {
        this.columnIndex = columnIndex;
        sortDirection = SWT.DOWN;
    }

    /*
     * @see ViewerComparator#compare(Viewer, Object, Object)
     */
    @Override
    public int compare(Viewer treeViewer, Object e1, Object e2) {
        int result = 0;
    
        if (!(e1 instanceof Notification) || !(e2 instanceof Notification)
                || !(treeViewer instanceof TreeViewer)) {
            return result;
        }
    
        Notification element1 = (Notification) e1;
        Notification element2 = (Notification) e2;
    
        Tree tree = ((TreeViewer) treeViewer).getTree();
        if (columnIndex == getColumnIndex(tree, NotificationColumn.MESSAGE)) {
            result = super.compare(treeViewer, element1.getMessage(), element2
                    .getMessage());
        } else if (columnIndex == getColumnIndex(tree, NotificationColumn.DATE)) {
            result = element1.getTimeStamp() > element2.getTimeStamp() ? 1 : -1;
        } else if (columnIndex == getColumnIndex(tree, NotificationColumn.TYPE)) {
            result = super.compare(treeViewer, element1.getType(), element2
                    .getType());
        } else if (columnIndex == getColumnIndex(tree,
                NotificationColumn.SEQUENCE_NUMBER)) {
            result = element1.getSequenceNumber() > element2
                    .getSequenceNumber() ? 1 : -1;
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
     *            The notification column
     * @return The column index
     */
    private int getColumnIndex(Tree tree, NotificationColumn column) {
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }
}

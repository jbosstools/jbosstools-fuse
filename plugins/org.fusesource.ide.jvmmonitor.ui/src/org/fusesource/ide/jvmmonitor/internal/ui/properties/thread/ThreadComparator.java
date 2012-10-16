/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.thread;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.core.IThreadElement;

/**
 * The thread comparator.
 */
public class ThreadComparator extends ViewerComparator {

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
    public ThreadComparator(int columnIndex) {
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
    
        if (!(e1 instanceof IThreadElement)
                || !(e2 instanceof IThreadElement)
                || !(treeViewer instanceof TreeViewer)) {
            return result;
        }
    
        IThreadElement element1 = (IThreadElement) e1;
        IThreadElement element2 = (IThreadElement) e2;
    
        Tree tree = ((TreeViewer) treeViewer).getTree();
        if (columnIndex == getColumnIndex(tree, ThreadColumn.THREAD)) {
            result = super.compare(treeViewer, element1.getThreadName(),
                    element2.getThreadName());
        } else if (columnIndex == getColumnIndex(tree, ThreadColumn.STATE)) {
            String state1 = element1.getThreadState().name();
            String state2 = element2.getThreadState().name();
            if (element1.isDeadlocked()) {
                state1 = "DEADLOCKED"; //$NON-NLS-1$
            }
            if (element2.isDeadlocked()) {
                state2 = "DEADLOCKED"; //$NON-NLS-1$
            }
            result = super.compare(treeViewer, state1, state2);
        } else if (columnIndex == getColumnIndex(tree, ThreadColumn.CPU)) {
            result = (element1.getCpuUsage() > element2.getCpuUsage()) ? 1 : -1;
        } else if (columnIndex == getColumnIndex(tree,
                ThreadColumn.BLOCKED_TIME)) {
            long time1 = element1.getBlockedTime();
            long time2 = element2.getBlockedTime();
            result = (time1 > time2) ? 1 : -1;
        } else if (columnIndex == getColumnIndex(tree,
                ThreadColumn.BLOCKED_COUNT)) {
            long count1 = element1.getBlockedCount();
            long count2 = element2.getBlockedCount();
            result = (count1 > count2) ? 1 : -1;
        } else if (columnIndex == getColumnIndex(tree, ThreadColumn.WAITED_TIME)) {
            long time1 = element1.getWaitedTime();
            long time2 = element2.getWaitedTime();
            result = (time1 > time2) ? 1 : -1;
        } else if (columnIndex == getColumnIndex(tree,
                ThreadColumn.WAITED_COUNT)) {
            long count1 = element1.getWaitedCount();
            long count2 = element2.getWaitedCount();
            result = (count1 > count2) ? 1 : -1;
        } else if (columnIndex == getColumnIndex(tree, ThreadColumn.LOCK)) {
            result = super.compare(treeViewer, element1.getLockName(), element2
                    .getLockName());
        } else if (columnIndex == getColumnIndex(tree, ThreadColumn.LOCK_OWNWER)) {
            result = super.compare(treeViewer, element1.getLockOwnerName(),
                    element2.getLockOwnerName());
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
    private int getColumnIndex(Tree tree, ThreadColumn column) {
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }
}

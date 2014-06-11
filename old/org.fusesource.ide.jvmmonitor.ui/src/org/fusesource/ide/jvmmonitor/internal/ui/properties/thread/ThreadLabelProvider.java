/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.thread;

import java.lang.Thread.State;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.core.IThreadElement;
import org.fusesource.ide.jvmmonitor.internal.ui.IConstants;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.overview.IFormat;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The label provider for thread viewer.
 */
public class ThreadLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    /** The columns taken into account for filter. */
    private static final ThreadColumn[] COLUMNS_TAKEN_INTO_ACCOUNT_FOR_FILTER = {
            ThreadColumn.THREAD, ThreadColumn.STATE, ThreadColumn.LOCK,
            ThreadColumn.LOCK_OWNWER };

    /** The thread running object image. */
    private Image threadRunnableObjImage;

    /** The thread waiting object image. */
    private Image threadWaitingObjImage;

    /** The thread blocked object image. */
    private Image threadBlockedObjImage;

    /** The thread suspended object image. */
    private Image threadSuspendedObjImage;

    /** The thread deadlocked object image. */
    private Image threadDeadlockedObjImage;

    /** The tree viewer. */
    private TreeViewer treeViewer;

    /**
     * The constructor.
     * 
     * @param treeViewer
     *            The tree viewer
     */
    public ThreadLabelProvider(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    /*
     * @see ITableLabelProvider#getColumnText(Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof IThreadElement) {
            return getColumnText((IThreadElement) element, columnIndex);
        }
        return super.getText(element);
    }

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(ThreadColumn.THREAD)
                && element instanceof IThreadElement) {
            return getThreadObjImage((IThreadElement) element);
        }
        return super.getImage(element);
    }

    /*
     * Gets the text for filtering.
     * 
     * @see LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object obj) {
        if (obj instanceof IThreadElement) {
            StringBuffer buffer = new StringBuffer();

            // columns
            for (ThreadColumn column : COLUMNS_TAKEN_INTO_ACCOUNT_FOR_FILTER) {
                buffer.append(
                        getColumnText((IThreadElement) obj,
                                getColumnIndex(column))).append(' ');
            }

            // stack traces
            if (Activator.getDefault().getPreferenceStore()
                    .getBoolean(IConstants.WIDE_SCOPE_THREAD_FILTER)) {
                for (StackTraceElement element : ((IThreadElement) obj)
                        .getStackTraceElements()) {
                    buffer.append(element.getClassName()).append('.')
                            .append(element.getMethodName()).append(' ');
                    buffer.append(element.getFileName()).append(' ');
                }
            }
            return buffer.toString();
        }
        return super.getText(obj);
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        if (threadRunnableObjImage != null) {
            threadRunnableObjImage.dispose();
        }
        if (threadWaitingObjImage != null) {
            threadWaitingObjImage.dispose();
        }
        if (threadBlockedObjImage != null) {
            threadBlockedObjImage.dispose();
        }
        if (threadSuspendedObjImage != null) {
            threadSuspendedObjImage.dispose();
        }
    }

    /**
     * Gets the column text with the given thread list element.
     * 
     * @param element
     *            The thread list element
     * @param columnIndex
     *            The column index
     * @return The column text
     */
    private String getColumnText(IThreadElement element, int columnIndex) {
        if (columnIndex == getColumnIndex(ThreadColumn.THREAD)) {
            return element.getThreadName();
        } else if (columnIndex == getColumnIndex(ThreadColumn.STATE)) {
            String state = element.getThreadState().name();
            if (element.isDeadlocked()) {
                state = "DEADLOCKED"; //$NON-NLS-1$
            }
            return state;
        } else if (columnIndex == getColumnIndex(ThreadColumn.CPU)) {
            return String.format(" %.1f", element.getCpuUsage()) + "%"; //$NON-NLS-1$ //$NON-NLS-2$
        } else if (columnIndex == getColumnIndex(ThreadColumn.BLOCKED_TIME)) {
            return IFormat.MILLISEC_FORMAT.format(element.getBlockedTime());
        } else if (columnIndex == getColumnIndex(ThreadColumn.BLOCKED_COUNT)) {
            return String.valueOf(element.getBlockedCount());
        } else if (columnIndex == getColumnIndex(ThreadColumn.WAITED_TIME)) {
            return IFormat.MILLISEC_FORMAT.format(element.getWaitedTime());
        } else if (columnIndex == getColumnIndex(ThreadColumn.WAITED_COUNT)) {
            return String.valueOf(element.getWaitedCount());
        } else if (columnIndex == getColumnIndex(ThreadColumn.LOCK)) {
            return element.getLockName();
        } else if (columnIndex == getColumnIndex(ThreadColumn.LOCK_OWNWER)) {
            return element.getLockOwnerName();
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the column index corresponding to the given column.
     * 
     * @param column
     *            The thread column
     * @return The column index
     */
    private int getColumnIndex(ThreadColumn column) {
        Tree tree = treeViewer.getTree();
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the thread object image.
     * 
     * @param element
     *            The thread list element
     * @return The image
     */
    private Image getThreadObjImage(IThreadElement element) {
        if (element.isDeadlocked()) {
            if (threadDeadlockedObjImage == null
                    || threadDeadlockedObjImage.isDisposed()) {
                threadDeadlockedObjImage = Activator.getImageDescriptor(
                        ISharedImages.THREAD_DEADLOCKED_IMG_PATH).createImage();
            }
            return threadDeadlockedObjImage;
        }

        if (element.isSuspended()) {
            if (threadSuspendedObjImage == null
                    || threadSuspendedObjImage.isDisposed()) {
                threadSuspendedObjImage = Activator.getImageDescriptor(
                        ISharedImages.THREAD_SUSPENDED_IMG_PATH).createImage();
            }
            return threadSuspendedObjImage;
        }

        State state = element.getThreadState();
        if (state == State.WAITING || state == State.TIMED_WAITING) {
            if (threadWaitingObjImage == null
                    || threadWaitingObjImage.isDisposed()) {
                threadWaitingObjImage = Activator.getImageDescriptor(
                        ISharedImages.THREAD_WAITING_IMG_PATH).createImage();
            }
            return threadWaitingObjImage;
        } else if (state == State.RUNNABLE) {
            if (threadRunnableObjImage == null
                    || threadRunnableObjImage.isDisposed()) {
                threadRunnableObjImage = Activator.getImageDescriptor(
                        ISharedImages.THREAD_RUNNABLE_IMG_PATH).createImage();
            }
            return threadRunnableObjImage;
        } else if (state == State.BLOCKED) {
            if (threadBlockedObjImage == null
                    || threadBlockedObjImage.isDisposed()) {
                threadBlockedObjImage = Activator.getImageDescriptor(
                        ISharedImages.THREAD_BLOCKED_IMG_PATH).createImage();
            }
            return threadBlockedObjImage;
        }
        return null;
    }
}

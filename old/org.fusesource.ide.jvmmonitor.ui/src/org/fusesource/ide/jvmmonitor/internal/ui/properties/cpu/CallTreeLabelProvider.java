/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.core.cpu.ICallTreeNode;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.IThreadNode;

/**
 * The call tree label provider.
 */
public class CallTreeLabelProvider extends AbstractLabelProvider {

    /** The tree viewer. */
    private TreeViewer treeViewer;

    /**
     * The constructor.
     * 
     * @param treeViewer
     *            The tree viewer
     */
    public CallTreeLabelProvider(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    /*
     * @see ILabelProvider#getImage(Object)
     */
    @Override
    public Image getImage(Object element) {
        return null;
    }

    /*
     * @see ILabelProvider#getText(Object)
     */
    @Override
    public String getText(Object element) {
        return getCallTreeColumnText(element);
    }

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(CallTreeColumn.CALL_TREE)) {
            return getCallTreeColumnImage(element);
        } else if (columnIndex == getColumnIndex(CallTreeColumn.TIME_PERCENTAGE)) {
            return getTimeColumnImage(element);
        } else if (columnIndex == getColumnIndex(CallTreeColumn.SELFTIME_PERCENTAGE)) {
            return getSelfTimeColumnImage(element);
        }

        return null;
    }

    /*
     * @see ITableLabelProvider#getColumnText(Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        String text = ""; //$NON-NLS-1$
        if (columnIndex == getColumnIndex(CallTreeColumn.CALL_TREE)) {
            text = getCallTreeColumnText(element);
        } else if (columnIndex == getColumnIndex(CallTreeColumn.TIME_MS)) {
            text = getTimeInMsColumnText(element);
        } else if (columnIndex == getColumnIndex(CallTreeColumn.TIME_PERCENTAGE)) {
            text = getTimeInPercentageColumnText(element);
        } else if (columnIndex == getColumnIndex(CallTreeColumn.SELFTIME_MS)) {
            text = getSelfTimeInMsColumnText(element);
        } else if (columnIndex == getColumnIndex(CallTreeColumn.SELFTIME_PERCENTAGE)) {
            text = getSelfTimeInPercentageColumnText(element);
        } else if (columnIndex == getColumnIndex(CallTreeColumn.COUNT)) {
            text = getCountColumnText(element);
        }

        return text;
    }

    /*
     * @see ITableFontProvider#getFont(java.lang.Object, int)
     */
    @Override
    public Font getFont(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(CallTreeColumn.CALL_TREE)) {
            return null;
        }

        return getmonospacedFont(treeViewer.getControl().getFont());
    }

    /**
     * Gets the call tree column text.
     * 
     * @param element
     *            the element
     * @return the call tree column text
     */
    private String getCallTreeColumnText(Object element) {
        if (element instanceof IThreadNode) {
            if (((IThreadNode) element).hasChildren()) {
                return Messages.threadLabel + ' '
                        + ((IThreadNode) element).getName();
            }
        } else if (element instanceof ICallTreeNode) {
            return ((ICallTreeNode) element).getName();
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the time in milliseconds column text.
     * 
     * @param element
     *            the element
     * @return the time column text
     */
    private String getTimeInMsColumnText(Object element) {
        ICpuModel cpuModel = (ICpuModel) treeViewer.getInput();
        if (cpuModel == null) {
            return ""; //$NON-NLS-1$
        }

        int length = String.valueOf(cpuModel.getMaxTotalTime() * 1000).length();
        length = (length > 10) ? 10 : length;
        if (element instanceof IThreadNode) {
            String milliseconds = getMillisecondsText(
                    ((IThreadNode) element).getTotalTime(), length);
            return milliseconds;
        } else if (element instanceof ICallTreeNode) {
            long totalTime = ((ICallTreeNode) element).getTotalTime();
            return getMillisecondsText(totalTime, length);
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the time in percentage column text.
     * 
     * @param element
     *            the element
     * @return the time column text
     */
    private String getTimeInPercentageColumnText(Object element) {
        if (element instanceof IThreadNode) {
            return "100.0%";//$NON-NLS-1$ 
        } else if (element instanceof ICallTreeNode) {
            double percentage = ((ICallTreeNode) element)
                    .getTotalTimeInPercentage();
            return String.format("%5.1f", percentage) + "%";//$NON-NLS-1$  //$NON-NLS-2$
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the self time in milliseconds column text.
     * 
     * @param element
     *            the element
     * @return the self time column text
     */
    private String getSelfTimeInMsColumnText(Object element) {
        ICpuModel cpuModel = (ICpuModel) treeViewer.getInput();
        if (cpuModel == null) {
            return ""; //$NON-NLS-1$
        }

        if (element instanceof ICallTreeNode) {
            long totalTime = ((ICallTreeNode) element).getSelfTime();
            int length = String.valueOf(cpuModel.getMaxSelfTime() * 1000)
                    .length();
            length = (length > 10) ? 10 : length;
            return getMillisecondsText(totalTime, length);
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the self time in percentage column text.
     * 
     * @param element
     *            the element
     * @return the self time column text
     */
    private String getSelfTimeInPercentageColumnText(Object element) {
        if (element instanceof ICallTreeNode) {
            double percentage = ((ICallTreeNode) element)
                    .getSelfTimeInPercentage();
            return String.format("%5.1f", percentage) + "%";//$NON-NLS-1$  //$NON-NLS-2$
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the count column text.
     * 
     * @param element
     *            the element
     * @return the count column text
     */
    private String getCountColumnText(Object element) {
        ICpuModel cpuModel = (ICpuModel) treeViewer.getInput();
        if (cpuModel == null) {
            return ""; //$NON-NLS-1$
        }

        if (element instanceof ICallTreeNode) {
            int length = String.valueOf(cpuModel.getMaxInvocationCount())
                    .length();
            return String.format("%" + length + "d", //$NON-NLS-1$ //$NON-NLS-2$
                    ((ICallTreeNode) element).getInvocationCount());
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the image for call tree column.
     * 
     * @param element
     *            the element
     * @return the image, or null if given element is unexpected
     */
    private Image getCallTreeColumnImage(Object element) {
        if (element instanceof IThreadNode) {
            if (((IThreadNode) element).hasChildren()) {
                return getThreadImage();
            }
        } else if (element instanceof ICallTreeNode) {
            return getMethodImage();
        }
        return null;
    }

    /**
     * Gets the image for time column.
     * 
     * @param element
     *            the element
     * @return the image, or null if given element is unexpected
     */
    private Image getTimeColumnImage(Object element) {
        if (element instanceof IThreadNode) {
            return getPercentageImage(HUNDRED);
        } else if (element instanceof ICallTreeNode) {
            double percentage = ((ICallTreeNode) element)
                    .getTotalTimeInPercentage();
            return getPercentageImage(percentage);
        }
        return null;
    }

    /**
     * Gets the image for self time column.
     * 
     * @param element
     *            the element
     * @return the image, or null if given element is unexpected
     */
    private Image getSelfTimeColumnImage(Object element) {
        if (element instanceof ICallTreeNode) {
            double percentage = ((ICallTreeNode) element)
                    .getSelfTimeInPercentage();
            return getPercentageImage(percentage);
        }
        return null;
    }

    /**
     * Gets the column index corresponding to the given column.
     * 
     * @param column
     *            The call tree column
     * @return The column index
     */
    private int getColumnIndex(CallTreeColumn column) {
        Tree tree = treeViewer.getTree();
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }
}

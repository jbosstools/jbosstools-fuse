/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;
import org.fusesource.ide.jvmmonitor.core.cpu.IThreadNode;

/**
 * The hot spot label provider.
 */
public class HotSpotsLabelProvider extends AbstractLabelProvider {

    /** The filtered tree. */
    private HotSpotsFilteredTree filteredTree;

    /**
     * The constructor.
     * 
     * @param filteredTree
     *            The filteredTree
     */
    public HotSpotsLabelProvider(HotSpotsFilteredTree filteredTree) {
        this.filteredTree = filteredTree;
    }

    /*
     * @see ILabelProvider#getImage(Object)
     */
    @Override
    public Image getImage(Object obj) {
        return null;
    }

    /*
     * @see ILabelProvider#getText(Object)
     */
    @Override
    public String getText(Object element) {
        return getMethodColumnText(element);
    }

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(HotSpotsColumn.HOT_SPOT)) {
            return getMethodColumnImage(element);
        } else if (columnIndex == getColumnIndex(HotSpotsColumn.SELFTIME_PERCENTAGE)) {
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
        if (columnIndex == getColumnIndex(HotSpotsColumn.HOT_SPOT)) {
            text = getMethodColumnText(element);
        } else if (columnIndex == getColumnIndex(HotSpotsColumn.SELFTIME_MS)) {
            text = getSelfTimeInMsColumnText(element);
        } else if (columnIndex == getColumnIndex(HotSpotsColumn.SELFTIME_PERCENTAGE)) {
            text = getSelfTimeInPercentageColumnText(element);
        } else if (columnIndex == getColumnIndex(HotSpotsColumn.COUNT)) {
            text = getCountColumnText(element);
        }

        return text;
    }

    /*
     * @see ITableFontProvider#getFont(java.lang.Object, int)
     */
    @Override
    public Font getFont(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(HotSpotsColumn.HOT_SPOT)) {
            return null;
        }

        return getmonospacedFont(filteredTree.getFont());
    }

    /**
     * Gets the image for call tree column.
     * 
     * @param element
     *            the element
     * @return the image, or null if given element is unexpected
     */
    private Image getMethodColumnImage(Object element) {
        if (element instanceof IThreadNode) {
            if (((IThreadNode) element).hasChildren()) {
                return getThreadImage();
            }
        } else if (element instanceof IMethodNode) {
            return getMethodImage();
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
        if (element instanceof IMethodNode) {
            double percentage = ((IMethodNode) element)
                    .getSelfTimeInPercentage();
            return getPercentageImage(percentage);
        }
        return null;
    }

    /**
     * Gets the method column text.
     * 
     * @param element
     *            the element
     * @return the method column text
     */
    private String getMethodColumnText(Object element) {
        if (element instanceof IThreadNode) {
            if (((IThreadNode) element).hasChildren()) {
                return Messages.threadLabel + ' '
                        + ((IThreadNode) element).getName();
            }
        } else if (element instanceof IMethodNode) {
            return ((IMethodNode) element).getName();
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
        ICpuModel cpuModel = (ICpuModel) filteredTree.getViewer().getInput();
        if (cpuModel == null) {
            return ""; //$NON-NLS-1$
        }

        if (element instanceof IMethodNode) {
            long selfTime = ((IMethodNode) element).getSelfTime();
            int length = String.valueOf(cpuModel.getMaxSelfTime() * 1000)
                    .length();
            length = (length > 10) ? 10 : length;
            return getMillisecondsText(selfTime, length);
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
        if (element instanceof IMethodNode) {
            double percentage = ((IMethodNode) element)
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
        ICpuModel cpuModel = (ICpuModel) filteredTree.getViewer().getInput();
        if (cpuModel == null) {
            return ""; //$NON-NLS-1$
        }

        if (element instanceof IMethodNode) {
            int length = String.valueOf(cpuModel.getMaxInvocationCount())
                    .length();
            return String.format("%" + length + "d", //$NON-NLS-1$ //$NON-NLS-2$
                    ((IMethodNode) element).getInvocationCount());
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the column index corresponding to the given column.
     * 
     * @param column
     *            The hot spots column
     * @return The column index
     */
    private int getColumnIndex(HotSpotsColumn column) {
        Tree tree = filteredTree.getViewer().getTree();
        String label = column == HotSpotsColumn.HOT_SPOT ? filteredTree
                .getMethodColumnName() : column.label;
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(label)) {
                return i;
            }
        }
        return -1;
    }
}

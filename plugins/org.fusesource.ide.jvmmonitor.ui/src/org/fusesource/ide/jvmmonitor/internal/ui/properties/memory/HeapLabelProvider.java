/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.core.IHeapElement;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The heap label provider.
 */
public class HeapLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    /** The class image. */
    private Image classImage;

    /** The tree viewer. */
    private TreeViewer treeViewer;

    /**
     * The constructor.
     * 
     * @param tableViewer
     *            The tree viewer
     */
    public HeapLabelProvider(TreeViewer tableViewer) {
        this.treeViewer = tableViewer;
    }

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(HeapColumn.CLASS)) {
            return getClassImage();
        }
        return null;
    }

    /*
     * @see ITableLabelProvider#getColumnText(Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof IHeapElement) {
            return getColumnText((IHeapElement) element, columnIndex);
        }
        return super.getText(element);
    }

    /*
     * Gets the text for filtering.
     * 
     * @see LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object obj) {
        if (obj instanceof IHeapElement) {
            return getColumnText((IHeapElement) obj,
                    getColumnIndex(HeapColumn.CLASS));
        }
        return super.getText(obj);
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        super.dispose();
        if (classImage != null) {
            classImage.dispose();
        }
    }

    /**
     * Gets the column text with the given heap list element.
     * 
     * @param element
     *            The heap list element
     * @param columnIndex
     *            The column index
     * @return The column text
     */
    private String getColumnText(IHeapElement element, int columnIndex) {
        if (columnIndex == getColumnIndex(HeapColumn.CLASS)) {
            return element.getClassName();
        } else if (columnIndex == getColumnIndex(HeapColumn.SIZE)) {
            return NumberFormat.getInstance().format(element.getSize());
        } else if (columnIndex == getColumnIndex(HeapColumn.COUNT)) {
            return NumberFormat.getInstance().format(element.getCount());
        } else if (columnIndex == getColumnIndex(HeapColumn.DELTA)) {
            return NumberFormat.getInstance().format(
                    element.getSize() - element.getBaseSize());
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the column index corresponding to the given column.
     * 
     * @param column
     *            The heap column
     * @return The column index
     */
    private int getColumnIndex(HeapColumn column) {
        Tree table = treeViewer.getTree();
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the class image.
     * 
     * @return The class image
     */
    private Image getClassImage() {
        if (classImage == null || classImage.isDisposed()) {
            classImage = Activator.getImageDescriptor(
                    ISharedImages.CLASS_OBJ_IMG_PATH).createImage();
        }
        return classImage;
    }
}

/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.timeline;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The MBean attribute label provider.
 */
public class MBeanAttributeLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    /** The attribute image. */
    private Image attributeImage;

    /** The MBean image. */
    private Image mBeanImage;

    /** The viewer. */
    private TreeViewer viewer;

    /**
     * The constructor.
     * 
     * @param viewer
     *            The viewer
     */
    public MBeanAttributeLabelProvider(TreeViewer viewer) {
        this.viewer = viewer;
    }

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(MBeanAttributeColumn.Attribute)) {
            return getAttributeImage();
        } else if (columnIndex == getColumnIndex(MBeanAttributeColumn.ObjectName)) {
            return getMBeanImage();
        }
        return null;
    }

    /*
     * @see ITableLabelProvider#getColumnText(Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof MBeanAttribute) {
            return getColumnText((MBeanAttribute) element, columnIndex);
        }
        throw new IllegalArgumentException("unknown input"); //$NON-NLS-1$
    }

    /*
     * Gets the text for filtering.
     * 
     * @see LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object obj) {
        if (obj instanceof MBeanAttribute) {
            return getColumnText((MBeanAttribute) obj,
                    getColumnIndex(MBeanAttributeColumn.Attribute));
        }
        return super.getText(obj);
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        if (attributeImage != null) {
            attributeImage.dispose();
        }
        if (mBeanImage != null) {
            mBeanImage.dispose();
        }
    }

    /**
     * Gets the column text with the given MXBean attribute.
     * 
     * @param attribute
     *            The MXBean attribute
     * @param columnIndex
     *            The column index
     * @return The column text
     */
    private String getColumnText(MBeanAttribute attribute, int columnIndex) {
        if (columnIndex == getColumnIndex(MBeanAttributeColumn.ObjectName)) {
            return attribute.getObjectName().getCanonicalName();
        } else if (columnIndex == getColumnIndex(MBeanAttributeColumn.Attribute)) {
            return attribute.getAttributeName();
        }
        throw new IllegalArgumentException("unknown column"); //$NON-NLS-1$
    }

    /**
     * Gets the column index corresponding to the given column.
     * 
     * @param column
     *            The attribute column
     * @return The column index
     */
    private int getColumnIndex(MBeanAttributeColumn column) {
        Tree tree = viewer.getTree();
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the attribute image.
     * 
     * @return The attribute image
     */
    private Image getAttributeImage() {
        if (attributeImage == null || attributeImage.isDisposed()) {
            attributeImage = Activator.getImageDescriptor(
                    ISharedImages.ATTRIBUTE_IMG_PATH).createImage();
        }
        return attributeImage;
    }

    /**
     * Gets the MBean image.
     * 
     * @return The MBean image
     */
    private Image getMBeanImage() {
        if (mBeanImage == null || mBeanImage.isDisposed()) {
            mBeanImage = Activator.getImageDescriptor(
                    ISharedImages.MBEAN_IMG_PATH).createImage();
        }
        return mBeanImage;
    }
}

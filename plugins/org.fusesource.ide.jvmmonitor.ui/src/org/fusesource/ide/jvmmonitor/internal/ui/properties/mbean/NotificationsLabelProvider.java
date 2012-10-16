/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.Notification;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The notifications label provider.
 */
public class NotificationsLabelProvider extends LabelProvider implements
        ITableLabelProvider, ISharedImages {

    /** The date format. */
    private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss.SSS"; //$NON-NLS-1$

    /** the notification image */
    private Image notificationImage;

    /** The tree viewer. */
    private TreeViewer treeViewer;

    /**
     * The constructor.
     * 
     * @param treeViewer
     *            The tree viewer
     */
    public NotificationsLabelProvider(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    /*
     * @see ITableLabelProvider#getColumnText(Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof Notification) {
            return getColumnText((Notification) element, columnIndex);
        }
        return super.getText(element);
    }

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        if (columnIndex == getColumnIndex(NotificationColumn.MESSAGE)) {
            return getNotificationImage();
        }
        return super.getImage(element);
    }

    /*
     * @see BaseLabelProvider#dispose()
     */
    @Override
    public void dispose() {
        if (notificationImage != null) {
            notificationImage.dispose();
        }
    }

    /**
     * Gets the column text with the given thread list element.
     * 
     * @param element
     *            The notification
     * @param columnIndex
     *            The column index
     * @return The column text
     */
    private String getColumnText(Notification element, int columnIndex) {
        if (columnIndex == getColumnIndex(NotificationColumn.TYPE)) {
            return element.getType();
        } else if (columnIndex == getColumnIndex(NotificationColumn.DATE)) {
            return new SimpleDateFormat(DATE_FORMAT)
                    .format(new Date(element.getTimeStamp()));
        } else if (columnIndex == getColumnIndex(NotificationColumn.SEQUENCE_NUMBER)) {
            return String.valueOf(element.getSequenceNumber());
        } else if (columnIndex == getColumnIndex(NotificationColumn.MESSAGE)) {
            return element.getMessage();
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the column index corresponding to the given column.
     * 
     * @param column
     *            The notification column
     * @return The column index
     */
    private int getColumnIndex(NotificationColumn column) {
        Tree tree = treeViewer.getTree();
        for (int i = 0; i < tree.getColumnCount(); i++) {
            if (tree.getColumn(i).getText().equals(column.label)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the notification image.
     * 
     * @return the image
     */
    private Image getNotificationImage() {
        if (notificationImage == null || notificationImage.isDisposed()) {
            notificationImage = Activator.getImageDescriptor(
                    NOTIFICATION_IMG_PATH).createImage();
        }
        return notificationImage;
    }
}

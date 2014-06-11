/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The action to open notification details dialog.
 */
public class NotificationDetailsDialogAction extends Action implements
        ISelectionChangedListener, ISharedImages {

    /** The dialog. */
    private NotificationDetailsDialog dialog;

    /** The notification tree. */
    private NotificationFilteredTree tree;

    /**
     * The constructor.
     * 
     * @param tree
     *            The notification tree
     */
    protected NotificationDetailsDialogAction(NotificationFilteredTree tree) {
        this.tree = tree;
        dialog = new NotificationDetailsDialog(tree);
        setText(Messages.notificationDetailsLabel);
        setImageDescriptor(Activator.getImageDescriptor(DETAILS_IMG_PATH));
        setEnabled(false);
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (dialog.isOpened()) {
            dialog.refreshWidgets();
        }
        ISelection selection = tree.getViewer().getSelection();
        setEnabled(selection != null && !selection.isEmpty());
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        if (dialog.isOpened()) {
            dialog.refreshWidgets();
        } else {
            dialog.open();
        }
    }
}

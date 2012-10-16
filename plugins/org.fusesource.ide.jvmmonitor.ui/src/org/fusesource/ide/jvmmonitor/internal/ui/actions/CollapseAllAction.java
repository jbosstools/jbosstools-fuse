/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.actions;

import static org.fusesource.ide.jvmmonitor.ui.ISharedImages.COLLAPSE_ALL_IMG_PATH;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.fusesource.ide.jvmmonitor.ui.Activator;

/**
 * The action to collapse all tree nodes.
 */
public class CollapseAllAction extends Action {

    /** The tree viewer. */
    private TreeViewer viewer;

    /**
     * The constructor.
     */
    public CollapseAllAction() {
        setToolTipText(Messages.collapseAllLabel);
        setImageDescriptor(Activator.getImageDescriptor(COLLAPSE_ALL_IMG_PATH));
        setId(getClass().getName());
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        if (viewer != null) {
            viewer.collapseAll();
        }
    }

    /**
     * Sets the viewer.
     * 
     * @param treeViewer
     *            The tree viewer
     */
    public void setViewer(TreeViewer treeViewer) {
        viewer = treeViewer;
    }
}

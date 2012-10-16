/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.AbstractFilteredTree.ViewerType;

/**
 * The action to find tree item.
 */
public class FindAction extends Action {

    /** The tree viewer. */
    private TreeViewer viewer;

    /** The viewer type. */
    private ViewerType type;

    /**
     * The constructor.
     */
    public FindAction() {
        setText(Messages.findLabel);
        setActionDefinitionId(IWorkbenchCommandConstants.EDIT_FIND_AND_REPLACE);
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        if (viewer != null && type != null) {
            new FindDialog(viewer, type).open();
        }
    }

    /**
     * Sets the viewer.
     * 
     * @param treeViewer
     *            The tree viewer
     * @param viewerType
     *            The viewer type
     */
    public void setViewer(TreeViewer treeViewer, ViewerType viewerType) {
        viewer = treeViewer;
        type = viewerType;
    }
}

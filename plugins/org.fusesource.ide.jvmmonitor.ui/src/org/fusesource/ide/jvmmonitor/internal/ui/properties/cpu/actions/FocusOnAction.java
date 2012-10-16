/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.fusesource.ide.jvmmonitor.core.cpu.ICallTreeNode;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.AbstractFilteredTree;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.AbstractFilteredTree.ViewerType;


/**
 * The action to focus on frame or thread.
 */
public class FocusOnAction extends Action {

    /** The selected frame node. */
    private ICallTreeNode selectedNode;

    /** The filtered tree. */
    private AbstractFilteredTree filteredTree;

    /**
     * The constructor.
     * 
     * @param filteredTree
     *            The filtered tree
     */
    public FocusOnAction(AbstractFilteredTree filteredTree) {
        this.filteredTree = filteredTree;
        setText(Messages.focusOnLabel);
        setEnabled(false);
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        ICpuModel cpuModel = (ICpuModel) filteredTree.getViewer().getInput();
        boolean focused = cpuModel.getFocusTarget() != null;

        if (!focused) {
            cpuModel.setFocusTarget(selectedNode);
        } else {
            cpuModel.setFocusTarget(null);
            updateMenu();
        }

        filteredTree.getViewer().refresh();
    }

    /**
     * Notifies when menu is about to be shown.
     */
    public void aboutToShow() {
        ICpuModel cpuModel = (ICpuModel) filteredTree.getViewer().getInput();
        boolean focused = cpuModel.getFocusTarget() != null;
        setChecked(focused);

        Object element = ((TreeSelection) filteredTree.getViewer()
                .getSelection()).getFirstElement();

        // frame is selected on call tree
        if (element instanceof ICallTreeNode) {
            selectedNode = (ICallTreeNode) element;
            setEnabled(true);
            if (!focused) {
                updateMenu();
            }
            return;
        }

        if (filteredTree.getViewerType() == ViewerType.CallTree) {
            // thread node is selected on call tree
            selectedNode = null;
        } else {
            selectedNode = cpuModel.getFocusTarget();
        }

        setEnabled(focused);
        updateMenu();
    }

    /**
     * Updates the menu with the currently selected frame.
     */
    private void updateMenu() {
        StringBuffer text = new StringBuffer();
        text.append(Messages.focusOnLabel);
        if (selectedNode != null) {
            text.append(" '").append((selectedNode).getNonqualifiedName()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        setText(text.toString());
    }
}

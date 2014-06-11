/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;
import org.fusesource.ide.jvmmonitor.core.cpu.IMethodNode;

/**
 * The action to show the callers/callees of selected method.
 */
public class ShowCallersCalleesAction extends Action implements
        ISelectionChangedListener {

    /** The tree viewer. */
    private TreeViewer treeViewer;

    /** The target for callers/callees. */
    private IMethodNode callesCalleesTargetNode;

    /**
     * The constructor.
     * 
     * @param treeViewer
     *            The tree viewer
     */
    public ShowCallersCalleesAction(TreeViewer treeViewer) {
        setText(Messages.showCallersCalleesLabel);
        setEnabled(false);
        this.treeViewer = treeViewer;
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection selection = event.getSelection();
        if (!(selection instanceof StructuredSelection)) {
            return;
        }

        Object element = ((StructuredSelection) selection).getFirstElement();

        if (element instanceof IMethodNode) {
            callesCalleesTargetNode = (IMethodNode) element;
            setEnabled(true);
        } else {
            setEnabled(false);
        }
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        ICpuModel cpuModel = (ICpuModel) treeViewer.getInput();
        cpuModel.setCallersCalleesTarget(callesCalleesTargetNode);
    }
}

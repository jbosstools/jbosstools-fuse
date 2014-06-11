/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.RefreshJob;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;


/**
 * The action to stop monitoring JVM.
 */
public class StopMonitoringAction extends Action implements
        ISelectionChangedListener {

    /** The active JVMs. */
    List<IActiveJvm> jvms;

    /** The tree viewer. */
    TreeViewer treeViewer;

    /** The visibility. */
    private boolean visible;

    /**
     * The constructor.
     * 
     * @param treeViewer
     *            The tree viewer
     */
    public StopMonitoringAction(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
        jvms = new ArrayList<IActiveJvm>();
        visible = false;

        setText(Messages.stopMonitoringLabel);
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.DISCONNECT_IMG_PATH));
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (!(event.getSelection() instanceof TreeSelection)) {
            return;
        }

        jvms.clear();

        visible = false;
        for (Object element : ((TreeSelection) event.getSelection()).toArray()) {
            if (element instanceof IActiveJvm) {
                jvms.add((IActiveJvm) element);
                visible = true;
            } else {
                visible = false;
                jvms.clear();
                break;
            }
        }

        refresh();
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        RefreshJob refreshJob = new RefreshJob(Messages.stopMonitoringJobLabel,
                UUID.randomUUID().toString()) {

            @Override
            protected void refreshModel(IProgressMonitor monitor) {
                for (IActiveJvm jvm : jvms) {
                    jvm.disconnect();
                }
            }

            @Override
            protected void refreshUI() {
                if (!treeViewer.getControl().isDisposed()) {
                    treeViewer.refresh();
                }
            }
        };
        refreshJob.schedule();
    }

    /**
     * Refreshes the enable state.
     */
    protected void refresh() {
        boolean enable = true;
        for (IActiveJvm jvm : jvms) {
            if (!jvm.isConnected() || !jvm.isConnectionSupported()) {
                enable = false;
            }
        }
        setEnabled(enable);
    }

    /**
     * Gets the state indicating if this action is visible.
     * 
     * @return <tt>true</tt> if this action is visible
     */
    protected boolean getVisible() {
        return visible;
    }
}

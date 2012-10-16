/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.fusesource.ide.jvmmonitor.core.ISnapshot;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The action to open snapshot.
 */
public class OpenSnapshotAction extends Action implements
        ISelectionChangedListener {

    /** The snapshots. */
    private List<ISnapshot> snapshots;

    /** The visibility. */
    private boolean visible;

    /**
     * The constructor.
     */
    public OpenSnapshotAction() {
        setText(Messages.openSnapshotLabel);
        snapshots = new ArrayList<ISnapshot>();
        visible = false;
    }

    /*
     * @see ISelectionChangedListener#selectionChanged(SelectionChangedEvent)
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        if (!(event.getSelection() instanceof TreeSelection)) {
            return;
        }

        snapshots.clear();

        visible = false;
        for (Object element : ((TreeSelection) event.getSelection()).toArray()) {
            if (element instanceof ISnapshot) {
                snapshots.add((ISnapshot) element);
                visible = true;
            } else {
                visible = false;
                snapshots.clear();
                break;
            }
        }

        setEnabled(snapshots.size() > 0);
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        for (ISnapshot snapshot : snapshots) {
            openEditor(snapshot.getFileStore());
        }
    }

    /**
     * Opens the text editor.
     * 
     * @param fileStore
     *            The file store
     */
    public static void openEditor(final IFileStore fileStore) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    IDE.openEditorOnFileStore(PlatformUI.getWorkbench()
                            .getActiveWorkbenchWindow().getActivePage(),
                            fileStore);
                } catch (PartInitException e) {
                    Activator.log(IStatus.ERROR, null, e);
                }
            }
        });
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

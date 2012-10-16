/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.editors;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.ToggleOrientationAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.ToggleOrientationAction.Orientation;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.thread.ThreadSashForm;

/**
 * The thread action contributor.
 */
public class ThreadActionContributor extends EditorActionBarContributor {

    /** The actions to toggle orientation. */
    private ArrayList<ToggleOrientationAction> orientationActions;

    /*
     * @see EditorActionBarContributor#contributeToToolBar(IToolBarManager)
     */
    @Override
    public void contributeToToolBar(IToolBarManager toolBarManager) {
        orientationActions = new ArrayList<ToggleOrientationAction>();
        for (Orientation orientation : Orientation.values()) {
            orientationActions.add(new ToggleOrientationAction(orientation));
        }

        for (Action action : orientationActions) {
            toolBarManager.add(action);
        }
    }

    /*
     * @see EditorActionBarContributor#setActiveEditor(IEditorPart)
     */
    @Override
    public void setActiveEditor(IEditorPart targetEditor) {
        if (!(targetEditor instanceof ThreadDumpEditor)) {
            return;
        }

        ThreadDumpEditor threadDump = (ThreadDumpEditor) targetEditor;
        ThreadSashForm sashForm = threadDump.getThreadSashForm();
        for (ToggleOrientationAction action : orientationActions) {
            action.setSashForm(sashForm);
            if (action.isChecked()) {
                action.run();
            }
        }
    }
}

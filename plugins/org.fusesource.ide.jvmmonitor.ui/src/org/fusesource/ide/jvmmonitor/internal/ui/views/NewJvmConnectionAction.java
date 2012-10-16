/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.ui.Activator;
import org.fusesource.ide.jvmmonitor.ui.ISharedImages;

/**
 * The action to create a new JVM connection.
 */
public class NewJvmConnectionAction extends Action {

    /** The tree viewer. */
    private TreeViewer viewer;

    /**
     * The constructor.
     * 
     * @param viewer
     *            The tree viewer
     */
    public NewJvmConnectionAction(TreeViewer viewer) {
        this.viewer = viewer;

        setText(Messages.newJvmConnectionLabel);
        setImageDescriptor(Activator
                .getImageDescriptor(ISharedImages.ADD_JVM_IMG_PATH));
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        NewJvmConnectionWizard wizard = new NewJvmConnectionWizard(viewer);
        WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell(), wizard);
        dialog.create();
        dialog.open();
    }
}

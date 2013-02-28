/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.EditorUtils;
import org.fusesource.ide.jmx.ui.internal.dialogs.OpenMBeanSelectionDialog;


public class OpenMBeanAction extends Action implements
        IWorkbenchWindowActionDelegate {

    public OpenMBeanAction() {
        super();
        setText(Messages.OpenMBeanAction_text);
        setDescription(Messages.OpenMBeanAction_description);
        setToolTipText(Messages.OpenMBeanAction_tooltip);
    }

    @Override
    public void run() {
        Shell parent = JMXUIActivator.getActiveWorkbenchShell();
        OpenMBeanSelectionDialog dialog = new OpenMBeanSelectionDialog(parent);
        dialog.setTitle(Messages.OpenMBeanAction_dialogTitle);
        dialog.setMessage(Messages.OpenMBeanAction_dialogDescription);
        int result = dialog.open();
        if (result != IDialogConstants.OK_ID)
            return;
        Object object = dialog.getFirstResult();
        IEditorInput input = EditorUtils.getEditorInput(object);
        if (input != null) {
            EditorUtils.openMBeanEditor(input);
        }
    }

    public void run(IAction action) {
        run();
    }

    public void dispose() {
        // do nothing
    }

    public void init(IWorkbenchWindow window) {
        // do nothing
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // do nothing
    }

}

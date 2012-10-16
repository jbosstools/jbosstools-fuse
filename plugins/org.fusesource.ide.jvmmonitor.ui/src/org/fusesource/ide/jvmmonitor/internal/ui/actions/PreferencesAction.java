/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * The action to open preference dialog.
 */
public class PreferencesAction extends Action {

    /** The preference dialog (keep reference for testing). */
    private PreferenceDialog dialog;

    /**
     * The constructor.
     */
    public PreferencesAction() {
        setText(Messages.preferencesLabel);
        setId(getClass().getName());
    }

    /*
     * @see Action#run()
     */
    @Override
    public void run() {
        getDialog().open();
    }

    /**
     * Gets the preference dialog.
     * 
     * @return The preference dialog
     */
    private PreferenceDialog getDialog() {
        String[] preferencePages = new String[] {
                "org.fusesource.ide.jvmmonitor.ui.JavaMonitorPreferencePage", //$NON-NLS-1$
                "org.fusesource.ide.jvmmonitor.tools.ToolsPreferencePage" }; //$NON-NLS-1$

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getShell();
        dialog = PreferencesUtil.createPreferenceDialogOn(shell,
                preferencePages[0], preferencePages, null);
        return dialog;
    }
}

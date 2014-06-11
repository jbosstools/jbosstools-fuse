/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * The java monitor perspective factory.
 */
public class JavaMonitorPerspectiveFactory implements IPerspectiveFactory {

    /** The folder id for navigator views. */
    private static final String ID_NAVIGATOR_FOLDER = "org.fusesource.ide.jvmmonitor.internal.ui.NavigatorFolderView"; //$NON-NLS-1$

    /** The folder id for debug view. */
    private static final String ID_DEBUG_FOLDER = "org.fusesource.ide.jvmmonitor.internal.ui.DebugFolderView"; //$NON-NLS-1$

    /** The folder id for properties view. */
    private static final String ID_PROPERTIES_FOLDER = "org.fusesource.ide.jvmmonitor.internal.ui.PropertiesFolderView"; //$NON-NLS-1$

    /** The folder id for outline view. */
    private static final String ID_OUTLINE_FOLDER = "org.fusesource.ide.jvmmonitor.internal.ui.OutlineFolderView"; //$NON-NLS-1$

    /** The JVM explorer view id. */
    private static final String ID_JVM_EXPLORER = "org.fusesource.ide.jvmmonitor.ui.JvmExplorer"; //$NON-NLS-1$

    /*
     * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
     */
    @Override
    @SuppressWarnings("deprecation")
    public void createInitialLayout(IPageLayout layout) {

        IFolderLayout topLeftFolder = layout.createFolder(ID_NAVIGATOR_FOLDER,
                IPageLayout.LEFT, (float) 0.25, IPageLayout.ID_EDITOR_AREA);
        topLeftFolder.addView(ID_JVM_EXPLORER);
        topLeftFolder.addView(JavaUI.ID_PACKAGES);

        IFolderLayout bottomLeftFolder = layout.createFolder(ID_DEBUG_FOLDER,
                IPageLayout.BOTTOM, (float) 0.5, ID_NAVIGATOR_FOLDER);
        bottomLeftFolder.addView(IDebugUIConstants.ID_DEBUG_VIEW);

        IFolderLayout bottomFolder = layout.createFolder(ID_PROPERTIES_FOLDER,
                IPageLayout.BOTTOM, (float) 0.5, IPageLayout.ID_EDITOR_AREA);
        bottomFolder.addView(IPageLayout.ID_PROP_SHEET);

        IFolderLayout rightFolder = layout.createFolder(ID_OUTLINE_FOLDER,
                IPageLayout.RIGHT, (float) 0.75, IPageLayout.ID_EDITOR_AREA);
        rightFolder.addView(IPageLayout.ID_OUTLINE);

        layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
        layout.addActionSet(IDebugUIConstants.DEBUG_ACTION_SET);

        layout.addShowViewShortcut(ID_JVM_EXPLORER);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(JavaUI.ID_PACKAGES);

        layout.addPerspectiveShortcut(JavaUI.ID_PERSPECTIVE);
        layout.addPerspectiveShortcut(IDebugUIConstants.ID_DEBUG_PERSPECTIVE);
    }
}

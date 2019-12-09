/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.branding.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.foundation.ui.util.UIHelper;


/**
 * @author lhein
 */
public class FusePerspective implements IPerspectiveFactory, UIHelper {
	
	public static final String ID = "org.fusesource.ide.branding.perspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		configureAvailableActionSets(layout);
		
		layout.setEditorAreaVisible(false);

		String editorArea = layout.getEditorArea();

		// left
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.20f, editorArea);

		// bottom
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.60f, editorArea);

		IFolderLayout messages = layout.createFolder("messages", IPageLayout.RIGHT, 0.60f, IPageLayout.ID_PROP_SHEET);
		addViewIfExists(messages, ID_MESSAGE_TABLE);
		addViewIfExists(messages, ID_SERVERS_VIEW);
		addViewIfExists(messages, ID_CONSOLE_VIEW);

		// right	
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.60f, editorArea);
		addViewIfExists(right, ID_JMX_EXPLORER);
		addViewIfExists(right, ID_DIAGRAM_VIEW);
		addViewIfExists(right, ID_TERMINAL_VIEW);
	}

	private void configureAvailableActionSets(IPageLayout layout) {
		layout.addActionSet(JavaUI.ID_ACTION_SET);
		layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);

		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(IDebugUIConstants.DEBUG_ACTION_SET);

		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
	}

	private void addViewIfExists(IFolderLayout folderlayout, String viewId) {
		if (existView(viewId)) {
			folderlayout.addView(viewId);
		}
	}
	
	private boolean existView(String viewId) {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			return wb.getViewRegistry().find(viewId) != null;
		}
		return false;
	}
}

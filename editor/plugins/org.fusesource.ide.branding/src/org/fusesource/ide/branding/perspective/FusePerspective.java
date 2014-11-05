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

package org.fusesource.ide.branding.perspective;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.commons.ui.UIHelper;


/**
 * @author lhein
 */
public class FusePerspective implements IPerspectiveFactory, UIHelper {
	
	public static final String ID = "org.fusesource.ide.branding.perspective";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui
	 * .IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);

		String editorArea = layout.getEditorArea();

		// left
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.20f, editorArea);

		// bottom
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.70f, editorArea);

		IFolderLayout messages = layout.createFolder("messages", IPageLayout.RIGHT, 0.50f, IPageLayout.ID_PROP_SHEET);
		if (existView(ID_MESSAGE_TABLE)) messages.addView(ID_MESSAGE_TABLE);
		if (existView(ID_SERVERS_VIEW)) messages.addView(ID_SERVERS_VIEW);
		messages.addView(ID_CONSOLE_VIEW);

		// right
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.25f, editorArea);
		if (existView(ID_JMX_EXPORER)) right.addView(ID_JMX_EXPORER);

		IFolderLayout views = layout.createFolder("views",  IPageLayout.LEFT, 0.60f, "right");
		if (existView(ID_DIAGRAM_VIEW)) views.addView(ID_DIAGRAM_VIEW);
		if (existView(ID_TERMINAL_VIEW)) views.addView(ID_TERMINAL_VIEW);
	}

	private boolean existView(String viewId) {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			return wb.getViewRegistry().find(viewId) != null;
		}
		return false;
	}
}

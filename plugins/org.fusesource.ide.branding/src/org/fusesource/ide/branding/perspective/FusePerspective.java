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

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.fusesource.ide.commons.ui.UIHelper;


/**
 * @author lhein
 */
public class FusePerspective implements IPerspectiveFactory, UIHelper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui
	 * .IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		boolean showConsole = false;
		layout.setEditorAreaVisible(false);

		String editorArea = layout.getEditorArea();

		// left
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.20f, editorArea);

		// bottom
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.70f, editorArea);
		// layout.addView(ID_MESSAGE_TABLE, IPageLayout.RIGHT, 0.50f,
		// IPageLayout.ID_PROP_SHEET);

		IFolderLayout messages = layout.createFolder("messages", IPageLayout.RIGHT, 0.50f, IPageLayout.ID_PROP_SHEET);
		messages.addView(ID_LOGS_VIEW);
		messages.addView(ID_MESSAGE_TABLE);

		boolean showLogs = false;
		if (showLogs) {
			messages.addView(ID_LOGS_VIEW);
		}


		messages.addView(ID_SERVERS_VIEW);
		if (!showConsole) {
			messages.addView(ID_CONSOLE_VIEW);
		}

		// right
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.25f, editorArea);
		right.addView(ID_JMX_EXPORER);
		right.addView(ID_FABRIC_EXPORER);

		IFolderLayout views = layout.createFolder("views",  IPageLayout.LEFT, 0.60f, "right");
		views.addView(ID_DIAGRAM_VIEW);
		views.addView(ID_TERMINAL_VIEW);
	}


}

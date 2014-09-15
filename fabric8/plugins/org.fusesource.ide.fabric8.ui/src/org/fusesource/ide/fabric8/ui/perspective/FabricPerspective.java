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

package org.fusesource.ide.fabric8.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.fusesource.ide.commons.ui.UIHelper;


public class FabricPerspective implements IPerspectiveFactory, UIHelper {

	public static final String ID_FABRIC_EXPORER = "org.fusesource.ide.fabric.navigator";
	public static final String ID_LOGS_VIEW = "org.fusesource.ide.fabric.views.logs.LogsView";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		String editorArea = layout.getEditorArea();

		layout.addView(ID_FABRIC_EXPORER, IPageLayout.LEFT, 0.30f, editorArea);
		layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.50f, editorArea);

		//layout.addView(ID_LOGS_VIEW, IPageLayout.TOP, 0.50f, IPageLayout.ID_PROP_SHEET);

		IFolderLayout messages = layout.createFolder("LogsView", IPageLayout.TOP, 0.50f, IPageLayout.ID_PROP_SHEET);
		messages.addView(ID_LOGS_VIEW);
		messages.addView(ID_MESSAGE_TABLE);


		layout.addView(ID_TERMINAL_VIEW, IPageLayout.RIGHT, 0.40f, editorArea);
		layout.addView(ID_DIAGRAM_VIEW, IPageLayout.RIGHT, 0.40f, editorArea);
	}
}

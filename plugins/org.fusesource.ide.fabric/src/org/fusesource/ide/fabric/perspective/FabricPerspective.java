package org.fusesource.ide.fabric.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.fusesource.ide.commons.ui.PerspectiveSupport;


public class FabricPerspective extends PerspectiveSupport {

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

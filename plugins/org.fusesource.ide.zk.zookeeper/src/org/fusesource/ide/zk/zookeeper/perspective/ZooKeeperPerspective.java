package org.fusesource.ide.zk.zookeeper.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ZooKeeperPerspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		layout.addView("org.fusesource.ide.zk.zookeeper.views.explorer.ZooKeeperExplorerView", IPageLayout.LEFT, 0.40f, editorArea);
	}

}

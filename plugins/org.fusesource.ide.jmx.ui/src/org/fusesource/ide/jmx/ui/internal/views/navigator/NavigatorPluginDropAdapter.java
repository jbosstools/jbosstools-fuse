package org.fusesource.ide.jmx.ui.internal.views.navigator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.PluginDropAdapter;

public class NavigatorPluginDropAdapter extends PluginDropAdapter {

	public NavigatorPluginDropAdapter(StructuredViewer viewer) {
		super(viewer);
	}

	@Override
	public void drop(DropTargetEvent event) {
		super.drop(event);
	}

	@Override
	public boolean performDrop(Object data) {
		return super.performDrop(data);
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;
		//return super.validateDrop(target, operation, transferType);
	}

	

}

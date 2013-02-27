/**
 * 
 */
package org.fusesource.ide.camel.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.fusesource.ide.camel.editor.commands.DiagramOperations;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.editor.editor.RiderEditor;

/**
 * @author lhein
 *
 */
public class DeleteRouteHandler extends AbstractHandler {
	
	/**
	 * 
	 */
	public DeleteRouteHandler() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		if (window != null) {
			IWorkbenchPage activePage = window.getActivePage();
			if (activePage != null) {
				IEditorPart activeEditor = activePage.getActiveEditor();
				if (activeEditor instanceof RiderEditor) {
					RiderEditor editor = (RiderEditor) activeEditor;
					RiderDesignEditor designEditor = editor.getDesignEditor();
					if (designEditor != null) {
						DiagramOperations.deleteRoute(designEditor, designEditor.getSelectedRoute());
						if (designEditor.getModel().getChildren().size() < 1) {
							designEditor.addNewRoute();
						}
					}
				}
			}
		}
		return null;
	}
}

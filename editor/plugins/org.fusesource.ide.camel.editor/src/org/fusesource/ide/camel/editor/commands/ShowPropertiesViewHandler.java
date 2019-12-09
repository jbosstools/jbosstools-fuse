/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.foundation.ui.util.UIHelper;

public class ShowPropertiesViewHandler extends AbstractHandler {

	public ShowPropertiesViewHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
			if (workbenchWindow != null) {
				IWorkbenchPage page = workbenchWindow.getActivePage();
				if (page != null) {
					try {
						IViewPart viewPart = page.showView(UIHelper.ID_PROPERTIES_VIEW);
						final IEditorPart activeEditor = page.getActiveEditor();
						if (activeEditor != null) {
							activeEditor.setFocus();
						}
						viewPart.setFocus();
					} catch (PartInitException e) {
						CamelEditorUIActivator.pluginLog().logError(e);
					}
				}
			}
		}
		return null;
	}

}

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

package org.fusesource.ide.camel.editor.commands;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.fusesource.ide.camel.editor.EditorMessages;
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;


public class DeleteRouteCommand extends RecordingCommand {
	private final RiderDesignEditor designEditor;
	private final RouteSupport selectedRoute;

	public DeleteRouteCommand(RiderDesignEditor designEditor, TransactionalEditingDomain editingDomain, RouteSupport selectedRoute) {
		super(editingDomain);
		this.designEditor = designEditor;
		this.selectedRoute = selectedRoute;
	}

	@Override
	protected void doExecute() {
		if (selectedRoute != null) {
			boolean deleteIt = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), EditorMessages.deleteRouteCommandLabel, EditorMessages.deleteRouteCommandDescription);
			if (deleteIt) {
				RouteContainer model = designEditor.getModel();
				model.removeChild(selectedRoute);
				RouteSupport newRoute = null;
				if (model.getChildren().size()<1) {
					// no more routes - create one
					designEditor.addNewRoute();
				}
				newRoute = (RouteSupport)model.getChildren().get(0);
				designEditor.clearSelectedRouteCache();
				designEditor.setSelectedRoute(newRoute);
			}
		}
	}

}

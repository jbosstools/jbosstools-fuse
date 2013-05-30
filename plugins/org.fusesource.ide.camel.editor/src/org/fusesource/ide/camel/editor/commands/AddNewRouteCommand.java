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
import org.fusesource.ide.camel.editor.editor.RiderDesignEditor;
import org.fusesource.ide.camel.model.generated.Route;


public class AddNewRouteCommand extends RecordingCommand {

	public static final String ID = "org.fusesource.ide.camel.editor.commands.addRouteCommand";
	
	private final RiderDesignEditor designEditor;

	public AddNewRouteCommand(RiderDesignEditor designEditor, TransactionalEditingDomain editingDomain) {
		super(editingDomain);
		this.designEditor = designEditor;
	}

	@Override
	protected void doExecute() {
		Route route = new Route();
		designEditor.getModel().addChild(route);
		designEditor.setSelectedRoute(route);
	}

}

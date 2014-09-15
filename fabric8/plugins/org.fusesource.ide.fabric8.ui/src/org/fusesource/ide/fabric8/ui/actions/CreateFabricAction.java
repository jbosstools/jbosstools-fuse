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

package org.fusesource.ide.fabric8.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.fusesource.ide.commons.ui.Shells;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.navigator.Fabrics;


public class CreateFabricAction extends Action {
	private final Fabrics fabrics;

	public CreateFabricAction(Fabrics fabrics) {
		super(Messages.fabricAddLabel);
		this.fabrics = fabrics;
		setToolTipText(Messages.fabricAddTooltip);
		setImageDescriptor(FabricPlugin.getPlugin().getImageDescriptor("new_fabric.png"));
	}


	@Override
	public void run() {
		InputDialog dialog = new InputDialog(Shells.getShell(), Messages.fabricAddDialogTitle,
				Messages.fabricAddDialogMessage, "", null);
		int result = dialog.open();
		if (result == Window.OK) {
			String name = dialog.getValue();
			String urls = name;
			createFabric(name, urls);
		}
	}

	protected void createFabric(String name, String urls) {
		fabrics.addFabric(name, urls);
		//fabrics.refresh();
	}
}

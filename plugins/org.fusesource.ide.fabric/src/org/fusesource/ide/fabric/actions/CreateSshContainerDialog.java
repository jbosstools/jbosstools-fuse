/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.fabric.actions;

import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.commons.ui.form.FormDialogSupport;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.fusesource.ide.fabric.navigator.ProfileNode;
import org.fusesource.ide.fabric.navigator.VersionNode;


/**
 * The dialog for creating agents via ssh
 */
public class CreateSshContainerDialog extends FormDialogSupport {

	public CreateSshContainerDialog(VersionNode versionNode, ContainerNode selectedAgent, String defaultAgentName, ProfileNode selectedProfile) {
		setForm(new CreateSshContainerForm(this, versionNode, selectedAgent, defaultAgentName, selectedProfile));
	}

	/*
	 * @see Window#configureShell(Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.createSshAgentTitle);
	}
}
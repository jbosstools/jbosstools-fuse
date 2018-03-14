/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.editor;

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.swt.api.Shell;
import org.eclipse.reddeer.swt.impl.button.CancelButton;
import org.eclipse.reddeer.swt.impl.button.OkButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.swt.widgets.Control;

public class ConfigurationsDialog implements ReferencedComposite {

	public static final String TITLE = "Create new global element...";
	public static final String ROOT = "Red Hat Fuse";

	private Shell shell;

	public ConfigurationsDialog() {
		shell = new DefaultShell(TITLE);
	}

	public ConfigurationsDialog select(ConfigurationsEditor.Element element) {
		new DefaultTree(this).getItem(ROOT, element.getName()).select();
		return this;
	}

	public void ok() {
		new OkButton(this).click();
	}

	public void cancel() {
		new CancelButton(this).click();
	}

	@Override
	public Control getControl() {
		return shell.getSWTWidget();
	}
}

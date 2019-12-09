/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at https://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.fuse.reddeer.preference;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.eclipse.wst.server.ui.RuntimePreferencePage;
import org.eclipse.reddeer.eclipse.wst.server.ui.wizard.NewRuntimeWizardDialog;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.table.DefaultTable;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerKaraf;

/**
 * Represents the Fuse server runtime preference page
 * 
 * @author tsedmik
 */
public class FuseServerRuntimePreferencePage extends RuntimePreferencePage {

	public static final String FINISH_BUTTON = "Finish";
	public static final String EDIT_BUTTON = "Edit...";
	public static final String EDIT_WINDOW = "Edit Server Runtime Environment";
	public static final String INSTALL_DIR = "Home Directory";
	public static final String NAME = "Name";

	public FuseServerRuntimePreferencePage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	/**
	 * Adds a new server runtime.
	 * 
	 * @param path
	 *            installation directory of a server
	 * @param type
	 *            server type (e.g. "JBoss Fuse", "JBoss Fuse 6.1")
	 */
	public void addServerRuntime(ServerKaraf base) {
		NewRuntimeWizardDialog dialog = addRuntime();
		new DefaultTreeItem(base.getCategory(), base.getRuntimeType()).select();
		dialog.next();
		new LabeledText(NAME).setText(base.getRuntimeType());
		new LabeledText(INSTALL_DIR).setText(base.getHome());
		dialog.finish(TimePeriod.DEFAULT);
	}

	/**
	 * Edits the server runtime
	 * 
	 * @param name
	 *            name of the edited server runtime
	 * @param path
	 *            a new installation directory
	 */
	public void editServerRuntime(String name, String path) {
		new DefaultTable(0).select(name);
		new PushButton(EDIT_BUTTON).click();
		new DefaultShell(EDIT_WINDOW).setFocus();
		new LabeledText(INSTALL_DIR).setText(path);
		new PushButton(FINISH_BUTTON).click();
		AbstractWait.sleep(TimePeriod.SHORT);
	}
}

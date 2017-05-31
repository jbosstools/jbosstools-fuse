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
package org.jboss.tools.fuse.qe.reddeer.wizard;

import org.jboss.reddeer.jface.wizard.WizardDialog;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;

public class NewHostWizard extends WizardDialog {
	public static final String SSH_ONLY = "SSH Only";
	public static final String GENERAL = "General";
	public static final String TITLE = "New Connection";
	public static final String HOST_NAME = "Host name:";
	public static final String CONNECTION_NAME = "Connection name:";

	public NewHostWizard() {
		new DefaultShell(TITLE);
	}

	public NewHostWizard setSshOnly() {
		new DefaultTreeItem(GENERAL, SSH_ONLY).select();
		return this;
	}

	public NewHostWizard setHostName(String hostName) {
		new LabeledCombo(HOST_NAME).setText(hostName);
		return this;
	}

	public NewHostWizard setConnectionName(String connectionName) {
		new LabeledText(CONNECTION_NAME).setText(connectionName);
		return this;
	}

}

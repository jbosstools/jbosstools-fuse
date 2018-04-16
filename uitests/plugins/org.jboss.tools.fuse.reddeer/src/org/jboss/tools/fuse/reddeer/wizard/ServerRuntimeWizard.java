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
package org.jboss.tools.fuse.reddeer.wizard;

import java.util.List;

import org.eclipse.reddeer.jface.wizard.WizardDialog;
import org.eclipse.reddeer.swt.impl.button.RadioButton;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.group.DefaultGroup;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * Server Runtime Wizard
 * 
 * @author apodhrad
 * 
 */
public class ServerRuntimeWizard extends WizardDialog {

	public static final String TITLE = "New Server Runtime Environment";
	
	public ServerRuntimeWizard() {
		super(TITLE);
	}

	public void activate() {
		new DefaultShell(TITLE).setFocus();
	}

	public ServerRuntimeWizard setType(String category, String label) {
		new DefaultTreeItem(category, label).select();
		return this;
	}

	public void selectJre(String jreName) {
		if (jreName == null) {
			return;
		}
		new RadioButton(new DefaultGroup(this, "Runtime JRE"), "Alternate JRE: ").click();
		new DefaultCombo(new DefaultGroup(this, "Runtime JRE"), 1).setSelection(jreName);
	}

	public String getExecutionEnvironment() {
		return new DefaultCombo(new DefaultGroup(this, "Runtime JRE"), 0).getSelection();
	}

	public List<String> getExecutionEnvironments() {
		return new DefaultCombo(new DefaultGroup(this, "Runtime JRE"), 0).getItems();
	}

	public void selectExecutionEnvironment(String temp) {
		if (temp == null) return;
		new DefaultCombo(new DefaultGroup(this, "Runtime JRE"), 0).setSelection(temp);
	}

	/*
	 * JBoss EAP
	 */
	public static final String NAME = "Name";
	public static final String HOME_DIR = "Home Directory";
	public static final String CONFIGURATION = "Configuration file: ";

	public ServerRuntimeWizard setName(String name) {
		new LabeledText(NAME).setText(name);
		return this;
	}

	public ServerRuntimeWizard setHomeDirectory(String path) {
		new LabeledText(HOME_DIR).setText(path);
		return this;
	}

	public ServerRuntimeWizard setConfiguration(String configuration) {
		if (configuration != null) {
			new LabeledText(CONFIGURATION).setText(configuration);
		}
		return this;
	}

	/*
	 * JBoss Fuse
	 */
	public static final String INSTALL_DIR = "Home Directory";

	public ServerRuntimeWizard setInstallationDir(String path) {
		new LabeledText(INSTALL_DIR).setText(path);
		return this;
	}
}

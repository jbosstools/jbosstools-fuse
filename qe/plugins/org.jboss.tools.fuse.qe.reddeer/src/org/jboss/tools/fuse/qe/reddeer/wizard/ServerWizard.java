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

import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.jface.wizard.NewWizardDialog;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.button.RadioButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.group.DefaultGroup;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.DefaultText;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;

/**
 * 
 * @author apodhrad
 * 
 */
public class ServerWizard extends NewWizardDialog {

	public static final String HOST_NAME = "Server's host name:";
	public static final String SERVER_NAME = "Server name:";
	public static final String RUNTIME = "Server runtime environment:";
	public static final String REMOTE_SERVER_HOME = "Remote Server Home:";
	public static final String CONNECTION_TYPE_LOCAL = "Local";
	public static final String CONNECTION_TYPE_REMOTE = "Remote";
	public static final String CONNTROLLED_BY_MANAGEMENT = "Management Operations";
	public static final String CONNTROLLED_BY_FILESYSTEM = "Filesystem and shell operations";
	public static final String REMOTE_RUNTIME_DETAILS = "Remote Runtime Details";
	public static final String ASSIGN_RUNTIME = "Assign a runtime to this server";
	public static final String EXTERNALLY_MANAGED = "Server lifecycle is externally managed.";

	public ServerWizard() {
		super("Server", "Server");
	}

	@Override
	public void open() {
		super.open();
		new WaitUntil(new ShellWithTextIsAvailable("New Server"));
		AbstractWait.sleep(TimePeriod.SHORT);
		new DefaultShell("New Server");
	}

	public ServerWizard setType(String category, String label) {
		new DefaultTreeItem(category, label).select();
		return this;
	}

	public ServerWizard setName(String name) {
		new LabeledText(SERVER_NAME).setText(name);
		return this;
	}

	public ServerWizard setRuntime(String runtime) {
		try {
			new DefaultCombo(0).setSelection(runtime);
		} catch (Exception ex) {
			new DefaultCombo(1).setSelection(runtime);
		}
		return this;
	}

	public ServerWizard setRemote() {
		new RadioButton(CONNECTION_TYPE_REMOTE).click();
		return this;
	}

	public ServerWizard setLocal() {
		new RadioButton(CONNECTION_TYPE_LOCAL).click();
		return this;
	}

	public ServerWizard setHostName(String hostName) {
		new LabeledText(HOST_NAME).setText(hostName);
		return this;
	}

	public ServerWizard setUseManagementOperations(boolean mgmt) {
		if (mgmt) {
			new RadioButton(CONNTROLLED_BY_MANAGEMENT).click();
		} else {
			new RadioButton(CONNTROLLED_BY_FILESYSTEM).click();
		}
		return this;
	}

	public ServerWizard setExternallyManaged(boolean value) {
		new CheckBox(EXTERNALLY_MANAGED).toggle(value);
		return this;
	}

	public ServerWizard setAssignRuntime(boolean value) {
		new CheckBox(ASSIGN_RUNTIME).toggle(value);
		return this;
	}

	public NewHostWizard addHost() {
		new PushButton("New Host...").click();
		return new NewHostWizard();
	}

	public ServerWizard setRemoteServerHome(String remoteHome) {
		new DefaultText(new DefaultGroup(REMOTE_RUNTIME_DETAILS), 0).setText(remoteHome);
		return this;

	}

	/*
	 * JBoss Fuse
	 */
	public static final String PORT_NUMBER = "SSH Port: ";
	public static final String USER_NAME = "User Name:";
	public static final String PASSWORD = "Password: ";

	public ServerWizard setPort(String port) {
		new LabeledText(PORT_NUMBER).setText(port);
		return this;
	}

	public ServerWizard setUsername(String username) {
		new LabeledText(USER_NAME).setText(username);
		return this;
	}

	public ServerWizard setPassword(String password) {
		new LabeledText(PASSWORD).setText(password);
		return this;
	}
}

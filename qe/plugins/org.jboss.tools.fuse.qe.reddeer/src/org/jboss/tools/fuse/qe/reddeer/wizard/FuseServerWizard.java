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

import org.jboss.reddeer.eclipse.wst.server.ui.wizard.ModifyModulesPage;
import org.jboss.reddeer.jface.wizard.NewWizardDialog;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.swt.impl.tree.DefaultTreeItem;
import org.jboss.reddeer.common.wait.AbstractWait;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;

/**
 * Wizard for creating a Fuse server.
 * 
 * @author tsedmik
 */
public class FuseServerWizard extends NewWizardDialog {

	private static final String SERVER_SECTION = "JBoss Fuse";
	private static final String HOST_NAME = "Server's host name:";
	private static final String NAME = "Server name:";
	private static final String PORT_NUMBER = "SSH Port: ";
	private static final String USER_NAME = "User Name:";
	private static final String PASSWORD = "Password: ";

	private String type;
	private String name;
	private String hostName;
	private String portNumber;
	private String userName;
	private String password;
	private String[] projects;

	public FuseServerWizard() {
		super("Server", "Server");
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setPortNumber(String portNumber) {
		this.portNumber = portNumber;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getProjects() {
		return projects;
	}

	public void setProjects(String... projects) {
		this.projects = projects;
	}

	public void execute() {
		open();

		new DefaultTreeItem(SERVER_SECTION, type).select();
		if (name != null) {
			new LabeledText(NAME).setText(name);
		}
		if (hostName != null) {
			new LabeledText(HOST_NAME).setText(hostName);
		}

		next();
		closeSecureStorage();

		if (portNumber != null) {
			new LabeledText(PORT_NUMBER).setText(portNumber);
		}
		if (userName != null) {
			new LabeledText(USER_NAME).setText(userName);
		}
		if (password != null) {
			new LabeledText(PASSWORD).setText(password);
		}

		next();
		new ModifyModulesPage().add(projects);

		finish(TimePeriod.VERY_LONG);
	}

	/**
	 * Tries to close 'Secure Storage' dialog window
	 */
	private static void closeSecureStorage() {

		try {
			new WaitUntil(new ShellWithTextIsAvailable("Secure Storage"), TimePeriod.getCustom(5));
		} catch (RuntimeException ex) {
			return;
		}
		new DefaultShell("Secure Storage");
		new LabeledText("Password:").setText("admin");
		new PushButton("OK").click();
		AbstractWait.sleep(TimePeriod.SHORT);
		new DefaultShell("New Server");
	}
}

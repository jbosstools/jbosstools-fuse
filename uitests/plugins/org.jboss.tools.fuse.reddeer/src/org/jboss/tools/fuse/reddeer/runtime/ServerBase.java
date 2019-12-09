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
package org.jboss.tools.fuse.reddeer.runtime;

import java.io.File;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.common.wait.WaitWhile;
import org.eclipse.reddeer.eclipse.condition.ConsoleHasText;
import org.eclipse.reddeer.eclipse.ui.console.ConsoleView;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.Server;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServerLabel;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersView2;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersViewEnums.ServerState;
import org.eclipse.reddeer.requirements.server.ServerRequirementState;
import org.eclipse.reddeer.swt.api.Tree;
import org.eclipse.reddeer.swt.api.TreeItem;
import org.eclipse.reddeer.swt.condition.ShellIsActive;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.menu.ContextMenuItem;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.swt.impl.tree.DefaultTreeItem;
import org.eclipse.reddeer.workbench.core.condition.JobIsRunning;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.tools.fuse.reddeer.preference.InstalledJREs;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerAS;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerEAP;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerFuse;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerKaraf;
import org.jboss.tools.fuse.reddeer.runtime.impl.ServerWildFly;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 
 * @author apodhrad
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
	@JsonSubTypes.Type(name = "as", value = ServerAS.class),
	@JsonSubTypes.Type(name = "eap", value = ServerEAP.class),
	@JsonSubTypes.Type(name = "fuse", value = ServerFuse.class),
	@JsonSubTypes.Type(name = "karaf", value = ServerKaraf.class),
	@JsonSubTypes.Type(name = "wildfly", value = ServerWildFly.class) })
public abstract class ServerBase extends RuntimeBase {

	public static final String ADD_REMOVE_LABEL = "Add and Remove...";
	public static final String DEFAULT_JRE = "default";
	public static final String DEFAULT_EXEC_ENV = "default";

	private String jre;
	private String jreName;
	private String execEnv;
	protected String type;

	public String getJre() {
		return jre != null ? jre : DEFAULT_JRE;
	}

	public void setJre(String jre) {
		if (jre.equals(DEFAULT_JRE)) {
			return;
		}
		File jreFile = new File(jre);
		if (!jreFile.exists()) {
			throw new IllegalArgumentException("JRE path '" + jre + "' doesn't exist.");
		}
		this.jre = jre;
		this.jreName = jreFile.getName();
	}

	public String getJreName() {
		return jreName;
	}

	public String getExecEnv() {
		return execEnv;
	}

	public void setExecEnv(String execEnv) {
		this.execEnv = execEnv;
	}

	public String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	public abstract int getHttpPort();

	public String getRuntimeName() {
		return getName() + " Runtime";
	}

	public void setState(ServerRequirementState requiredState) {
		ServersView2 serversView = new ServersView2();
		serversView.open();
		Server server = serversView.getServer(name);

		ServerState currentState = server.getLabel().getState();
		switch (currentState) {
		case STARTED:
			if (requiredState == ServerRequirementState.STOPPED)
				server.stop();
			break;
		case STOPPED:
			if (requiredState == ServerRequirementState.RUNNING && canStart()) {
				try {
					server.start();
				} catch (Exception e) {
					try {
						server = new ServersView2().getServer(name);
						server.stop();
					} catch (Exception ex) {

					}
					server = new ServersView2().getServer(name);
					server.start();
				}
			}
			break;
		default:
			new AssertionError("It was expected to have server in " + ServerState.STARTED + " or " + ServerState.STOPPED
					+ "state." + " Not in state " + currentState + ".");
		}
	}

	public void deployProject(String project) {
		deployProject(project, "Deployed \"" + project + ".jar\"");
	}

	public void deployProject(String project, String checkPhrase) {
		ConsoleView consoleView = new ConsoleView();
		consoleView.open();
		consoleView.clearConsole();

		ServersView2 serversView = new ServersView2();
		serversView.open();
		Tree tree = new DefaultTree();
		for (TreeItem item : tree.getItems()) {
			ServerLabel serverLabel = new ServerLabel(item);
			if (serverLabel.getName().equals(getName())) {
				item.select();
				new ContextMenuItem(ADD_REMOVE_LABEL).select();
				new DefaultShell(ADD_REMOVE_LABEL);
				new DefaultTreeItem(project).select();
				new PushButton("Add >").click();
				new PushButton("Finish").click();
				new WaitWhile(new ShellIsActive(ADD_REMOVE_LABEL), TimePeriod.LONG);
				new WaitWhile(new JobIsRunning(), TimePeriod.LONG);
				checkDeployment(project, checkPhrase);
				return;
			}
		}
		throw new RuntimeException("Cannot find server '" + getName() + "'");
	}

	protected void checkDeployment(String project, String checkPhrase) {
		new WaitUntil(new ConsoleHasText(checkPhrase), TimePeriod.LONG);
	}

	public String getUrl(String path) {
		return getUrl("localhost", path);
	}

	public abstract String getUrl(String host, String path);

	/**
	 * Returns whether the server can be started.
	 * 
	 * @return whether the server can be started
	 */
	protected boolean canStart() {
		return true;
	}

	/**
	 * Adds new jre if it is defined.
	 */
	protected void addJre() {
		if (jre != null) {
			WorkbenchPreferenceDialog dialog = new WorkbenchPreferenceDialog();
			InstalledJREs page = new InstalledJREs(dialog);
			dialog.open();
			dialog.select(page);
			page.addJre(jre, jreName);
			dialog.ok();
		}
	}

	@Override
	public boolean exists() {
		IServer[] server = ServerCore.getServers();
		for (int i = 0; i < server.length; i++) {
			if (server[i].getId().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean isRemote() {
		return false;
	}

}

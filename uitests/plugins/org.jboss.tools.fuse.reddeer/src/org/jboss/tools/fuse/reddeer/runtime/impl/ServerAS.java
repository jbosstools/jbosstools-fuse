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
package org.jboss.tools.fuse.reddeer.runtime.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.eclipse.wst.server.ui.RuntimePreferencePage;
import org.eclipse.reddeer.eclipse.wst.server.ui.cnf.ServersView2;
import org.eclipse.reddeer.eclipse.wst.server.ui.editor.ServerEditor;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.workbench.core.condition.JobIsKilled;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.runtime.Remote;
import org.jboss.tools.fuse.reddeer.runtime.ServerBase;
import org.jboss.tools.fuse.reddeer.wizard.NewHostWizard;
import org.jboss.tools.fuse.reddeer.wizard.ServerRuntimeWizard;
import org.jboss.tools.fuse.reddeer.wizard.ServerWizard;

/**
 * AS Server
 * 
 * @author Andrej Podhradsky (apodhrad@redhat.com)
 * 
 */
public class ServerAS extends ServerBase {

	public static final int DEFAULT_HTTP_PORT = 8080;
	public static final String DEFAULT_CONFIGURATION = "standalone.xml";

	private final String category = "JBoss Community";

	private final String label = "JBoss AS";

	private Remote remote;

	private String configuration;

	public ServerAS() {
		setType("AS");
	}
	
	public String getConfiguration() {
		return configuration != null ? configuration : DEFAULT_CONFIGURATION;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getCategory() {
		return category;
	}

	public String getServerType() {
		return label + " " + getVersion();
	}

	public String getRuntimeType() {
		return "JBoss " + getVersion() + " Runtime";
	}

	@Override
	public int getHttpPort() {
		return DEFAULT_HTTP_PORT;
	}

	@Override
	public String getUrl(String host, String path) {
		StringBuilder result = new StringBuilder();
		result.append("http://").append(host).append(":").append(getHttpPort()).append("/").append(path);
		return result.toString();
	}

	@Override
	public void create() {
		if (isRemote()) {
			ServerWizard serverWizard = new ServerWizard();
			serverWizard.open();
			serverWizard.setType(getCategory(), getServerType());
			serverWizard.setName(name);
			serverWizard.setHostName(remote.getHost());

			serverWizard.next();
			serverWizard.setRemote();
			serverWizard.setUseManagementOperations(remote.isUseManagementOperations());
			serverWizard.setAssignRuntime(false);
			serverWizard.setExternallyManaged(remote.isExternallyManaged());

			serverWizard.next();

			NewHostWizard hostWizard = serverWizard.addHost().setSshOnly();
			hostWizard.next();
			new WaitUntil(new JobIsKilled("Refreshing server adapter list"), TimePeriod.LONG, false);
			hostWizard.setHostName(remote.getHost()).setConnectionName(remote.getHost()).finish();

			if (!remote.isUseManagementOperations() || !remote.isExternallyManaged()) {
				serverWizard.setRemoteServerHome(remote.getRemoteHome());
			}

			serverWizard.finish();

			ServersView2 servers = new ServersView2();
			servers.open();
			ServerEditor serverEditor = servers.getServer(name).open();
			new LabeledText("User Name").setText(remote.getUsername()); // TODO: move this into ServerEditor
			new LabeledText("Password").setText(remote.getPassword());
			serverEditor.save();

		} else {
			addJre();

			WorkbenchPreferenceDialog preferences = new WorkbenchPreferenceDialog();
			preferences.open();

			// Add runtime
			RuntimePreferencePage runtimePreferencePage = new RuntimePreferencePage(preferences);
			preferences.select(runtimePreferencePage);
			runtimePreferencePage.addRuntime();
			ServerRuntimeWizard runtimeWizard = new ServerRuntimeWizard();
			runtimeWizard.activate();
			runtimeWizard.setType(getCategory(), getRuntimeType());
			runtimeWizard.next();
			runtimeWizard.setName(getRuntimeName());
			runtimeWizard.setHomeDirectory(getHome());
			runtimeWizard.selectJre(getJreName());
			runtimeWizard.selectExecutionEnvironment(getExecEnv());
			new WaitUntil(new JobIsKilled("Refreshing server adapter list"), TimePeriod.LONG, false);
			runtimeWizard.finish(TimePeriod.VERY_LONG);
			preferences.ok();

			// Add server
			ServerWizard serverWizard = new ServerWizard();
			serverWizard.open();
			serverWizard.setType(getCategory(), getServerType());
			serverWizard.setName(getName());
			serverWizard.next();
			serverWizard.setRuntime(getRuntimeName());
			new WaitUntil(new JobIsKilled("Refreshing server adapter list"), TimePeriod.LONG, false);
			serverWizard.finish();
		}
	}

	@Override
	protected boolean canStart() {
		int port = 8080;

		if (isRemote()) {
			boolean portOpen = false;

			try (Socket socket = new Socket(remote.getHost(), port)) {
				portOpen = true;
			} catch (IOException e) {
				portOpen = false;
			}

			if (remote.isExternallyManaged() && !portOpen) {
				throw new RuntimeException("No server running on " + remote.getHost() + " on port " + port);
			}
			if (!remote.isExternallyManaged() && portOpen) {
				throw new RuntimeException("Port '" + port + "' is already in use on " + remote.getHost() + "!");
			}

			return true;
		} else {
			try {
				new ServerSocket(port).close();
				return true;
			} catch (IOException e) {
				throw new RuntimeException("Port '" + port + "' is already in use!", e);
			}
		}
	}

	@Override
	public boolean isRemote() {
		return remote != null;
	}

}

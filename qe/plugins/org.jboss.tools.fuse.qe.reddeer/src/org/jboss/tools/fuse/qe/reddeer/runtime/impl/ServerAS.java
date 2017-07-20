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
package org.jboss.tools.fuse.qe.reddeer.runtime.impl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.core.condition.JobIsKilled;
import org.jboss.reddeer.eclipse.wst.server.ui.RuntimePreferencePage;
import org.jboss.reddeer.eclipse.wst.server.ui.editor.ServerEditor;
import org.jboss.reddeer.eclipse.wst.server.ui.view.ServersView;
import org.jboss.reddeer.swt.impl.text.LabeledText;
import org.jboss.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.qe.reddeer.runtime.Namespaces;
import org.jboss.tools.fuse.qe.reddeer.runtime.Remote;
import org.jboss.tools.fuse.qe.reddeer.runtime.ServerBase;
import org.jboss.tools.fuse.qe.reddeer.wizard.NewHostWizard;
import org.jboss.tools.fuse.qe.reddeer.wizard.ServerRuntimeWizard;
import org.jboss.tools.fuse.qe.reddeer.wizard.ServerWizard;

/**
 * AS Server
 * 
 * @author apodhrad
 * 
 */
@XmlRootElement(name = "as", namespace = Namespaces.SOA_REQ)
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerAS extends ServerBase {

	public static final int DEFAULT_HTTP_PORT = 8080;
	public static final String DEFAULT_CONFIGURATION = "standalone.xml";

	private final String category = "JBoss Community";

	private final String label = "JBoss AS";

	@XmlElement(name = "remote", namespace = Namespaces.SOA_REQ)
	private Remote remote;

	@XmlElement(name = "configuration", namespace = Namespaces.SOA_REQ, defaultValue = DEFAULT_CONFIGURATION)
	private String configuration;

	public String getConfiguration() {
		return configuration;
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
		StringBuffer result = new StringBuffer();
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

			ServersView servers = new ServersView();
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
			RuntimePreferencePage runtimePreferencePage = new RuntimePreferencePage();
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

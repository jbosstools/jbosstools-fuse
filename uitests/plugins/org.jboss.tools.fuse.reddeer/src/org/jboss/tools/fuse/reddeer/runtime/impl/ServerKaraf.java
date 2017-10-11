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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import org.eclipse.reddeer.common.wait.AbstractWait;
import org.eclipse.reddeer.common.wait.TimePeriod;
import org.eclipse.reddeer.common.wait.WaitUntil;
import org.eclipse.reddeer.direct.preferences.Preferences;
import org.eclipse.reddeer.eclipse.wst.server.ui.RuntimePreferencePage;
import org.eclipse.reddeer.swt.condition.ShellIsAvailable;
import org.eclipse.reddeer.swt.impl.button.PushButton;
import org.eclipse.reddeer.swt.impl.shell.DefaultShell;
import org.eclipse.reddeer.swt.impl.text.LabeledText;
import org.eclipse.reddeer.swt.impl.tree.DefaultTree;
import org.eclipse.reddeer.workbench.core.condition.JobIsKilled;
import org.eclipse.reddeer.workbench.ui.dialogs.WorkbenchPreferenceDialog;
import org.jboss.tools.fuse.reddeer.Activator;
import org.jboss.tools.fuse.reddeer.condition.TreeHasItem;
import org.jboss.tools.fuse.reddeer.runtime.ServerBase;
import org.jboss.tools.fuse.reddeer.wizard.ServerRuntimeWizard;
import org.jboss.tools.fuse.reddeer.wizard.ServerWizard;

/**
 * Apache Karaf Server
 * 
 * @author apodhrad, tsedmik
 */
public class ServerKaraf extends ServerBase {

	private final String category = "Apache";

	private final String label = "Karaf";

	private String host;
	private String port;
	private String username;
	private String password;

	public ServerKaraf() {
		setType("Karaf");
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCategory() {
		return category;
	}

	public String getServerType() {
		return label + " " + getVersion() + " Server";
	}

	public String getRuntimeType() {
		return label + " " + getVersion();
	}

	@Override
	public int getHttpPort() {
		Properties systemProperties = new Properties();
		try {
			systemProperties.load(new FileReader(new File(getHome(), "etc/system.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Integer.valueOf(systemProperties.getProperty("org.osgi.service.http.port"));
	}

	@Override
	public String getUrl(String host, String path) {
		StringBuffer result = new StringBuffer();
		result.append("http://").append(host).append(":").append(getHttpPort()).append("/");
		if (path.toLowerCase().endsWith("wsdl")) {
			result.append("cxf").append("/");
		}
		result.append(path);
		return result.toString();
	}

	@Override
	public void create() {
		addJre();

		WorkbenchPreferenceDialog preferences = new WorkbenchPreferenceDialog();
		preferences.open();

		// Add runtime
		RuntimePreferencePage runtimePreferencePage = new RuntimePreferencePage(preferences);
		preferences.select(runtimePreferencePage);
		runtimePreferencePage.addRuntime();
		ServerRuntimeWizard runtimeWizard = new ServerRuntimeWizard();
		new WaitUntil(new TreeHasItem(new DefaultTree(), getCategory(), getRuntimeType()));
		runtimeWizard.setType(getCategory(), getRuntimeType());
		runtimeWizard.next();
		runtimeWizard.setName(getRuntimeName());
		runtimeWizard.setInstallationDir(getHome());
		runtimeWizard.selectJre(getJreName());
		runtimeWizard.selectExecutionEnvironment(getExecEnv());
		new WaitUntil(new JobIsKilled("Refreshing server adapter list"), TimePeriod.LONG, false);
		runtimeWizard.finish(TimePeriod.VERY_LONG);

		preferences.ok();

		// Add server
		ServerWizard serverWizard = new ServerWizard();
		serverWizard.open();
		new WaitUntil(new TreeHasItem(new DefaultTree(), getCategory(), getServerType()));
		serverWizard.setType(getCategory(), getServerType());
		serverWizard.setName(getName());
		serverWizard.setRuntime(getRuntimeName());
		serverWizard.next();
		closeSecureStorage();
		serverWizard.setPort(getPort());
		serverWizard.setUsername(getUsername());
		serverWizard.setPassword(getPassword());
		new WaitUntil(new JobIsKilled("Refreshing server adapter list"), TimePeriod.LONG, false);
		serverWizard.finish();

		// Set ssh home
		Preferences.set("org.eclipse.jsch.core", "SSH2HOME", Activator.getResources(".ssh").getAbsolutePath());

		// Copy host.key
		try {
			Files.copy(this.getClass().getResourceAsStream("/resources/host.key"),
					Paths.get(getHome() + "/etc/host.key"), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Can't copy 'host.key' file!", e);
		}
	}

	@Override
	protected void checkDeployment(String project, String checkPhrase) {
		return;
	}

	/**
	 * Tries to close 'Secure Storage' dialog window
	 */
	private static void closeSecureStorage() {
		try {
			new WaitUntil(new ShellIsAvailable("Secure Storage"), TimePeriod.DEFAULT);
		} catch (RuntimeException ex1) {
			return;
		}
		new DefaultShell("Secure Storage");
		new LabeledText("Password:").setText("admin");
		new PushButton("OK").click();
		AbstractWait.sleep(TimePeriod.SHORT);
		new DefaultShell("New Server");
	}
}

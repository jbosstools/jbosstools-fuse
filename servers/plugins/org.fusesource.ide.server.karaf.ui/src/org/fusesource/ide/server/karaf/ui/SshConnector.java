/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.server.karaf.ui;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.tm.terminal.connector.ssh.launcher.SshLauncherDelegate;
import org.eclipse.tm.terminal.view.core.interfaces.constants.ITerminalsConnectorConstants;
import org.eclipse.ui.IViewPart;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Messages;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegate;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IControllableServerBehavior;


/**
 * Thread used to ping server to test when it is started.
 */
public class SshConnector  {

	private static HashMap<IServer, SshConnector> connectors = new HashMap<>();
	
	private int port;
	private String host;
	private String userName;
	private String passwd;
	private IServer server;
	private IControllableServerBehavior behaviorDelegate;
	private static final String TERMINAL_VIEW_LABEL = Messages.shellViewLabel;
	
	public SshConnector(IServer server) {
		super();
		this.server = server;
		this.behaviorDelegate = (IControllableServerBehavior)server.loadAdapter(IControllableServerBehavior.class, new NullProgressMonitor());
		IKarafServerDelegate config = (IKarafServerDelegate)server.loadAdapter(IKarafServerDelegate.class, new NullProgressMonitor());
		this.host = server.getHost();
		this.port = config.getPortNumber();
		this.userName = config.getUserName();
		this.passwd = config.getPassword();
	}
	/**
	 * creates/establishes a SSH connection with the provided connection data
	 * 
	 * @param server				the server object
	 * @param behaviorDelegate		the behaviour delegate
	 * @param host					the host name
	 * @param port					the port number
	 * @param user					the user name
	 * @param pass					the user password
	 */
	public SshConnector(IServer server, IControllableServerBehavior behaviorDelegate, String host, int port, String user, String pass) {
		super();
		this.server = server;
		this.behaviorDelegate = behaviorDelegate;
		this.host = host;
		this.port = port;
		this.userName = user;
		this.passwd = pass;
	}
	
	

	public void extractData(Map<String, Object> data) {
		if (data == null) {
			return;
		}

    	// set the terminal connector id for ssh
    	data.put(ITerminalsConnectorConstants.PROP_TERMINAL_CONNECTOR_ID, "org.eclipse.tm.terminal.connector.ssh.SshConnector"); //$NON-NLS-1$
		data.put(ITerminalsConnectorConstants.PROP_IP_HOST, host);
		data.put(ITerminalsConnectorConstants.PROP_IP_PORT, Integer.valueOf(port));
		data.put(ITerminalsConnectorConstants.PROP_TIMEOUT, Integer.valueOf(0));
		data.put(ITerminalsConnectorConstants.PROP_SSH_KEEP_ALIVE, Integer.valueOf(300));
		data.put(ITerminalsConnectorConstants.PROP_SSH_PASSWORD, passwd);
		data.put(ITerminalsConnectorConstants.PROP_SSH_USER, userName);
		data.put(ITerminalsConnectorConstants.PROP_ENCODING, StandardCharsets.UTF_8.name());
    }
	
	/**
	 * starts the ssh connection
	 */
	public void start() {
		// open the terminal view
		IViewPart vp = KarafUIPlugin.openTerminalView();
		if (vp == null ) {
			KarafUIPlugin.getLogger().error("Unable to open the terminal view!");
			return;
		}
		
		Map<String, Object> properties = new HashMap<>();
		extractData(properties);
		properties.put(ITerminalsConnectorConstants.PROP_DELEGATE_ID, "org.eclipse.tm.terminal.connector.ssh.launcher.ssh");
		SshLauncherDelegate delegate = new SshLauncherDelegate();
		delegate.execute(properties, null);
	}

	public void onConnect() {
		// store the active connector
		connectors.put(server, this);
		// and open the terminal view
		KarafUIPlugin.openTerminalView().setFocus();
	}

	public void onDisconnect() {
		// open the terminal view
		IViewPart vp = KarafUIPlugin.openTerminalView();
		if (vp == null ) {
			KarafUIPlugin.getLogger().error("Unable to open the terminal view!");
			return;
		}
		// remove from the active connectors
		connectors.remove(server);
	}
	
	public static SshConnector getConnectorForServer(IServer server) {
		return connectors.get(server);
	}
}

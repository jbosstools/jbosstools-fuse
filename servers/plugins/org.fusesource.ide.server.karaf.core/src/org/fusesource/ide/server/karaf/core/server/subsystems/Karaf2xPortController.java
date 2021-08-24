/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.fusesource.ide.server.karaf.core.Activator;
import org.fusesource.ide.server.karaf.core.server.BaseConfigPropertyProvider;
import org.fusesource.ide.server.karaf.core.server.IKarafServerDelegate;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;

/**
 * @author lhein
 */
public class Karaf2xPortController extends AbstractSubsystemController
		implements IServerPortController {

	private static final String KARAF_DATA_PLACEHOLDER = "${karaf.data}";
	private static final String DEFAULT_SHUTDOWN_COMMAND = "SHUTDOWN";
	private static final String SHUTDOWN_COMMAND_PROPERTY = "karaf.shutdown.command";
	public static final String SHUTDOWN_PORT_PROPERTY = "karaf.shutdown.port";
	public static final String SHUTDOWN_PORT_FILE_PROPERTY = "karaf.shutdown.port.file";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fusesource.ide.server.karaf.core.server.subsystems.IServerPortController
	 * #findPort(int, int)
	 */
	@Override
	public int findPort(int id, int defaultValue) {
		int port = defaultValue;

		switch (id) {
		case KEY_JNDI:
			port = findJNDIPort(defaultValue);
			break;
		case KEY_PORT_OFFSET:
			port = findPortOffset(defaultValue);
			break;
		case KEY_WEB:
			port = findWebPort(defaultValue);
			break;
		case KEY_JMX_RMI:
			port = findJMXRMIPort(defaultValue);
			break;
		case KEY_SSH_PORT:
			port = findSSHPort(defaultValue);
			break;
		case KEY_MANAGEMENT_PORT:
			port = findManagementPort(defaultValue);
			break;
		default: // TODO: log an error as we don't support that id
		}

		return port;
	}

	protected int findJNDIPort(int defaultValue) {
		// no JNDI support here
		return defaultValue;
	}

	protected int findPortOffset(int defaultValue) {
		// no Port offset
		return defaultValue;
	}

	protected int findWebPort(int defaultValue) {
		// no web port
		return defaultValue;
	}

	protected int findJMXRMIPort(int defaultValue) {
		// TODO: look for that port in etc/*.karaf.management.cfg
		return defaultValue;
	}

	protected int findSSHPort(int defaultValue) {
		IServer s = getServer();
		if (s != null) {
			IKarafServerDelegate ksd = (IKarafServerDelegate)s.loadAdapter(IKarafServerDelegate.class, new NullProgressMonitor());
			if (ksd != null) {
				return ksd.getPortNumber();
			}
		}
		return defaultValue;
	}

	protected int findManagementPort(int defaultValue) {
		BaseConfigPropertyProvider provider = new BaseConfigPropertyProvider(
				getServerConfigPropertyFile());
		String sPort = provider
				.getEnvironmentResolvedConfigurationProperty(SHUTDOWN_PORT_PROPERTY);
		String sPortFile = provider
				.getEnvironmentResolvedConfigurationProperty(SHUTDOWN_PORT_FILE_PROPERTY);
		if (sPort == null) {
			sPortFile = substitutePlaceHolders(sPortFile);
			sPort = readKarafShutdownPortFromFile(sPortFile);
		}
		try {
			return Integer.parseInt(sPort);
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	protected String substitutePlaceHolders(String value) {
		int idx = value.indexOf(KARAF_DATA_PLACEHOLDER);
		if (idx != -1) {
			// the port file value contains place holder....substitute known
			// values
			return getServer().getRuntime().getLocation().append("data")
					.toOSString()
					+ value.substring(idx + KARAF_DATA_PLACEHOLDER.length());
		}
		return value;
	}

	protected String getShutdownCommand() {
		BaseConfigPropertyProvider provider = new BaseConfigPropertyProvider(
				getServerConfigPropertyFile());
		String cmd = provider
				.getEnvironmentResolvedConfigurationProperty(getShutdownCommandPropertyKey());
		if (cmd == null)
			cmd = getDefaultKarafShutdownCommand();
		return cmd;
	}

	protected File getServerConfigPropertyFile() {
		return getServer().getRuntime().getLocation().append("etc")
				.append("config.properties").toFile();
	}

	protected String getKarafDataPlaceholder() {
		return KARAF_DATA_PLACEHOLDER;
	}

	protected String getDefaultKarafShutdownCommand() {
		return DEFAULT_SHUTDOWN_COMMAND;
	}

	protected String getShutdownCommandPropertyKey() {
		return SHUTDOWN_COMMAND_PROPERTY;
	}

	protected String getShutdownPortPropertyKey() {
		return SHUTDOWN_PORT_PROPERTY;
	}

	protected String getShutdownPortFile() {
		return SHUTDOWN_PORT_FILE_PROPERTY;
	}

	private String readKarafShutdownPortFromFile(String portFile) {
		// no port defined, so look it up from the ports file
		try (BufferedReader br = new BufferedReader(new FileReader(new File(portFile)))) {
			return br.readLine();
		} catch (IOException ex) {
			Activator.getLogger().error(ex);
		}
		return null;
	}
}

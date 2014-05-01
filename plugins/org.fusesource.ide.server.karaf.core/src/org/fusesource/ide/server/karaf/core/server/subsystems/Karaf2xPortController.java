/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.core.server.subsystems;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.fusesource.ide.server.karaf.core.server.BaseKarafConfigPropertyProvider;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;

/**
 * @author lhein
 */
public class Karaf2xPortController extends AbstractSubsystemController
		implements IServerPortController {

	private static final String KARAF_DATA_PLACEHOLDER = "${karaf.data}";

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

	private int findJNDIPort(int defaultValue) {
		// no JNDI support here
		return defaultValue;
	}

	private int findPortOffset(int defaultValue) {
		// no Port offset
		return defaultValue;
	}

	private int findWebPort(int defaultValue) {
		// no web port
		return defaultValue;
	}

	private int findJMXRMIPort(int defaultValue) {
		// TODO: look for that port in etc/*.karaf.management.cfg
		return defaultValue;
	}

	private int findSSHPort(int defaultValue) {
		// TODO: look for that port in etc/*.karaf.shell.cfg
		return defaultValue;
	}

	private int findManagementPort(int defaultValue) {
		BaseKarafConfigPropertyProvider provider = new BaseKarafConfigPropertyProvider();
		File configFile = getServer().getRuntime().getLocation().append("etc")
				.append("config.properties").toFile();
		String sPort = provider.getConfigurationProperty(
				Karaf2xShutdownController.SHUTDOWN_PORT_PROPERTY, configFile);
		String sPortFile = provider.getConfigurationProperty(
				Karaf2xShutdownController.SHUTDOWN_PORT_FILE_PROPERTY,
				configFile);
		if (sPort == null) {
			sPortFile = substitutePlaceHolders(sPortFile);
			sPort = readPortFromFile(sPortFile);
		}
		try {
			return Integer.parseInt(sPort);
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}
	
	private String substitutePlaceHolders(String value) {
		int idx = value.indexOf(KARAF_DATA_PLACEHOLDER);
		if (idx != -1) {
			// the port file value contains place holder....substitute known
			// values
			return getServer().getRuntime().getLocation()
					.append("data").toOSString()
					+ value.substring(idx
							+ KARAF_DATA_PLACEHOLDER.length());
		}
		return value;
	}

	private String readPortFromFile(String portFile) {
		// no port defined, so look it up from the ports file
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(portFile)));
			return br.readLine();
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
}

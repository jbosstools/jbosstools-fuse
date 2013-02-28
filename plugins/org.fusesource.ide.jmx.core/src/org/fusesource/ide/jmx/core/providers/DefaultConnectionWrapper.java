/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    "Rob Stryker" <rob.stryker@redhat.com> - Initial implementation
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.core.providers;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.fusesource.ide.jmx.core.ExtensionManager;
import org.fusesource.ide.jmx.core.IConnectionProvider;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.core.IJMXRunnable;
import org.fusesource.ide.jmx.core.JMXActivator;
import org.fusesource.ide.jmx.core.JMXCoreMessages;
import org.fusesource.ide.jmx.core.JMXException;
import org.fusesource.ide.jmx.core.tree.NodeUtils;
import org.fusesource.ide.jmx.core.tree.Root;


public class DefaultConnectionWrapper implements IConnectionWrapper {
	private JMXServiceURL url;
	private JMXConnector connector;
	private MBeanServerConnection connection;
	private Root root;

	private boolean isConnected = false;
	private Map<String, String[]> environment = new HashMap<String, String[]>();;

	private MBeanServerConnectionDescriptor descriptor;

	public DefaultConnectionWrapper(JMXServiceURL url) {
		this.url = url;
	}

	public DefaultConnectionWrapper(MBeanServerConnectionDescriptor descriptor) throws MalformedURLException {
		this.descriptor = descriptor;
		String username = descriptor.getUserName();
		if (username != null && username.length() > 0) {
			String[] credentials = new String[] { username, descriptor.getPassword() };
			environment.put(JMXConnector.CREDENTIALS, credentials);
		}
		url = new JMXServiceURL(descriptor.getURL());
	}

	@Override
	public JMXConnector getConnector() {
		return connector;
	}

	public MBeanServerConnectionDescriptor getDescriptor() {
		return descriptor;
	}

	public IConnectionProvider getProvider() {
		return ExtensionManager.getProvider(DefaultConnectionProvider.PROVIDER_ID);
	}

	public MBeanServerConnection getConnection() {
		return connection;
	}

	public boolean canControl() {
		return true;
	}

	public synchronized void connect() throws Exception {
		// try to connect
		connector = JMXConnectorFactory.connect(url, environment);
		connection = connector.getMBeanServerConnection();
		isConnected = true;
		fireConnectionChanged();
	}

	public synchronized void disconnect() throws Exception {
		// close
		root = null;
		isConnected = false;
		if (connector != null) {
			try {
				connector.close();
			} finally {
				fireConnectionChanged();
			}
		} else {
			fireConnectionChanged();
		}
		connector = null;
		connection = null;
	}

	protected void fireConnectionChanged() {
		((DefaultConnectionProvider) getProvider()).fireChanged(this);
	}

	public boolean isConnected() {
		return isConnected;
	}

	public Root getRoot() {
		return root;
	}

	public void loadRoot() {
		if (isConnected() && root == null) {
			try {
				root = NodeUtils.createObjectNameTree(this);
			} catch (CoreException ce) {
				// TODO LOG
			}
		}
	}

	public void run(IJMXRunnable runnable) throws CoreException {
		try {
			runnable.run(getConnection());
		} catch (JMXException ce) {
			IStatus s = new Status(IStatus.ERROR, JMXActivator.PLUGIN_ID,
					JMXCoreMessages.DefaultConnection_ErrorRunningJMXCode, ce);
			throw new CoreException(s);
		}
	}
}

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

package org.fusesource.ide.jmx.ui.internal.localjmx;

import java.util.List;

import javax.management.remote.JMXServiceURL;

import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.PartialRefreshableNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.jmx.core.tree.HasRoot;
import org.fusesource.ide.jmx.core.tree.Root;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.IJvmModelChangeListener;
import org.fusesource.ide.jvmmonitor.core.JvmModel;
import org.fusesource.ide.jvmmonitor.core.JvmModelEvent;
import org.fusesource.ide.jvmmonitor.core.mbean.IMBeanServer;


public class JvmConnectionsNode extends PartialRefreshableNode<JvmKey, JvmConnectionWrapper> implements HasRefreshableUI, HasRoot {

	private final Root root;
	private final RefreshableUI contentProvider;
	private IJvmModelChangeListener listener;

	public static JvmConnectionWrapper createKarafConnection(String rootDir) {
		return createKarafConnection(rootDir, "JMX");
	}

	public static JvmConnectionWrapper createKarafConnection(String rootDir, String name) {
		JvmConnectionsNode collection = new JvmConnectionsNode(null, null);
		JvmConnectionWrapper answer = collection.getKarafVMConnection(rootDir);
		if (answer != null) {
			answer.setName(name);
		}
		return answer;
	}

	public JvmConnectionsNode(Root root, RefreshableUI contentProvider) {
		super(root);
		this.root = root;
		this.contentProvider = contentProvider;
	}


	public Root getRoot() {
		return root;
	}

	@Override
	public String toString() {
		return "Local Processes";
	}

	@Override
	public RefreshableUI getRefreshableUI() {
		if (contentProvider instanceof RefreshableUI) {
			return contentProvider;
		}
		return super.getRefreshableUI();
	}

	@Override
	protected Object[] doLoadChildren() {
		try {
			JvmModel model = JvmModel.getInstance();
			if (listener == null) {
				listener = new IJvmModelChangeListener() {
					public void jvmModelChanged(JvmModelEvent e) {
						refresh();
					}
				};
				model.addJvmModelChangeListener(listener);
			}
			List<IHost> hosts = model.getHosts();
			for (IHost host : hosts) {
				String hostName = host.getName();
				List<IActiveJvm> jvms = host.getActiveJvms();
				for (IActiveJvm jvm : jvms) {
					int pid = jvm.getPid();
					JvmKey key = new JvmKey(hostName, pid);
					JvmConnectionWrapper connectionWrapper = getChild(key);
					if (connectionWrapper != null) {
						// update the jvm in case its stopped & restarted with
						// same connection
						connectionWrapper.setActiveJvm(jvm);
						nodeStillExists(connectionWrapper);
					} else {
						IMBeanServer mbeanServer = jvm.getMBeanServer();
						JMXServiceURL jmxUrl = mbeanServer.getJmxUrl();
						if (jmxUrl != null) {
							connectionWrapper = new JvmConnectionWrapper(this, jmxUrl, jvm);
							addChild(key, connectionWrapper);
						}
					}
				}
			}
		} catch (Throwable e) {
			error(e);
		}
		return list.toArray();
	}

	public JvmConnectionWrapper getKarafVMConnection(String rootDir) {
		for (int i = 0; i < 2; i++) {
			if (i > 0) {
				// lets refresh just in case its just started
				refresh();
			}
			Object[] array = getChildObjectArray();
			for (Object node : array) {
				if (node instanceof JvmConnectionWrapper) {
					JvmConnectionWrapper vmc = (JvmConnectionWrapper) node;
					if (vmc.isKaraf(rootDir)) {
						return vmc;
					}
				}
			}
		}
		return null;
	}

	private static void error(Throwable t) {
		JMXUIActivator.getLogger().warning(t);
		Throwable cause = t.getCause();
		if (cause != null) {
			JMXUIActivator.getLogger().warning("Cause: " + cause, cause);
		}
	}
}

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

package org.fusesource.ide.fabric.navigator.jmx;

import java.io.IOException;
import java.util.HashMap;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.RefreshableNode;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.util.Strings;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.fabric.navigator.ContainerNode;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.core.JMXException;
import org.jboss.tools.jmx.core.providers.DefaultConnectionProvider;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.core.tree.NodeUtils;
import org.jboss.tools.jmx.core.tree.Root;
import org.jboss.tools.jmx.ui.ImageProvider;


public class FabricConnectionWrapper extends RefreshableNode implements ImageProvider, IConnectionWrapper, HasRefreshableUI {
	private final ContainerNode agentNode;
	private JMXConnector connector;
	private MBeanServerConnection connection;
	private Root root;
	private boolean isConnected;

	public FabricConnectionWrapper(ContainerNode agentNode) {
		super(agentNode);
		this.agentNode = agentNode;
		String jmxUrl = agentNode.getContainer().getJmxUrl();
		this.isConnected = !Strings.isBlank(jmxUrl);
	}


	@Override
	public String toString() {
		return "JMX";
	}


	@Override
	public IConnectionProvider getProvider() {
		// TODO
		// return null;
		return ExtensionManager.getProvider(DefaultConnectionProvider.PROVIDER_ID);
	}

	@Override
	public boolean canControl() {
		return true;
	}

	@Override
	public RefreshableUI getRefreshableUI() {
		return agentNode.getRefreshableUI();
	}


	@Override
	public synchronized void connect() throws IOException {
		/*
		 * // try to connect connector = JMXConnectorFactory.connect(url,
		 * environment); connection = connector.getMBeanServerConnection();
		 */
		isConnected = true;
		// ((DefaultConnectionProvider)getProvider()).fireChanged(this);
		fireConnectionChanged();
		refresh();
	}

	@Override
	public synchronized void disconnect() throws IOException {
		// close
		root = null;
		isConnected = false;
		/*
		 * try { connector.close(); } finally {
		 * ((DefaultConnectionProvider)getProvider()).fireChanged(this); }
		 */
		connector = null;
		connection = null;
		clearChildren();
		fireConnectionChanged();
		refresh();
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public Root getRoot() {
		return root;
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("releng_gears.gif");
	}

	public void loadRoot() {
		if (isConnected && root == null) {
			try {
				root = NodeUtils.createObjectNameTree(this, new NullProgressMonitor());
			} catch (CoreException ce) {
				// lets create an empty root for now
				root = new Root(this);
				// TODO LOG
			}
		}
	}

	@Override
	protected void loadChildren() {
		root = null;
		loadChildren(this);
	}

	public void loadChildren(Node parent) {
		loadRoot();
		System.err.println("FabricConnectionWrapper:loadChildren(Node)...root == null ? " + (root == null));
		if (root != null) {
			// lets add all the children
			Node[] children = root.getChildren();
			for (Node node : children) {
				parent.addChild(node);
			}
		}
	}

	public void loadChildren(JmxNode jmxNode) {
		loadRoot();
	}

	protected void fireConnectionChanged() {
		DefaultConnectionProvider provider = (DefaultConnectionProvider) getProvider();
		provider.fireChanged(this);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.jmx.core.IConnectionWrapper#run(org.fusesource.ide.jmx.core.IJMXRunnable)
	 */
	@Override
	public void run(IJMXRunnable runnable) throws JMXException {
	}


	@Override
	public void loadRoot(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void run(IJMXRunnable runnable, HashMap<String, String> prefs)
			throws JMXException {
		// TODO Auto-generated method stub
		
	}
}

/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

package org.fusesource.ide.jmx.core.tree;

import java.util.List;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.fusesource.ide.commons.ui.drop.DelegateDropListener;
import org.fusesource.ide.commons.ui.drop.DropHandler;
import org.fusesource.ide.commons.ui.drop.DropHandlerFactory;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.core.JMXActivator;


public class Root extends Node implements Refreshable, DropHandlerFactory {

	private IConnectionWrapper connection;
	private MBeansNode mbeansNode;

	public Root(IConnectionWrapper connection) {
		super(null);
		this.connection = connection;
		mbeansNode = new MBeansNode(this);
		addChild(mbeansNode);
	}

	@Override
	public String toString() {
		return connection != null ? connection.toString() : "Root";
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	public IConnectionWrapper getConnection() {
		return connection;
	}

	public boolean containsDomain(String domain) {
		return getDomainNode(domain) != null;
	}

	public DomainNode getDomainNode(String domain) {
		Node[] nodes = mbeansNode.getChildren();
		for (Node node : nodes) {
			if (node instanceof DomainNode) {
				DomainNode domainNode = (DomainNode) node;
				if (domain.equals(domainNode.getDomain())) {
					return domainNode;
				}
			}
		}
		return null;
	}

	@Override
	public void refresh() {
		IConnectionWrapper wrapper = connection;
		if (wrapper != null) {
			try {
				wrapper.disconnect();
				wrapper.connect();

				// TODO fire better UI stuff here...
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}


	@Override
	public RefreshableUI getRefreshableUI() {
		if (connection instanceof HasRefreshableUI) {
			HasRefreshableUI ui = (HasRefreshableUI) connection;
			return ui.getRefreshableUI();
		}
		return super.getRefreshableUI();
	}

	@Override
	public DropHandler createDropHandler(DropTargetEvent event) {
		final List<Node> children = getChildrenList();
		for (Node node : children) {
			final DropHandler handler = DelegateDropListener.createDropHandler(node, event);
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}

	public static Root getRoot(Node parent) {
		if (parent.getParent() == null) {
			return (Root) parent;
		}
		return getRoot(parent.getParent());
	}

	public MBeansNode getMBeansNode() {
		return mbeansNode;
	}

	public boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	public void connect() {
		if (connection != null) {
			try {
				connection.connect();
			} catch (Exception e) {
				JMXActivator.getLogger().warning("Failed to connect to " + connection + ". " + e, e);
			}
		}
	}


}
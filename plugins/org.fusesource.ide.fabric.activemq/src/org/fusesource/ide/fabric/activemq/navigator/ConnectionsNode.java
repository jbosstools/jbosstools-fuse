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

package org.fusesource.ide.fabric.activemq.navigator;

import java.util.Collection;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.fusesource.fabric.activemq.facade.BrokerFacade;
import org.fusesource.fabric.activemq.facade.ConnectionViewFacade;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.fusesource.ide.commons.ui.ImageProvider;
import org.fusesource.ide.fabric.FabricPlugin;
import org.fusesource.ide.jmx.ui.internal.views.navigator.ContextMenuProvider;


public class ConnectionsNode extends RefreshableCollectionNode implements ImageProvider, ContextMenuProvider {

	private final BrokerNode brokerNode;
	private final BrokerFacade facade;

	public ConnectionsNode(BrokerNode brokerNode) {
		super(brokerNode);
		this.brokerNode = brokerNode;
		this.facade = brokerNode.getFacade();
	}

	@Override
	public String toString() {
		return "Connections";
	}

	@Override
	protected void loadChildren() {
		try {
			Collection<ConnectionViewFacade> list = facade.getConnections();
			if (list != null) {
				for (ConnectionViewFacade mbean : list) {
					addChild(new ConnectionNode(this, mbean));
				}
			}
		} catch (Exception e) {
			brokerNode.handleException(this, e);
		}
	}

	@Override
	public void refresh() {
		// TODO for some reason this doesn't work, so lets refresh the broker
		getBrokerNode().refresh();
	}

	public BrokerNode getBrokerNode() {
		return brokerNode;
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("connection_folder.png");
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
	}

	protected BrokerFacade getFacade() {
		return getBrokerNode().getFacade();
	}

}

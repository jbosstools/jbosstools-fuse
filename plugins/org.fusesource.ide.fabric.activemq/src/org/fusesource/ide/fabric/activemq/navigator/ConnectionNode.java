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

import org.apache.activemq.broker.jmx.ConnectionViewMBean;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.fabric.FabricPlugin;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;




public class ConnectionNode extends NodeSupport implements ImageProvider {

	private final ConnectionViewMBean mbean;

	public ConnectionNode(Node parent, ConnectionViewMBean mbean) {
		super(parent);
		this.mbean = mbean;
		setPropertyBean(mbean);
	}

	public ConnectionViewMBean getMbean() {
		return mbean;
	}

	@Override
	public String toString() {
		return mbean.getRemoteAddress();
	}

	@Override
	public Image getImage() {
		return FabricPlugin.getDefault().getImage("connection.png");
	}
}

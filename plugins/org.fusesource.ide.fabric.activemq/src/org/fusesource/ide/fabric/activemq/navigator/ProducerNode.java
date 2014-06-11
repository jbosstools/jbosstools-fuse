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

import java.util.Collections;
import java.util.List;

import org.apache.activemq.broker.jmx.ProducerViewMBean;
import org.fusesource.ide.commons.tree.ConnectedNode;
import org.fusesource.ide.commons.tree.HasRefreshableUI;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.commons.tree.RefreshableUI;
import org.jboss.tools.jmx.core.tree.Node;




public class ProducerNode extends NodeSupport implements ConnectedNode {
	private final ProducerViewMBean mbean;
	private final Node destinationNode;

	public ProducerNode(Node parent, Node destinationNode, ProducerViewMBean mbean) {
		super(parent);
		this.destinationNode = destinationNode;
		this.mbean = mbean;
		setPropertyBean(mbean);
	}

	public Node getDestinationNode() {
		return destinationNode;
	}

	@Override
	public String toString() {
		try {
			return mbean.getClientId() + "/" + mbean.getSessionId();
		} catch (Exception ex) {
			getRefreshableUI().fireRefresh();
			return "";
		}
	}
	
	public RefreshableUI getRefreshableUI() {
		if (this instanceof RefreshableUI) {
			return (RefreshableUI) this;
		} else if (parent != null && parent instanceof HasRefreshableUI) {
			return ((HasRefreshableUI)parent).getRefreshableUI();
		}
		return null;
	}
	@Override
	public List<Node> getConnectedTo() {
		return Collections.singletonList(getDestinationNode());
	}
}
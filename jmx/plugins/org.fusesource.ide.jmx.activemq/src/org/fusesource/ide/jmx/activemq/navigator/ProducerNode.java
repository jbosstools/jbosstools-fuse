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

package org.fusesource.ide.jmx.activemq.navigator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.activemq.broker.jmx.ProducerViewMBean;
import org.fusesource.ide.foundation.ui.tree.ConnectedNode;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.fusesource.ide.foundation.ui.tree.RefreshableUI;
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
	
	@Override
	public RefreshableUI getRefreshableUI() {
		return this;
	}
	
	@Override
	public List<Node> getConnectedTo() {
		return Collections.singletonList(getDestinationNode());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ProducerNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), mbean, destinationNode);
	}
}
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

import java.util.Objects;

import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.jboss.tools.jmx.core.tree.Node;




public class SubscriptionNode extends NodeSupport {
	private final SubscriptionViewMBean mbean;

	public SubscriptionNode(Node parent, SubscriptionViewMBean mbean) {
		super(parent);
		this.mbean = mbean;
		setPropertyBean(mbean);
	}

	@Override
	public String toString() {
		return mbean.getClientId() + "/" + mbean.getSessionId();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SubscriptionNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), mbean, "Subscription");
	}
}

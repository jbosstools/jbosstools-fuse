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

import org.apache.activemq.broker.jmx.SubscriptionViewMBean;
import org.fusesource.ide.commons.tree.NodeSupport;
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
}

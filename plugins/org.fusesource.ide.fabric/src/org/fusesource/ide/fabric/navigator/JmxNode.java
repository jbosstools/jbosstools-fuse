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

package org.fusesource.ide.fabric.navigator;

import org.fusesource.ide.commons.tree.NodeSupport;

public class JmxNode extends NodeSupport {

	private final ContainerNode agentNode;

	public JmxNode(ContainerNode agentNode) {
		super(agentNode);
		this.agentNode = agentNode;
	}


	@Override
	public String toString() {
		return "JMX " + agentNode.getContainer().getJmxUrl();
	}
}

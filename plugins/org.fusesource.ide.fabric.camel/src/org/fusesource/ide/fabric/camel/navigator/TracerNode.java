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

package org.fusesource.ide.fabric.camel.navigator;

import org.fusesource.ide.commons.tree.NodeSupport;

public class TracerNode extends NodeSupport {
	private final CamelContextNode camelContextNode;

	public TracerNode(CamelContextNode camelContextNode) {
		super(camelContextNode);
		this.camelContextNode = camelContextNode;	// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "Tracer";
	}
	
}

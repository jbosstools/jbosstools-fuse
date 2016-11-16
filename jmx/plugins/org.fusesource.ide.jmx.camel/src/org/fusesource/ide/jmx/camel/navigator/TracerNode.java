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

package org.fusesource.ide.jmx.camel.navigator;

import org.fusesource.ide.foundation.ui.tree.NodeSupport;

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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof TracerNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if(isConnectionAvailable()) {
			return ("CamelTracerNode-" + camelContextNode.getManagementName() + "-" + getConnection().getProvider().getName(getConnection())).hashCode();
		}
		return super.hashCode();
	}
}

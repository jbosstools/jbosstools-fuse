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

package org.fusesource.ide.jmx.camel.navigator;

import java.util.Objects;

import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;

public class ComponentsNode extends RefreshableCollectionNode {
	private final CamelContextNode camelContextNode;

	public ComponentsNode(CamelContextNode camelContextNode) {
		super(camelContextNode);
		this.camelContextNode = camelContextNode;
	}
	@Override
	public String toString() {
		return "Components";
	}
	
	@Override
	protected void loadChildren() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ComponentsNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), camelContextNode);
	}
}

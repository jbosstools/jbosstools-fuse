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

import org.fusesource.ide.commons.tree.RefreshableCollectionNode;
import org.jboss.tools.jmx.core.tree.Node;

public abstract class IdBasedFabricNode extends RefreshableCollectionNode {

	private final Fabric fabric;
	private final String id;
	private final String className;

	public IdBasedFabricNode(Node parent, Fabric fabric, String id) {
		super(parent);
		this.fabric = fabric;
		this.id = id;
		this.className = getClass().getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdBasedFabricNode other = (IdBasedFabricNode) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Fabric getFabric() {
		return fabric;
	}


	@Override
	public String toString() {
		return id;
	}

	public String getId() {
		return id;
	}

}
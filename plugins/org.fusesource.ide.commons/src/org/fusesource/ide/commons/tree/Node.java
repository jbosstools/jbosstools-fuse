/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.commons.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node implements Comparable, HasRefreshableUI {

	protected Node parent;
	private List<Node> children = new ArrayList<Node>();

	public Node(Node parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public Node addChild(Node node) {
		if (!children.contains(node)) {
			children.add(node);
			Collections.sort(children);
			return node;
		} else {
			return children.get(children.indexOf(node));
		}
	}

	public boolean removeChild(Node child) {
		return children.remove(child);
	}

	public Node[] getChildren() {
		return children.toArray(new Node[children.size()]);
	}

	public List<Node> getChildrenList() {
		return children;
	}

	public Node getParent() {
		return parent;
	}

	public void clearChildren() {
		children.clear();
	}

	@Override
	public RefreshableUI getRefreshableUI() {
		if (this instanceof RefreshableUI) {
			return (RefreshableUI) this;
		} else if (parent != null) {
			return parent.getRefreshableUI();
		}
		return null;
	}

}

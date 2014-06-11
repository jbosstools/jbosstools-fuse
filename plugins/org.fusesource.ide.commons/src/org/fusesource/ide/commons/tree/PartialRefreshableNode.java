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

package org.fusesource.ide.commons.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fusesource.ide.commons.Activator;
import org.jboss.tools.jmx.core.tree.Node;


/**
 * Performs partial refreshes of children using a key to store children and detect when they have really been added or removed.
 */
public abstract class PartialRefreshableNode<K,V> extends RefreshableCollectionNode implements GraphableNode, HasChildrenArray {
	protected List<V> list = new ArrayList<V>();

	private Map<K, V> childMap = new HashMap<K, V>();
	private Set<V> addedNodes = new HashSet<V>();
	private Set<V> nodesToRemove = new HashSet<V>();

	public PartialRefreshableNode(Node parent) {
		super(parent);
	}

	@Override
	protected void loadChildren() {
		addedNodes.clear();
		nodesToRemove.clear();
		nodesToRemove.addAll(list);
		doLoadChildren();
		list.removeAll(nodesToRemove);
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.tree.GraphableNode#getChildrenGraph()
	 */
	@Override
	public List<Node> getChildrenGraph() {
		List<Node> answer = new ArrayList<Node>();
		return answer;
	}
	
	@Override
	protected void refreshUI() {
		RefreshableUI ui = getRefreshableUI();
		if (ui != null) {
			if (!addedNodes.isEmpty() || !nodesToRemove.isEmpty()) {
				List<V> nodesToRefresh = new ArrayList<V>();
				nodesToRefresh.addAll(addedNodes);
				nodesToRefresh.addAll(nodesToRemove);
				for (V node : nodesToRefresh) {
					ui.fireRefresh(node, false);
				}
				ui.fireRefresh(this, false);
			}
		} else {
			Activator.getLogger().warning("Could not find RefreshableUI for " + this);
		}
	}

	protected abstract Object[] doLoadChildren();

	public Object[] getChildObjectArray() {
		// force lazy load
		checkLoaded();
		return list.toArray();
	}

	protected void addChild(K key, V connectionWrapper) {
		childMap.put(key, connectionWrapper);
		list.add(connectionWrapper);
		addedNodes.add(connectionWrapper);
	}

	protected boolean nodeStillExists(V connectionWrapper) {
		return nodesToRemove.remove(connectionWrapper);
	}

	protected V getChild(K key) {
		return childMap.get(key);
	}

}
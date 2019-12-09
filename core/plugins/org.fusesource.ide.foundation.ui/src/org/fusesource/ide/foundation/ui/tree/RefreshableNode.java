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

package org.fusesource.ide.foundation.ui.tree;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.util.Nodes;
import org.jboss.tools.jmx.core.tree.Node;


public abstract class RefreshableNode extends NodeSupport implements Refreshable {

	private AtomicBoolean loaded = new AtomicBoolean(false);
	private boolean loading;

	public RefreshableNode(Node parent) {
		super(parent);
	}

	@Override
	public void refresh() {
		clearChildren();
		loadChildren();
		refreshUI();
	}

	@Override
	public boolean removeChild(Node child) {
		boolean answer = super.removeChild(child);
		if (answer) {
			refreshParentUI();
		}
		return answer;
	}

	protected void refreshUI() {
		RefreshableUI ui = getRefreshableUI();
		if (ui != null) {
			ui.fireRefresh(this, false);
		} else {
			FoundationUIActivator.pluginLog().logWarning("Could not find RefreshableUI for " + this);
		}
		// lets try force a properties UI update
		Nodes.refreshSelection(this);
	}

	protected void refreshParentUI() {
		Node p = getParent();
		if (p instanceof RefreshableNode) {
			RefreshableNode pr = (RefreshableNode) p;
			pr.refreshUI();
		} else {
			refreshUI();
		}
	}

	@Override
	public Node[] getChildren() {
		checkLoaded();
		return super.getChildren();
	}


	@Override
	public List<Node> getChildrenList() {
		checkLoaded();
		return super.getChildrenList();
	}

	protected synchronized void checkLoaded() {
		if (!loaded.get()) {
			loading = true;
			try {
				if(!isConnectionAvailable()){
					throw new IllegalStateException("The connection is not available.");
				}
				loadChildren();
				refreshUIAfterLazyLoad();
			} catch (Exception e) {
				FoundationUIActivator.pluginLog().logWarning("Failed to load children of " + this + ". " + e, e);
				loaded.compareAndSet(false, false);
				return;
			} finally {
				loading = false;
			}
			loaded.compareAndSet(false, true);
		}
	}


	protected void refreshUIAfterLazyLoad() {
		// TODO don't do on startup?
		// refreshUI();
	}

	public boolean isLoaded() {
		return loaded.get();
	}
	
	/**
	 * Is the node loading its children (so we don't want to fire change events at this point).
	 */
	protected boolean isLoading() {
		return loading;
	}

	protected abstract void loadChildren();
	
	@Override
	public RefreshableUI getRefreshableUI() {
		return this;
	}
}

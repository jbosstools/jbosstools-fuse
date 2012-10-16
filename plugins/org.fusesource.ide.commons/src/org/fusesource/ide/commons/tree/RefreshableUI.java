package org.fusesource.ide.commons.tree;

/**
 * Represents an object which can cause UIs to be notified of a refresh being required due to model changes
 */
public interface RefreshableUI {

	public abstract void fireRefresh();

	public abstract void fireRefresh(final Object node, final boolean full);

}

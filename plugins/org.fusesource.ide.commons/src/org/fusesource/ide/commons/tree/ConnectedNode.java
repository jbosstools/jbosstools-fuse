package org.fusesource.ide.commons.tree;

import java.util.List;

public interface ConnectedNode {
	/**
	 * Returns the list of nodes this node is connected to for a directed graph.
	 */
	List<?> getConnectedTo();

}

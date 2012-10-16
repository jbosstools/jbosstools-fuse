package org.fusesource.ide.commons.tree;

import java.util.List;

/**
 * Extra helper methods to help show a graph/network view of a node and its descendents
 */
public interface GraphableNode {

	/**
	 * Returns the list of nodes (childen and their descendents) to show in a graph
	 */
	List<Node> getChildrenGraph();

}

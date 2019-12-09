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

import org.jboss.tools.jmx.core.tree.Node;

/**
 * Extra helper methods to help show a graph/network view of a node and its descendents
 */
public interface GraphableNode {

	/**
	 * Returns the list of nodes (childen and their descendents) to show in a graph
	 */
	List<Node> getChildrenGraph();

}

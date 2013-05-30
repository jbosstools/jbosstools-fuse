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

package org.fusesource.ide.camel.editor.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.RouteSupport;
import org.fusesource.ide.commons.tree.ConnectedNode;
import org.fusesource.ide.commons.tree.GraphableNode;
import org.fusesource.ide.commons.tree.GraphableNodeConnected;
import org.fusesource.ide.commons.tree.Node;


public class NodeGraphContentProvider implements  IStructuredContentProvider, IGraphEntityContentProvider {

	private static final Object[] EMPTY = {};

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
	}

	/*
	@Override
	public Object getSource(Object rel) {
		if (rel instanceof Flow) {
			Flow flow = (Flow) rel;
			return flow.getSource();
		}
		return null;
	}

	public Object getDestination(Object rel) {
		if (rel instanceof Flow) {
			Flow flow = (Flow) rel;
			return flow.getTarget();
		}
		return null;
	}
	 */

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		if (input instanceof AbstractNode) {
			AbstractNode node = (AbstractNode) input;

			Set<AbstractNode> set = new HashSet<AbstractNode>();
			RouteContainer parent;
			if (node instanceof RouteContainer) {
				parent = (RouteContainer) node;
			} else {
				parent = node.getParent();
				set.add(node);
			}
			if (parent == null) {
				set.addAll(node.getOutputs());
			} else {
				set.addAll(parent.getDescendents());
			}
			return set.toArray();

			/*

			Set<Flow> set = new HashSet<Flow>();
			RouteContainer parent;
			if (node instanceof RouteContainer) {
				parent = (RouteContainer) node;
			} else {
				parent = node.getParent();
			}
			if (parent == null) {
				set = node.getAllConnections();
			} else {
				Set<AbstractNode> descendents = parent.getDescendents();
				for (AbstractNode child : descendents) {
					set.addAll(child.getAllConnections());
				}
			}
			return set.toArray();
			 */
		} else {
			List<Object> answer = new ArrayList<Object>();
			if (input instanceof GraphableNode) {
				GraphableNode node = (GraphableNode) input;
				answer.addAll(node.getChildrenGraph());
			} else if (input != null) {
				answer.add(input);
				if (input instanceof Node) {
					Node aNode = (Node) input;
					answer.addAll(aNode.getChildrenList());
				}
			} else {
				// will prevent the grayish box in the diagram view
				return null;
			}
			return answer.toArray();
		}
	}


	@Override
	public Object[] getConnectedTo(Object entity) {
		if (entity instanceof GraphableNodeConnected) {
			GraphableNodeConnected gn = (GraphableNodeConnected) entity;
			return gn.getGraphConnectedTo().toArray();
		} else if (entity instanceof RouteSupport) {
			RouteSupport route = (RouteSupport) entity;
			return route.getRootNodes().toArray();
		} else if (entity instanceof AbstractNode) {
			AbstractNode node = (AbstractNode) entity;
			node.getOutputs().toArray();
		}
		/*
		if (entity instanceof Flow) {
			Flow flow = (Flow) entity;
			return new Object[] { flow.getSource() };
		} else if (entity instanceof AbstractNode) {
			AbstractNode node = (AbstractNode) entity;
			List<AbstractNode> outputs;
			if (node instanceof RouteContainer) {
				outputs = ((RouteContainer) node).getSourceNodes();
			} else {
				outputs = node.getTargetNodes();
			}
			outputs.toArray();

		}
		 */
		else if (entity instanceof ConnectedNode) {
			ConnectedNode node = (ConnectedNode) entity;
			return node.getConnectedTo().toArray();
		} else if (entity instanceof Node) {
			Node aNode = (Node) entity;
			return aNode.getChildren();
		}
		return EMPTY;
	}
}

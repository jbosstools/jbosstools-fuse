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

package org.fusesource.ide.jmx.diagram.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteElement;
import org.fusesource.ide.jmx.commons.tree.ConnectedNode;
import org.fusesource.ide.jmx.commons.tree.GraphableNode;
import org.fusesource.ide.jmx.commons.tree.GraphableNodeConnected;
import org.jboss.tools.jmx.core.tree.Node;

public class NodeGraphContentProvider implements  IStructuredContentProvider, IGraphEntityContentProvider {

	private static final Object[] EMPTY = {};

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	@Override
	public Object[] getElements(Object input) {
		if (input instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement node = (AbstractCamelModelElement) input;

			Set<AbstractCamelModelElement> set = new HashSet<>();
			CamelRouteElement parent;
			if (node instanceof CamelRouteElement) {
				parent = (CamelRouteElement) node;
			} else {
				parent = getRoute(node);
				set.add(node);
			}
			if (parent == null) {
				set.add(node.getOutputElement());
			} else {
				getAllChildren(parent.getChildElements(), set);
			}
			return set.toArray();
		} else {
			List<Object> answer = new ArrayList<>();
			if (input instanceof GraphableNode) {
				GraphableNode node = (GraphableNode) input;
				answer.addAll(node.getChildrenGraph());
			} else if (input instanceof Node) {
				return getAllNodeChildren((Node) input, new HashSet<>()).toArray();
			} else {
				// will prevent the grayish box in the diagram view
				return null;
			}
			return answer.toArray();
		}
	}
	
	/**
	 * @param input
	 * @return
	 */
	private Set<Node> getAllNodeChildren(Node node, Set<Node> handledNodes) {
		Set<Node> res = new HashSet<>();
		res.add(node);
		handledNodes.add(node);
		for (Node child : node.getChildrenList()) {
			if (!handledNodes.contains(child)) {
				res.addAll(getAllNodeChildren(child, handledNodes));
			}
		}
		return res;
	}

	private CamelRouteElement getRoute(AbstractCamelModelElement e) {
		AbstractCamelModelElement cme = e;
		while (cme != null && !(cme instanceof CamelRouteElement)) {
			cme = cme.getParent();
		}
		if (cme != null && cme instanceof CamelRouteElement) {
			return (CamelRouteElement)cme;
		}
		return null;
	}

	private void getAllOutputs(AbstractCamelModelElement elem, Set<AbstractCamelModelElement> set) {
		final AbstractCamelModelElement outputElement = elem.getOutputElement();
		if (outputElement != null) {
			set.add(outputElement);
		}
	}

	private void getAllChildren(List<AbstractCamelModelElement> elems, Set<AbstractCamelModelElement> set) {
		for (AbstractCamelModelElement e : elems) {
			set.add(e);
			getAllOutputs(e, set);
			if (!e.getChildElements().isEmpty()) {
				getAllChildren(e.getChildElements(), set);
			}
		}
	}

	@Override
	public Object[] getConnectedTo(Object entity) {
		if (entity instanceof GraphableNodeConnected) {
			GraphableNodeConnected gn = (GraphableNodeConnected) entity;
			return gn.getGraphConnectedTo().toArray();
		} else if (entity instanceof CamelRouteElement) {
			CamelRouteElement route = (CamelRouteElement) entity;
			return route.getInputs().toArray();
		} else if (entity instanceof AbstractCamelModelElement) {
			AbstractCamelModelElement node = (AbstractCamelModelElement) entity;
			return new Object[] {node.getOutputElement()};
		} else if (entity instanceof ConnectedNode) {
			ConnectedNode node = (ConnectedNode) entity;
			return node.getConnectedTo().toArray();
		} else if (entity instanceof Node) {
			Node aNode = (Node) entity;
			return aNode.getChildren();
		}
		return EMPTY;
	}
}

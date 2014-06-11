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

package org.fusesource.ide.fabric.camel.navigator;

import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.model.AbstractNode;
import org.jboss.tools.jmx.core.tree.Node;


public class ProcessorNode extends ProcessorNodeSupport {
	private static final boolean useCaching = true;

	private final RouteNode routeNode;
	private final AbstractNode node;

	public ProcessorNode(RouteNode routeNode, Node parent, AbstractNode node) {
		super(parent, routeNode.getRoute());
		this.routeNode = routeNode;
		this.node = node;
	}

	@Override
	public String toString() {
		return node.getDisplayText();
	}

	@Override
	public AbstractNode getAbstractNode() {
		return node;
	}

	@Override
	public CamelContextNode getCamelContextNode() {
		return routeNode.getCamelContextNode();
	}

	@Override
	protected void loadChildren() {
		List<AbstractNode> children = node.getOutputs();
		for (AbstractNode node : children) {
			addChild(new ProcessorNode(routeNode, this, node));
		}
	}

	@Override
	protected Object createPropertyBean() {
		String nodeId = getNodeId();
		//FabricCamelPlugin.getLogger().debug("" + this + " has nodeId: " + nodeId);
		if (nodeId != null) {
			if (useCaching) {
				return getCamelContextNode().createProcessorBeanView(getRouteId(), nodeId);
			} else {
				return getCamelContextNode().getProcessorMBean(nodeId);
			}
		}
		//return getAbstractNode();
		return super.createPropertyBean();
	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		routeNode.provideContextMenu(menu);
	}

	@Override
	public String getNodeId() {
		return node.getId();
	}

	@Override
	public Image getImage() {
		return node.getSmallImage();
	}
}

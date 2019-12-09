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

package org.fusesource.ide.jmx.camel.navigator;

import java.util.List;
import java.util.Objects;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.jboss.tools.jmx.core.tree.Node;


public class ProcessorNode extends ProcessorNodeSupport {
	private static final boolean useCaching = true;

	private final RouteNode routeNode;
	private final AbstractCamelModelElement node;

	public ProcessorNode(RouteNode routeNode, Node parent, AbstractCamelModelElement node) {
		super(parent, routeNode.getRoute());
		this.routeNode = routeNode;
		this.node = node;
	}

	@Override
	public String toString() {
		return node.getDisplayText();
	}

	@Override
	public CamelContextNode getCamelContextNode() {
		return routeNode.getCamelContextNode();
	}

	@Override
	protected void loadChildren() {
		List<AbstractCamelModelElement> children = node.getChildElements(); //getOutputs()
		if (node.getOutputElement() != null) {
			addChild(new ProcessorNode(routeNode, this, node.getOutputElement()));
		}
		for (AbstractCamelModelElement pnode : children) {
			if (pnode.getInputElement() == null) {
				addChild(new ProcessorNode(routeNode, this, pnode));
			}
		}
	}

	@Override
	protected Object createPropertyBean() {
		String nodeId = getNodeId();
		//CamelJMXPlugin.getLogger().debug("" + this + " has nodeId: " + nodeId);
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
		Image img = CamelJMXPlugin.getDefault().getImage(node.getIconName().replaceAll(".png", "16.png"));
		if (img == null) img = CamelJMXPlugin.getDefault().getImage("generic16.png");
		return img;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ProcessorNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getConnection(), routeNode, node.getId());
	}
}

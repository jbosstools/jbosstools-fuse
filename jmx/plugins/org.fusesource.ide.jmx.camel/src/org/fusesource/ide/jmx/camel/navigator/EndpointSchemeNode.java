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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.tree.Refreshable;
import org.fusesource.ide.foundation.ui.tree.RefreshableCollectionNode;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.jboss.tools.jmx.core.tree.Node;
import org.jboss.tools.jmx.ui.ImageProvider;



public class EndpointSchemeNode extends RefreshableCollectionNode implements ImageProvider, ContextMenuProvider, Refreshable {
	private final EndpointsNode endpointsNode;
	private final String scheme;
	

	public EndpointSchemeNode(EndpointsNode endpointsNode, String scheme) {
		super(endpointsNode);
		this.endpointsNode = endpointsNode;
		this.scheme = scheme;
	}


	public EndpointsNode getEndpointsNode() {
		return endpointsNode;
	}

	
	@Override
	protected void loadChildren() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void refresh() {
		endpointsNode.refresh();
	}


	@Override
	public void provideContextMenu(IMenuManager menu) {
		endpointsNode.addCreateEndpointAction(menu, getScheme() + "://");
	}

	@Override
	public String toString() {
		return scheme;
	}


	public String getScheme() {
		return scheme;
	}

	@Override
	public Image getImage() {
		return CamelJMXPlugin.getDefault().getImage("endpoint_folder.png");
	}


	/**
	 * Returns true if there is an endpoint child with the given URI
	 */
	public boolean containsUri(String uri) {
		List<Node> list = getChildrenList();
		for (Node node : list) {
			if (node instanceof EndpointNode) {
				EndpointNode enode = (EndpointNode) node;
				if (Objects.equal(uri, enode.getEndpointUri())) {
					return true;
				}
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof EndpointSchemeNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return java.util.Objects.hash(getConnection(), scheme);
	}
}

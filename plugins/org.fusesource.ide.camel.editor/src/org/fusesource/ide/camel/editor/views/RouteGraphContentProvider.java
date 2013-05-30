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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphContentProvider;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.Flow;
import org.fusesource.ide.camel.model.RouteContainer;


public class RouteGraphContentProvider implements IGraphContentProvider {

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
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getSource(java.lang.Object)
	 */
	@Override
	public Object getSource(Object rel) {
		if (rel instanceof Flow) {
			Flow flow = (Flow) rel;
			return flow.getSource();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getDestination(java.lang.Object)
	 */
	@Override
	public Object getDestination(Object rel) {
		if (rel instanceof Flow) {
			Flow flow = (Flow) rel;
			return flow.getTarget();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphContentProvider#getElements(java.lang.Object)
	 */
	@Override
	public Object[] getElements(Object input) {
		if (input instanceof Object[]) {
			return (Object[]) input;
		} else if (input instanceof AbstractNode) {
			AbstractNode node = (AbstractNode) input;

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
		}
		return null;
	}
}

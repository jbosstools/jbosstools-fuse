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

package org.fusesource.ide.camel.editor.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;


/**
 * Helper class to track the selection of a node
 */
public class NodeSelectionSupport implements ISelectionListener {
	protected AbstractNode selectedNode;
	private RouteContainer nodeContainer;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		AbstractNode lastNode = selectedNode;
		selectedNode = AbstractNodes.getSelectedNode(selection);
		if (selectedNode != null) {
			if (selectedNode instanceof RouteContainer) {
				nodeContainer = (RouteContainer) selectedNode;
			} else {
				RouteContainer parent = selectedNode.getParent();
				if (parent != null) {
					nodeContainer = parent;
				}
			}

			if (lastNode == selectedNode) {
				// no need to repaint...
				return;
			}
			onNodeChanged(selectedNode);
		}
	}

	/**
	 * Override this method to perform some logic when the selected node changes
	 */
	protected void onNodeChanged(AbstractNode node) {
		//Activator.getLogger().debug("Selection changed: " + node, null);
	}

	public AbstractNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Returns the node container
	 */
	public RouteContainer getNodeContainer() {
		if (nodeContainer == null && selectedNode != null) {
			nodeContainer = selectedNode.getParent();
		}
		return nodeContainer;
	}
}

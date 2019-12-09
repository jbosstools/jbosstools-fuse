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

package org.fusesource.ide.camel.editor.utils;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * Helper class to track the selection of a node
 */
public class NodeSelectionSupport implements ISelectionListener {

	protected AbstractCamelModelElement selectedNode;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		AbstractCamelModelElement lastNode = selectedNode;
		selectedNode = NodeUtils.getSelectedNode(selection);
		if (selectedNode != null) {
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
	protected void onNodeChanged(AbstractCamelModelElement node) {
		//Activator.getLogger().debug("Selection changed: " + node, null);
	}

	public AbstractCamelModelElement getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Returns the node container
	 */
	public AbstractCamelModelElement getNodeContainer() {
		if (selectedNode != null) {
			return selectedNode.getParent();
		}
		return null;
	}
}

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

package org.fusesource.ide.camel.editor.propertysheet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.editor.Activator;
import org.fusesource.ide.camel.editor.editor.NodeSelectionSupport;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.RouteContainer;


public abstract class NodeSectionSupport extends AbstractPropertySection {
	protected AbstractNode node;
	private RouteContainer nodeContainer;
	private WorkbenchPart lastPart;
	private NodeSelectionSupport nodeListener = new NodeSelectionSupport() {
		@Override
		protected void onNodeChanged(AbstractNode node) {
			super.onNodeChanged(node);
			setSelectedNode(node);
		}
	};
	private ISelectionChangedListener selectionChangedListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			nodeListener.selectionChanged(lastPart, event.getSelection());
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput
	 * (org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		if (part instanceof WorkbenchPart) {
			final WorkbenchPart workbenchPart = (WorkbenchPart) part;
			if (lastPart != workbenchPart) {
				lastPart = workbenchPart;
				ISelectionProvider selectionProvider = workbenchPart.getSite()
						.getSelectionProvider();
				// lets remove the listener just in case we've already added it
				try {
					selectionProvider
							.removeSelectionChangedListener(selectionChangedListener);
				} catch (Exception e) {
					// ignore errors
				}
				selectionProvider
						.addSelectionChangedListener(selectionChangedListener);
			}
		}
		// Activator.getLogger().debug("Selection is " + selection +
		// " of type: "
		// + selection.getClass());
		super.setInput(part, selection);

		AbstractNode newNode = getSelectedNode(selection);

		// Activator.getLogger().debug("Selection first element is " + input +
		// " of type: " + (input == null ? null : input.getClass()));
		setSelectedNode(newNode);
	}

	protected AbstractNode getSelectedNode(ISelection selection) {
		return AbstractNodes.getSelectedNode(selection);
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();

		// lets update the node to the current selection
		ISelection selection = null;
		AbstractNode newNode = null;
		if (lastPart != null) {
			 selection = lastPart.getSite().getSelectionProvider().getSelection();
			
			newNode = getSelectedNode(selection);
			if (newNode != null) {
				setSelectedNode(newNode);
			}
		}
		
		Activator.getLogger().debug("After " + this + " about to be shown selection " + selection + " node: " + newNode + " last part: " + lastPart);
	}

	protected void setSelectedNode(AbstractNode newNode) {
		// Activator.getLogger().debug("Property view setting selected node to be: "
		// + newNode);

		if (newNode != null) {
			AbstractNode lastNode = node;
			if (newNode instanceof RouteContainer) {
				nodeContainer = (RouteContainer) newNode;
			} else {
				RouteContainer parent = newNode.getParent();
				if (parent != null) {
					nodeContainer = parent;
				}
			}

			// lets avoid this check just in case we sometimes lose an event
			// if (lastNode == newNode || newNode == null) {
			if (newNode == null) {
				// no need to repaint...
				return;
			}
			node = newNode;
			onNodeChanged(node);
		}
	}

	protected abstract void onNodeChanged(AbstractNode node);

	public AbstractNode getNode() {
		return node;
	}

	/**
	 * Returns the node container
	 */
	public RouteContainer getNodeContainer() {
		if (nodeContainer == null && node != null) {
			nodeContainer = node.getParent();
		}
		return nodeContainer;
	}
}

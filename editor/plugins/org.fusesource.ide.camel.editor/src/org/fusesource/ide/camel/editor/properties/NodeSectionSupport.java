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
package org.fusesource.ide.camel.editor.properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.WorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.fusesource.ide.camel.editor.internal.CamelEditorUIActivator;
import org.fusesource.ide.camel.editor.utils.NodeSelectionSupport;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public abstract class NodeSectionSupport extends AbstractPropertySection {
	protected AbstractCamelModelElement node;
	private WorkbenchPart lastPart;

	private NodeSelectionSupport nodeListener = new NodeSelectionSupport() {
		@Override
		protected void onNodeChanged(AbstractCamelModelElement node) {
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

		AbstractCamelModelElement newNode = getSelectedNode(selection);

		// Activator.getLogger().debug("Selection first element is " + input +
		// " of type: " + (input == null ? null : input.getClass()));
		setSelectedNode(newNode);
	}

	protected AbstractCamelModelElement getSelectedNode(ISelection selection) {
		return NodeUtils.getSelectedNode(selection);
	}

	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();

		// lets update the node to the current selection
		ISelection selection = null;
		AbstractCamelModelElement newNode = null;
		if (lastPart != null) {
			 selection = lastPart.getSite().getSelectionProvider().getSelection();

			newNode = getSelectedNode(selection);
			if (newNode != null) {
				setSelectedNode(newNode);
			}
		}
		if (CamelEditorUIActivator.getDefault().isDebugging()) {
			CamelEditorUIActivator.pluginLog().logInfo("After " + this + " about to be shown selection " + selection + " node: " + newNode + " last part: " + lastPart);
		}
	}

	protected void setSelectedNode(AbstractCamelModelElement newNode) {
		if (newNode != null) {
			AbstractCamelModelElement lastNode = node;
			// lets avoid this check just in case we sometimes lose an event
			// if (lastNode == newNode || newNode == null) {
			node = newNode;
			onNodeChanged(node);
		}
	}

	protected abstract void onNodeChanged(AbstractCamelModelElement node);

	public AbstractCamelModelElement getNode() {
		return node;
	}

	/**
	 * Returns the node container
	 */
	public AbstractCamelModelElement getNodeContainer() {
		if (node != null) {
			return node.getParent();
		}
		return null;
	}
}

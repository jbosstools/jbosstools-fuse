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

package org.fusesource.ide.jmx.ui.internal.actions;


import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.util.Objects;
import org.fusesource.ide.jmx.core.ExtensionManager;
import org.fusesource.ide.jmx.core.IConnectionWrapper;
import org.fusesource.ide.jmx.core.JMXActivator;
import org.fusesource.ide.jmx.core.JMXCoreMessages;
import org.fusesource.ide.jmx.core.tree.Root;
import org.fusesource.ide.jmx.ui.Messages;
import org.fusesource.ide.jmx.ui.internal.JMXImages;


/**
 * @author lhein
 */
public class RefreshAction extends Action implements IWorkbenchWindowActionDelegate {
	@SuppressWarnings("unused")
	private IWorkbenchWindow window;
	private StructuredViewer viewer;
	private String viewId;

	public RefreshAction() {
		super();
	}
	
	/**
	 * creates the refresh action
	 */
	public RefreshAction(String viewId) {
		super();
		this.viewId = viewId;
		Objects.notNull(viewId, "No viewId for RefreshAction");
		setText(Messages.RefreshAction_text);
		setDescription(Messages.RefreshAction_description);
		setToolTipText(Messages.RefreshAction_tooltip);
		JMXImages.setLocalImageDescriptors(this, "refresh.gif");
	}

	/**
	 * Refresh the specified node in the tree viewer/ structured selection.
	 * 
	 * @param onode - node to refresh
	 */
	private void refreshObjectNode(Object onode)
	{
		if (onode == null)
			return;
		
		if (onode instanceof Refreshable) {
			Refreshable refreshable = (Refreshable) onode;
			refreshable.refresh();
			refreshViewer(onode);
		} 
		else {
			IConnectionWrapper wrapper = null;
			
			// Identify the connection wrapper.
			if (onode instanceof IConnectionWrapper) 
				wrapper = (IConnectionWrapper) onode;

			else if (onode instanceof Node) {
				Node node = (Node) onode;
				while ((node != null) && (!(node instanceof Root)))
					node = node.getParent();

				if (node != null) 
					wrapper = ((Root)node).getConnection();
			}
			
			if (wrapper != null) {
				try {
					wrapper.disconnect();
					wrapper.connect();
					refreshViewer(wrapper);
					
					if (viewer instanceof TreeViewer) {
						TreeViewer treeViewer = (TreeViewer) viewer;
						treeViewer.expandToLevel(wrapper, 1);
					}
				} catch (Exception ex) {
				    Status status =
				    	new Status(IStatus.ERROR, JMXActivator.PLUGIN_ID, JMXCoreMessages.RefreshJobFailed,	ex);
					ErrorDialog.openError(Display.getCurrent().getActiveShell(), JMXCoreMessages.RefreshJob, 
							null, status);
				}
			}
		}		
	}  // refreshObjectNode
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb == null) {
			return;
		}

		IWorkbenchWindow aww = wb.getActiveWorkbenchWindow();
		if (aww == null) {
			return;
		}

		IWorkbenchPage ap = aww.getActivePage();
		if (ap == null) {
			return;
		}

		ISelection sel = getSelection(ap);
		if (sel == null) {
			IConnectionWrapper[] connections = ExtensionManager.getAllConnections();
			if (connections.length > 0)
				refreshObjectNode(connections[0]);
		}
		else if (sel instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection)sel;
			refreshObjectNode(treeSelection.getFirstElement());			
		}
		else if (sel instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) sel;
			refreshObjectNode(ss.getFirstElement());			
		}
	}  // run

	protected ISelection getSelection(IWorkbenchPage ap) {
		return ap.getSelection(viewId);
	}

	private void refreshViewer(Object node) {
		if (viewer != null) {
			viewer.refresh(node);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		run();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}
}

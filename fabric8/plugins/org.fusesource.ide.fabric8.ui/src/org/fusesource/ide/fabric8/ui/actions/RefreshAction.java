/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.ui.Selections;

/**
 * @author lhein
 */
public class RefreshAction extends Action implements
		IWorkbenchWindowActionDelegate {

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
		setText(Messages.RefreshAction_text);
		setDescription(Messages.RefreshAction_description);
		setToolTipText(Messages.RefreshAction_tooltip);
	}

	/**
	 * Refresh the specified node in the tree viewer/ structured selection.
	 *
	 * @param onode
	 *            - node to refresh
	 */
	private void refreshObjectNode(Object onode) {
		if (onode == null) return;
		if (onode instanceof Refreshable) {
			Refreshable refreshable = (Refreshable) onode;
			refreshable.refresh();
			refreshViewer(onode);
		}
	} 
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		Object o = getSelectedObject();
		return o instanceof Refreshable;
	}
	
	/*
	 * (non-Javadoc)
	 * 
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
			refreshViewer(null);
		} else if (sel instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) sel;
			refreshObjectNode(treeSelection.getFirstElement());
		} else if (sel instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) sel;
			refreshObjectNode(ss.getFirstElement());
		}
	} 

	protected Object getSelectedObject() {
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb == null) {
			return null;
		}
		IWorkbenchWindow aww = wb.getActiveWorkbenchWindow();
		if (aww == null) {
			return null;
		}
		IWorkbenchPage ap = aww.getActivePage();
		if (ap == null) {
			return null;
		}
		ISelection sel = getSelection(ap);
		return Selections.getFirstSelection(sel);
	}
	
	protected ISelection getSelection(IWorkbenchPage ap) {
		return ap.getSelection(viewId);
	}

	private void refreshViewer(Object node) {
		if (viewer != null) {
			if (node == null) {
				viewer.refresh();
			} else {
				viewer.refresh(node);	
			}			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action
	 * .IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.
	 * IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}
}

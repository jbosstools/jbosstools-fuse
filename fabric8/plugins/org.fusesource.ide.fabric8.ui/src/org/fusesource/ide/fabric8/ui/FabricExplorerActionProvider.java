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
package org.fusesource.ide.fabric8.ui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.fabric8.ui.actions.DoubleClickAction;
import org.fusesource.ide.fabric8.ui.actions.RefreshAction;

/**
 * @author lhein
 */
public class FabricExplorerActionProvider extends CommonActionProvider {

	private ICommonActionExtensionSite site;
	private DoubleClickAction doubleClickAction;
	private RefreshAction refreshAction;
	
	/**
	 * 
	 */
	public FabricExplorerActionProvider() {
		super();
	}
	
	@Override
	public void init(ICommonActionExtensionSite site) {
		super.init(site);
		this.site = site;
		String viewId = site.getViewSite().getId();
		doubleClickAction = new DoubleClickAction();
		refreshAction = new RefreshAction(viewId);
		StructuredViewer viewer = site.getStructuredViewer();
		refreshAction.setViewer(viewer);
		FabricPlugin.getLogger().debug("============================= View ID: " + viewId);
	}

	public StructuredViewer getStructuredViewer() {
		return site.getStructuredViewer();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, doubleClickAction);
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		
		menu.appendToGroup("additions", refreshAction); //$NON-NLS-1$
		
		Object o = Selections.getFirstSelection(site.getStructuredViewer().getSelection());
		if (o instanceof ContextMenuProvider) {
			ContextMenuProvider cmp = (ContextMenuProvider)o;
			cmp.provideContextMenu(menu);
		}
	}
}

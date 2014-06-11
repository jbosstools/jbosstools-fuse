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
package org.fusesource.ide.commons.views.jmx;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.fusesource.ide.commons.ui.ContextMenuProvider;

/**
 * This is a generic common navigator action provider, which delegates
 * all menu additions to the first selected element in the list.
 * If the selected item is a ContextMenuProvider, it will be asked
 * to fill the menu. 
 *  
 */
public class JMXContextMenuProvider extends CommonActionProvider {
	private ICommonActionExtensionSite actionSite;
	
	public JMXContextMenuProvider() {
		super();
	}

	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		this.actionSite = aSite;
	}

	protected void createActions(ICommonActionExtensionSite aSite) {
	}

	public void fillContextMenu(IMenuManager menu) {
		ICommonViewerSite site = actionSite.getViewSite();
		IStructuredSelection selection = null;
		if (site instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) site;
			selection = (IStructuredSelection) wsSite.getSelectionProvider()
					.getSelection();
		}
		if( selection != null && !selection.isEmpty()) {
			Object selected = selection.getFirstElement();
			if( selected instanceof ContextMenuProvider) {
				((ContextMenuProvider)selected).provideContextMenu(menu);
			}
		}
	}
}

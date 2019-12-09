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

package org.fusesource.ide.foundation.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;


public class PageSection implements ISection {

	private final IPage page;
	private TabbedPropertySheetPage tabbedSheetPage;
	private IWorkbenchPart part;
	private ISelection selection;

	public PageSection(IPage page) {
		this.page = page;
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedSheetPage) {
		this.tabbedSheetPage = tabbedSheetPage;

		IPageSite pageSite = getPageSite();
		if (pageSite != null) {
			// lets clear the menu first...
			/*
				IMenuManager menuManager = pageSite.getActionBars().getMenuManager();
				if (menuManager instanceof SubMenuManager) {
					SubMenuManager submm = (SubMenuManager) menuManager;
					submm.removeAll();
				}
				menuManager.setVisible(true);
				pageSite.getActionBars().getToolBarManager().removeAll();
			 */

			if (page instanceof IPageBookViewPage) {
				IPageBookViewPage pageBookViewPage = (IPageBookViewPage) page;
				try {
					pageBookViewPage.init(pageSite);
				} catch (PartInitException e) {
					FoundationUIActivator.pluginLog().logWarning("Failed to initialise page: " + pageBookViewPage + ". " + e, e);
				}
			} else if (page instanceof IViewPage) {
				IViewPage viewPage = (IViewPage) page;
				viewPage.init(pageSite);
			}
		}
		/*
			if (viewSite != null && page instanceof IViewPart) {
				IViewPart viewPart = (IViewPart) page;
				viewPart.init(viewSite);
			}
		 */
		page.createControl(parent);
	}

	protected IPageSite getPageSite() {
		IPageSite pageSite = null;
		if (this.tabbedSheetPage != null) {
			pageSite = this.tabbedSheetPage.getSite();
		}
		return pageSite;
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		this.part = part;
		this.selection = selection;
		if (page instanceof ISelectionListener) {
			ISelectionListener sl = (ISelectionListener) page;
			sl.selectionChanged(part, selection);
		}
	}

	@Override
	public void aboutToBeShown() {
		if (page instanceof ISelectionListener) {
			ISelectionListener sl = (ISelectionListener) page;
			sl.selectionChanged(part, selection);
		}
		try {
			if (page instanceof ViewPropertySheetPage) {
				ViewPropertySheetPage sheetPage = (ViewPropertySheetPage) page;
				IViewPage viewPage = sheetPage.getView();
				if (viewPage instanceof ISection) {
					ISection section = (ISection) viewPage;
					section.aboutToBeShown();
				}
			}
		} catch (Throwable t) {
			FoundationUIActivator.pluginLog().logWarning("aboutToBeShown() Failed with: " + t, t);
		}
	}

	@Override
	public void aboutToBeHidden() {
		try {
			if (page instanceof ViewPropertySheetPage) {
				ViewPropertySheetPage sheetPage = (ViewPropertySheetPage) page;
				IViewPage viewPage = sheetPage.getView();
				if (viewPage instanceof ISection) {
					ISection section = (ISection) viewPage;
					section.aboutToBeHidden();
				}
			}
		} catch (Throwable t) {
			FoundationUIActivator.pluginLog().logWarning("aboutToBeHidden() Failed with: " + t, t);
		}
	}

	protected IMenuManager getMenuManager() {
		IActionBars actionBars = getActionBars();
		IMenuManager menuManager = null;
		if (actionBars != null) {
			menuManager = actionBars.getMenuManager();
		}
		return menuManager;
	}

	protected IToolBarManager getToolBarManager() {
		IActionBars actionBars = getActionBars();
		IToolBarManager answer = null;
		if (actionBars != null) {
			answer = actionBars.getToolBarManager();
		}
		return answer;
	}

	protected IActionBars getActionBars() {
		IPageSite pageSite = getPageSite();
		IActionBars actionBars = null;
		if (pageSite != null) {
			actionBars = pageSite.getActionBars();
		}
		return actionBars;
	}

	@Override
	public void dispose() {
		page.dispose();
	}

	@Override
	public int getMinimumHeight() {
		return 0;
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

}

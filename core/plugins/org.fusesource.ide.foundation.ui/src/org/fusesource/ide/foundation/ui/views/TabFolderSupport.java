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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.ViewPart;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.util.PreferencesHelper;
import org.osgi.service.prefs.Preferences;


public abstract class TabFolderSupport extends ViewPart implements IViewPage {

	private static final String TAB_SELECTION_INDEX = "tabSelectionIndex";
	private CTabFolder tabFolder;
	private IPageSite pageSite;
	/*
	private Action refreshAction;
	private Action doubleClickAction;
	 */
	private List<IPage> pages = new ArrayList<>();

	public TabFolderSupport() {
		super();
	}

	@Override
	public void init(IPageSite pageSite) {
		this.pageSite = pageSite;
	}

	public IPageSite getPageSite() {
		return pageSite;
	}

	public String getId() {
		return getClass().getName();
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());

		tabFolder = new CTabFolder(parent, SWT.BORDER);

		try {
			createTabItems();
		} catch (PartInitException e) {
			FoundationUIActivator.pluginLog().logError("Failed to create tabs: "+ e, e);
		}


		tabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setTabSelectionIndex(tabFolder.getSelectionIndex());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		int lastSelectedIndex = getTabSelectionIndex();
		tabFolder.setSelection(lastSelectedIndex);

		// Create the help context id for the viewer's control
		makeActions();
		contributeToActionBars();

	}

	protected int getTabSelectionIndex() {
		Preferences node = getConfigurationNode();
		int lastSelectedIndex = node.getInt(TAB_SELECTION_INDEX, 0);
		if (!isValidSelectionIndex(lastSelectedIndex)) {
			lastSelectedIndex = 0;
		}
		return lastSelectedIndex;
	}

	protected void setTabSelectionIndex(int index) {
		if (isValidSelectionIndex(index)) {
			Preferences node = getConfigurationNode();
			node.putInt(TAB_SELECTION_INDEX, index);
			PreferencesHelper.flush(node);
		}
	}

	protected Preferences getConfigurationNode() {
		return PreferencesHelper.configurationNode(getId(), "TabFolder");
	}


	protected boolean isValidSelectionIndex(int index) {
		return index >= 0 && index < tabFolder.getItemCount();
	}

	protected abstract void createTabItems() throws PartInitException;


	public CTabItem addPage(String text, IPage page) throws PartInitException {
		IViewSite viewSite = getViewSite();

		pages.add(page);
		if (pageSite != null) {
			if (page instanceof IPageBookViewPage) {
				IPageBookViewPage pageBookViewPage = (IPageBookViewPage) page;
				pageBookViewPage.init(pageSite);
			} else if (page instanceof IViewPage) {
				IViewPage viewPage = (IViewPage) page;
				viewPage.init(pageSite);
			}
		}
		if (viewSite != null && page instanceof IViewPart) {
			IViewPart viewPart = (IViewPart) page;
			viewPart.init(viewSite);
		}
		Composite pageComposite = new Composite(tabFolder, SWT.NONE);
		pageComposite.setLayout(new FillLayout());
		page.createControl(pageComposite);
		return addTabItem(text, pageComposite);
	}


	protected CTabItem addTabItem(String text, Control control) {
		CTabItem item = new CTabItem(tabFolder, SWT.NONE);
		item.setControl(control);
		item.setText(text);
		return item;
	}

	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				TabFolderSupport.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(tabFolder);
		tabFolder.setMenu(menu);

		/*
		IWorkbenchPartSite site = getSite();
		if (site != null) {
			site.registerContextMenu(menuMgr, tabFolder);
		} else if (pageSite != null) {
			// TODO use a different ID?
			pageSite.registerContextMenu(getHelpID(), menuMgr, viewer);
		} else {
			warnNoSite();
		}
		 */
	}

	protected IWorkbenchPart getPart() {
		IWorkbenchPartSite site = getSite();
		IWorkbenchPart answer = null;
		if (site != null) {
			answer  = site.getPart();
		}
		if (answer == null) {
			IPageSite ps = getPageSite();
			if (ps != null) {
				answer = ps.getPage().getActivePart();
			}
		}
		return answer;

	}
	protected void contributeToActionBars() {
		IActionBars bars;
		if (getViewSite() != null) {
			bars = getViewSite().getActionBars();
		} else if (pageSite != null) {
			bars = pageSite.getActionBars();
		} else {
			warnNoSite();
			return;
		}
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	protected void warnNoSite() {
		FoundationUIActivator.pluginLog().logWarning("No IViewSite or IPageSite registered for " + this);
	}

	protected void fillLocalPullDown(IMenuManager manager) {
		/*
		manager.add(refreshAction);
		 */
	}

	protected void fillContextMenu(IMenuManager manager) {
		/*
		manager.add(refreshAction);
		 */
	}

	protected void fillLocalToolBar(IToolBarManager manager) {
		/*
		manager.add(refreshAction);
		 */
	}

	protected void makeActions() {
		/*
		refreshAction = new Action() {
			@Override
			public void run() {
				refresh();
			}
		};
		refreshAction.setText("Refresh");

		Activator.setLocalImageDescriptors(refreshAction, "refresh.gif"); //$NON-NLS-1$
		doubleClickAction = new Action() {
			@Override
			public void run() {
				ISelection selection = viewer.getSelection();
				doubleClickSelection(selection);
			}
		};
		 */
	}


	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		tabFolder.setFocus();
	}

	public CTabFolder getTabFolder() {
		return tabFolder;
	}
}
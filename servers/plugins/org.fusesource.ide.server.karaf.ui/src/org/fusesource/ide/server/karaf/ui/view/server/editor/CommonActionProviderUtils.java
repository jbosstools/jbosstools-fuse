/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.server.karaf.ui.view.server.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.cnf.ServerActionProvider;

/**
 * This class is copied from package
 *   org.jboss.ide.eclipse.as.ui.views.server.extensions
 *   
 * And may be a candidate for API cleanup. 
 * 
 * @author André Dietisheim
 */
public class CommonActionProviderUtils {
	
	private static final String SHOW_IN_QUICK_MENU_ID = ServerActionProvider.SHOW_IN_MENU_ID;

	public static ICommonViewerWorkbenchSite getCommonViewerWorkbenchSite(ICommonActionExtensionSite actionExtensionSite) {
		ICommonViewerWorkbenchSite wsSite = null;
		ICommonViewerSite viewSite = actionExtensionSite.getViewSite();
		if( viewSite instanceof ICommonViewerWorkbenchSite ) {
			StructuredViewer v = actionExtensionSite.getStructuredViewer();
			if( v instanceof CommonViewer ) {
				wsSite = (ICommonViewerWorkbenchSite) viewSite;
			}
		}
		return wsSite;
	}

	public static IStructuredSelection getSelection(ICommonActionExtensionSite actionExtensionSite) {
		IStructuredSelection structuredSelection = null;
		ICommonViewerWorkbenchSite workbenchSite = getCommonViewerWorkbenchSite(actionExtensionSite);
		if (workbenchSite != null) {
			ISelectionProvider selectionProvider = workbenchSite.getSelectionProvider();
			if (selectionProvider != null) {
				ISelection selection = selectionProvider.getSelection();
				if (selection instanceof IStructuredSelection) {
					structuredSelection = (IStructuredSelection) selection;
				}
			}
		}
		return structuredSelection;
	}
	
	public static boolean isServerSelected(IStructuredSelection selection) {
		return selection != null
				&& selection.getFirstElement() instanceof IServer; 
	}

	public static IContributionItem getShowInQuickMenu(IMenuManager menuManager) {
		return getShowInQuickMenu(menuManager, false);
	}

	public static IContributionItem getShowInQuickMenu(IMenuManager menuManager, boolean createShowInMenu) {
		IContributionItem item = null;
		if (menuManager != null) {
			item = menuManager.find(SHOW_IN_QUICK_MENU_ID);
			if(item==null && createShowInMenu) {
				String text = Messages.actionShowIn;
				final IWorkbench workbench = PlatformUI.getWorkbench();
				final IBindingService bindingService = (IBindingService) workbench
						.getAdapter(IBindingService.class);
				final TriggerSequence[] activeBindings = bindingService
						.getActiveBindingsFor(SHOW_IN_QUICK_MENU_ID);
				if (activeBindings.length > 0) {
					text += "\t" + activeBindings[0].format();
				}
				item = new MenuManager(text, SHOW_IN_QUICK_MENU_ID);
				menuManager.insertAfter(ServerActionProvider.TOP_SECTION_END_SEPARATOR, item);
			}
		}
		return item;
	}

	public static void addToShowInQuickSubMenu(IAction action, IMenuManager menu, ICommonActionExtensionSite actionSite) {
		IStructuredSelection selection = CommonActionProviderUtils.getSelection(actionSite);
		IContributionItem menuItem = CommonActionProviderUtils.getShowInQuickMenu(menu);
		if (menuItem instanceof MenuManager
			&& CommonActionProviderUtils.isServerSelected(selection) 
			&& action != null) {
			((MenuManager) menuItem).add(action);
		}

	}
	
	public static IWorkbenchPart getWorkbenchPart(String id) {
		IWorkbenchPart part = null;
		IWorkbenchPage page = getActiveWorkbenchPage();
		if (page != null) {
			part = page.findView(id);
		}
		return part;
	}

	public static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchPage page = null; 
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			page = window.getActivePage();
		}
		return page;
	}

	public static IWorkbenchPart showView(String partId) throws PartInitException {
		IWorkbenchPart part = CommonActionProviderUtils.getWorkbenchPart(partId);
		if (part == null) {
			part = CommonActionProviderUtils.getActiveWorkbenchPage().showView(partId);
		}
		return part;
	}
}

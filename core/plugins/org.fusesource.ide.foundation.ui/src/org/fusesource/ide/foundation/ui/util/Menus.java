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

package org.fusesource.ide.foundation.ui.util;

import java.util.Collection;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.fusesource.ide.foundation.core.util.Objects;


public class Menus {

	public static MenuItem getMenuItemByText(Menu menu, String label) {
		MenuItem[] items = menu.getItems();
		for (MenuItem item : items) {
			if (Objects.equal(label,  item.getText())) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Disposes (clears) the menu items
	 */
	public static void disposeItems(Menu menu) {
		MenuItem[] items = menu.getItems();
		for (MenuItem item : items) {
			Menu childMenu = item.getMenu();
			if (childMenu != null && !childMenu.isDisposed()) {
				disposeItems(childMenu);
				childMenu.dispose();
			}
			if (!item.isDisposed()) {
				item.dispose();
			}
		}
	}

	/**
	 * Adds the action or contribution item to the menu only if its not already been added
	 */
	public static void addAction(IContributionManager manager, Object actionOrContributionItem) {
		if (actionOrContributionItem instanceof IAction) {
			IAction action = (IAction) actionOrContributionItem;
			String id = getActionId(action);
			if (id == null || manager.find(id) != null) {
				return;
			}
			manager.add(action);
		} else if (actionOrContributionItem instanceof IContributionItem) {
			IContributionItem contributionItem = (IContributionItem) actionOrContributionItem;
			String id = getContributionItemId(contributionItem);
			if (id == null || manager.find(id) != null) {
				return;
			}
			manager.add(contributionItem);
		}
	}

	public static void addAction(IContributionManager manager, Collection<?> collection) {
		for (Object action : collection) {
			addAction(manager, action);
		}
	}

	public static void addAction(IToolBarManager manager, Collection<?> collection) {
		for (Object action : collection) {
			addAction(manager, action);
		}
	}

	public static void removeAction(IContributionManager manager, Collection<?> collection) {
		for (Object action : collection) {
			removeAction(manager, action);
		}
	}

	public static void removeAction(IMenuManager manager, Collection<?> collection) {
		for (Object action : collection) {
			removeAction(manager, action);
		}
	}

	public static void removeAction(IToolBarManager manager, Collection<?> collection) {
		for (Object action : collection) {
			removeAction(manager, action);
		}
	}

	public static void removeAction(IContributionManager manager, Object actionOrContributionItem) {
		if (actionOrContributionItem instanceof IContributionItem) {
			IContributionItem item = (IContributionItem) actionOrContributionItem;
			manager.remove(item);
			String id = getContributionItemId(item);
			if (id != null) {
				manager.remove(id);
			}
		} else if (actionOrContributionItem instanceof IAction) {
			IAction action = (IAction) actionOrContributionItem;
			String id = getActionId(action);
			if (id != null) {
				manager.remove(id);
			}
		}
	}

	protected static String getActionId(IAction action) {
		String id = action.getId();
		return id;
	}

	protected static String getContributionItemId(IContributionItem item) {
		String id = item.getId();
		return id;
	}


}

/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.commons.ui.Trees;
import org.fusesource.ide.jvmmonitor.core.ISWTResourceElement;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;


/**
 * The thread filtered tree.
 */
public class SWTResourceFilteredTree extends FilteredTree {

	/** The action bars. */
	private IActionBars actionBars;

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param actionBars
	 *            The action bars
	 */
	protected SWTResourceFilteredTree(Composite parent, IActionBars actionBars) {
		super(parent, SWT.MULTI | SWT.FULL_SELECTION, new PatternFilter(), true);
		this.actionBars = actionBars;

		configureTree();
		createContextMenu();
		setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	}

	/*
	 * @see FilteredTree#createControl(Composite, int)
	 */
	@Override
	protected void createControl(Composite composite, int treeStyle) {
		super.createControl(composite, treeStyle);

		// adjust the indentation of filter composite
		GridData data = (GridData) filterComposite.getLayoutData();
		data.horizontalIndent = 2;
		data.verticalIndent = 2;
		filterComposite.setLayoutData(data);
	}

	/**
	 * Updates the status line.
	 * 
	 * @param resourceElements
	 *            The SWT resource elements
	 */
	public void updateStatusLine(ISWTResourceElement[] resourceElements) {
		IStatusLineManager manager = actionBars.getStatusLineManager();

		IContributionItem[] items = manager.getItems();
		StatusLineContributionItem resourceCountContributionItem = null;
		for (IContributionItem item : items) {
			if (item instanceof StatusLineContributionItem) {
				resourceCountContributionItem = (StatusLineContributionItem) item;
			}
		}

		// create the status line
		if (resourceCountContributionItem == null) {
			resourceCountContributionItem = new StatusLineContributionItem(
					"ResourceCountContributionItem"); //$NON-NLS-1$
			manager.add(resourceCountContributionItem);
		}

		if (resourceElements == null) {
			resourceCountContributionItem.setText(Util.ZERO_LENGTH_STRING);
			return;
		}

		Map<String, Integer> resources = new HashMap<String, Integer>();
		for (ISWTResourceElement resourceElement : resourceElements) {
			String name = resourceElement.getName().split(" ")[0]; //$NON-NLS-1$
			Integer count = resources.get(name);
			resources.put(name, count == null ? 1 : ++count);
		}

		// set text on status line
		List<String> list = new ArrayList<String>(resources.keySet());
		Collections.sort(list);
		StringBuffer buffer = new StringBuffer();
		buffer.append("Total: ").append(resourceElements.length); //$NON-NLS-1$
		for (String name : list) {
			buffer.append(", ").append(name); //$NON-NLS-1$
			buffer.append(": ").append(resources.get(name)); //$NON-NLS-1$
		}
		resourceCountContributionItem.setText(buffer.toString());
	}

	/**
	 * Configure the tree adding columns.
	 */
	private void configureTree() {
		Trees.disposeColumns(getViewer());

		getViewer().getTree().setLinesVisible(true);
		getViewer().getTree().setHeaderVisible(true);

		TreeColumn treeColumn = new TreeColumn(getViewer().getTree(), SWT.NONE);
		treeColumn.setText(Messages.nameColumnLabel);
		treeColumn.setWidth(500);
		treeColumn.setAlignment(SWT.LEFT);
		treeColumn.setToolTipText(Messages.nameColumnToolTip);
	}

	/**
	 * Creates the context menu.
	 */
	private void createContextMenu() {
		final CopyAction copyAction = CopyAction.createCopyAction(actionBars);
		getViewer().getControl().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				getViewer().removeSelectionChangedListener(copyAction);
			}

			@Override
			public void focusGained(FocusEvent e) {
				getViewer().addSelectionChangedListener(copyAction);
			}
		});

		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(copyAction);
			}
		});

		Menu menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
	}
}

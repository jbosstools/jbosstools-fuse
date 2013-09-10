/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.thread;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.commons.ui.Trees;
import org.fusesource.ide.commons.ui.actions.ConfigureColumnsAction;
import org.fusesource.ide.commons.ui.config.ColumnConfiguration;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;


/**
 * The thread filtered tree.
 */
public class ThreadFilteredTree extends ConfigurableFilteredTree {

	/** The configure columns action. */
	ConfigureColumnsAction configureColumnsAction;

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param actionBars
	 *            The action bars
	 */
	protected ThreadFilteredTree(Composite parent, IActionBars actionBars) {
		super(parent, SWT.MULTI | SWT.FULL_SELECTION, new PatternFilter(), true);

		createContextMenu(actionBars);
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

	/*
	 * @see IConfigurableColumn#getColumns()
	 */
	@Override
	public List<String> getColumns() {
		ArrayList<String> columnLabels = new ArrayList<String>();
		ThreadColumn[] values = ThreadColumn.values();
		for (ThreadColumn value : values) {
			columnLabels.add(value.label);
		}
		return columnLabels;
	}

	/*
	 * @see IConfigurableColumn#getId()
	 */
	@Override
	public String getColumnConfigurationId() {
		return getClass().getName();
	}

	/*
	 * @see IConfigurableColumn#getDefaultVisibility(String)
	 */
	@Override
	public boolean getDefaultVisibility(String column) {
		return true;
	}

	/**
	 * Configure the tree adding columns.
	 */
	@Override
	protected void configureTree() {
		Trees.disposeColumns(getViewer());

		getViewer().getTree().setLinesVisible(true);
		getViewer().getTree().setHeaderVisible(true);

		List<ColumnConfiguration> columns = getConfiguration().getColumnConfigurations();
		for (ColumnConfiguration config : columns) {
			if (!config.isVisible()) {
				continue;
			}
			ThreadColumn column = ThreadColumn.getColumn(config.getName());

			TreeColumn treeColumn = new TreeColumn(getViewer().getTree(), SWT.NONE);
			treeColumn.setText(column.label);
			treeColumn.setWidth(column.defalutWidth);
			treeColumn.setAlignment(column.initialAlignment);
			treeColumn.setToolTipText(column.toolTip);
			treeColumn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (e.widget instanceof TreeColumn) {
						sortColumn((TreeColumn) e.widget);
					}
				}
			});
		}
		getConfiguration().addColumnListeners(getViewer());
	}

	/**
	 * Sorts the tree with given column.
	 * 
	 * @param treeColumn
	 *            the tree column
	 */
	void sortColumn(TreeColumn treeColumn) {
		int columnIndex = getViewer().getTree().indexOf(treeColumn);
		ThreadComparator sorter = (ThreadComparator) getViewer()
				.getComparator();

		if (sorter != null && columnIndex == sorter.getColumnIndex()) {
			sorter.reverseSortDirection();
		} else {
			sorter = new ThreadComparator(columnIndex);
			getViewer().setComparator(sorter);
		}
		getViewer().getTree().setSortColumn(treeColumn);
		getViewer().getTree().setSortDirection(sorter.getSortDirection());
		getViewer().refresh();
	}

	/**
	 * Creates the context menu.
	 * 
	 * @param actionBars
	 *            The action bars
	 */
	private void createContextMenu(IActionBars actionBars) {
		configureColumnsAction = new ConfigureColumnsAction(this);
		final CopyAction copyAction = CopyAction.createCopyAction(actionBars);
		getViewer().addSelectionChangedListener(copyAction);

		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(copyAction);
				manager.add(new Separator());
				manager.add(configureColumnsAction);
			}
		});

		Menu menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
	}
}

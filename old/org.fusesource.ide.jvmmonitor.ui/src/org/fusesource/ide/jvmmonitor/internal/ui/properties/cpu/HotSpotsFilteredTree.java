/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.fusesource.ide.commons.ui.Trees;
import org.fusesource.ide.commons.ui.config.ColumnConfiguration;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu.HotSpotsComparator.ColumnType;


/**
 * The hot spots filtered tree.
 */
public class HotSpotsFilteredTree extends AbstractFilteredTree {

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param actionBars
	 *            The action bars
	 */
	public HotSpotsFilteredTree(Composite parent, IActionBars actionBars) {
		super(parent, actionBars);
	}

	/*
	 * @see IConfigurableColumn#getColumns()
	 */
	@Override
	public List<String> getColumns() {
		ArrayList<String> columnLabels = new ArrayList<String>();
		HotSpotsColumn[] values = HotSpotsColumn.values();
		for (HotSpotsColumn value : values) {
			if (value == HotSpotsColumn.HOT_SPOT) {
				columnLabels.add(getMethodColumnName());
			} else {
				columnLabels.add(value.label);
			}
		}
		return columnLabels;
	}

	public void updateColumnConfiguration() {
		configureTree();
	}

	/*
	 * @see IConfigurableColumn#getDefaultVisibility(String)
	 */
	@Override
	public boolean getDefaultVisibility(String column) {
		return true;
	}

	/*
	 * @see AbstractFilteredTree#getViewerType()
	 */
	@Override
	public ViewerType getViewerType() {
		return ViewerType.HotSpots;
	}

	/*
	 * @see AbstractFilteredTree#configureTree()
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
			HotSpotsColumn column = getColumn(config.getName());

			TreeColumn treeColumn = new TreeColumn(getViewer().getTree(), SWT.NONE);
			String label = (column == HotSpotsColumn.HOT_SPOT) ? getMethodColumnName()
					: column.label;
			String toolTip = (column == HotSpotsColumn.HOT_SPOT) ? getMethodColumnToolTip()
					: column.toolTip;

			treeColumn.setText(label);
			treeColumn.setWidth(column.defalutWidth);
			treeColumn.setAlignment(column.alignment);
			treeColumn.setToolTipText(toolTip);
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
	 * Sorts the table with given column.
	 * 
	 * @param treeColumn
	 *            the tree column
	 */
	public void sortColumn(TreeColumn treeColumn) {
		ColumnType columnType;
		String columnText = treeColumn.getText();
		if (columnText.equals(getMethodColumnName())) {
			columnType = ColumnType.Methods;
		} else if (columnText.equals(HotSpotsColumn.SELFTIME_MS.label)) {
			columnType = ColumnType.TimeMs;
		} else if (columnText.equals(HotSpotsColumn.SELFTIME_PERCENTAGE.label)) {
			columnType = ColumnType.TimePercentage;
		} else if (columnText.equals(HotSpotsColumn.COUNT.label)) {
			columnType = ColumnType.Count;
		} else {
			throw new IllegalArgumentException("Unknown column"); //$NON-NLS-1$
		}

		// sort the tree items
		HotSpotsComparator comparator = new HotSpotsComparator(columnType);
		if (treeColumn.equals(getViewer().getTree().getSortColumn())
				&& getViewer().getTree().getSortDirection() == comparator
				.getSortDirection()) {
			comparator.reverseSortDirection();
		}
		getViewer().setComparator(comparator);

		// update sort indicator on tree
		getViewer().getTree().setSortColumn(treeColumn);
		getViewer().getTree().setSortDirection(comparator.getSortDirection());
		getViewer().refresh();
	}

	/**
	 * Gets the method column name.
	 * 
	 * @return The method column name
	 */
	protected String getMethodColumnName() {
		return HotSpotsColumn.HOT_SPOT.label;
	}

	/**
	 * Gets the method column tooltip.
	 * 
	 * @return The method column tooltip
	 */
	protected String getMethodColumnToolTip() {
		return HotSpotsColumn.HOT_SPOT.toolTip;
	}

	/**
	 * Gets the column corresponding to the givel label.
	 * 
	 * @param label
	 *            The label
	 * @return The column
	 */
	private HotSpotsColumn getColumn(String label) {
		for (HotSpotsColumn column : HotSpotsColumn.values()) {
			if (column == HotSpotsColumn.HOT_SPOT) {
				if (label.equals(getMethodColumnName())) {
					return HotSpotsColumn.HOT_SPOT;
				}
			} else {
				if (label.equals(column.label)) {
					return column;
				}
			}
		}
		return null;
	}
}

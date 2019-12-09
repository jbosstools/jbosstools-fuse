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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.chart.TableChartColumnInfo;
import org.fusesource.ide.foundation.ui.chart.TableChartOptions;
import org.fusesource.ide.foundation.ui.config.ColumnConfiguration;
import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.label.FunctionColumnLabelProvider;
import org.fusesource.ide.foundation.ui.label.WrappedCellLabelProvider;
import org.fusesource.ide.foundation.ui.util.Trees;
import org.fusesource.ide.foundation.ui.util.Viewers;
import org.fusesource.ide.foundation.ui.util.Widgets;


public abstract class TreeViewSupport extends ColumnViewSupport {
	protected TreeViewer viewer;
	private Composite parent;
	private TextTreeViewFilter filter = new TextTreeViewFilter();
	private ColumnFunctionComparator comparator = new ColumnFunctionComparator();
	private List<String> columnNames;

	protected abstract void createColumns();

	protected abstract void configureViewer();

	protected abstract ITreeContentProvider createContentProvider();


	@Override
	public List<String> getColumns() {
		if (columnNames == null) {
			columnNames = new ArrayList<String>();
			if (viewer != null) {
				Tree table = viewer.getTree();
				if (table != null) {
					TreeColumn[] tableColumns = table.getColumns();
					for (TreeColumn column : tableColumns) {
						try {
							String name = column.getText();
							columnNames.add(name);
						} catch (Exception e) {
							// ignore error trying to get name
							// probably the column is now disposed
						}
					}
				}
			}
		}
		return columnNames;
	}

	@Override
	public void updateColumnConfiguration(TableConfiguration configuration) {
		this.setConfiguration(configuration);
		Trees.disposeColumns(viewer);
		recreateColumns();
		getViewer().refresh(true);
	}

	protected void recreateColumns() {
	    // remove the listeners
        setConfiguration(null);
		createColumns();
		// force lazy creation
		columnNames = null;
		getColumns();
		reorderColumns();
        getConfiguration().addColumnListeners(getViewer());
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search: ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent ke) {
				filter.setSearchText(searchText.getText());
				onFilterChanged();
			}
		});
		createViewer();

		// Create the help context id for the viewer's control
		makeActions();
	}


	protected void onFilterChanged() {
		Viewers.refresh(viewer);
	}

	protected void createViewer() {
		// dispose of previous if we have them
		if (viewer != null) {
			viewer.getControl().dispose();
		}

		viewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		Tree table = viewer.getTree();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		ITreeContentProvider contentProvider = createContentProvider();
		filter.setContentProvider(contentProvider);
		viewer.setContentProvider(contentProvider);
		recreateColumns();

		viewer.addFilter(filter);
		viewer.setComparator(comparator);
		configureViewer();

		// Layout the viewer
		// Layout the viewer
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1);
		gridData.heightHint = 300;
		gridData.widthHint = 500;
		viewer.getControl().setLayoutData(gridData);

		chartOptions = createChartOptions();

		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getHelpSystem().setHelp(viewer.getControl(),
				getHelpID());
		hookDoubleClickAction();

		parent.layout(true);
	}

	/**
	 * Sets the columns with given column order and visibility; adding any not-mentioned columns to the end in their current order.
	 * 
	 * @param configuration
	 *            The column order and visibility
	 */
	private void reorderColumns() {
		if (getConfiguration() == null || !getConfiguration().hasColumns()) {
			// don't reorder unless we've either some defaults or we've reconfigured things
			return;
		}

		Map<String,TreeColumn> columnMap = new HashMap<String,TreeColumn>();
		Map<String,Integer> columnIndexes = new HashMap<String,Integer>();
		List<String> columnNameOrderList = new ArrayList<String>();
		List<Integer> columnOrderList = new ArrayList<Integer>();
		TreeColumn[] columnArray = Trees.getColumns(viewer);
		Tree table = viewer.getTree();
		for (int i = 0, size = columnArray.length; i < size; i++) {
			TreeColumn column = columnArray[i];
			String name = column.getText();
			columnMap.put(name, column);
			columnIndexes.put(name, i);
		}

		List<ColumnConfiguration> list = getConfiguration().getColumnConfigurations();
		for (ColumnConfiguration config: list) {
			String columnName = config.getName();
			boolean columnVisibility = config.isVisible();

			TreeColumn column = columnMap.get(columnName);
			if (!columnVisibility) {
				Widgets.dispose(column);
			} else {
				if (column == null) {
					FoundationUIActivator.pluginLog().logWarning("No column found for name '" + columnName + "'");
				} else {
					columnNameOrderList.add(columnName);
					int width = config.getWidth();
					if (width > 0) {
						column.setWidth(width);
					}
					Integer index = columnIndexes.get(columnName);
					if (index != null) {
						int idx = index;
						CellLabelProvider oldLabelProvider = getCellLabelProvider(idx);
						TreeViewerColumn viewerColumn = Trees.getTreeViewerColumn(column);

						configureLabelProvider(viewerColumn, config, oldLabelProvider);
					}
				}
			}
		}

		// now lets update the column indices as we may have disposed of some
		columnArray = table.getColumns();
		Map<String,Integer> indexMap = new HashMap<String,Integer>();
		for (int i = 0, size = columnArray.length; i < size; i++) {
			TreeColumn column = columnArray[i];
			String name = column.getText();
			indexMap.put(name, i);
		}
		for (String columnName : columnNameOrderList) {
			// lets find the order of this column
			Integer i = indexMap.get(columnName);
			if (i == null) {
				FoundationUIActivator.pluginLog().logWarning("Warning - no column index for name '" + columnName + "' found!");
			} else {
				columnOrderList.add(i);
			}
		}

		int[] columnOrder = table.getColumnOrder();
		for (int i = 0, size = columnOrder.length; i < size; i++) {
			int idx = columnOrder[i];
			Integer key = idx;
			if (idx >= 0 && idx < columnArray.length && !columnOrderList.contains(key)) {
				TreeColumn column = columnArray[i];
				if (column != null && !column.isDisposed()) {
					// lets add the column to the order
					columnOrderList.add(key);
				}
			}
		}
		int[] newColumnOrder = new int[columnOrderList.size()];
		int idx = 0;
		for (Integer key : columnOrderList) {
			newColumnOrder[idx++] = key;
		}
		table.setColumnOrder(newColumnOrder);
	}

	@Override
	protected TableChartOptions createChartOptions() {
		TableChartOptions options = new TableChartOptions();
		Tree table = viewer.getTree();
		for (int i = 0, size = table.getColumnCount(); i < size; i++) {
			CellLabelProvider labelProvider = getCellLabelProvider(i);
			Class<?> returnType = Objects.getReturnType(labelProvider);
			if (Objects.isNumberType(returnType)) {
				TreeColumn column = table.getColumn(i);
				options.addNumericColumn(new TableChartColumnInfo(column, labelProvider));
			}
		}
		return options;
	}

	protected CellLabelProvider getCellLabelProvider(int idx) {
		CellLabelProvider oldLabelProvider = viewer.getLabelProvider(idx);

		// lets unwrap if we've added a wrapped provider already
		if (oldLabelProvider instanceof WrappedCellLabelProvider) {
			WrappedCellLabelProvider wrapped = (WrappedCellLabelProvider) oldLabelProvider;
			oldLabelProvider = wrapped.getWrappedLabelProvider();
		}
		return oldLabelProvider;
	}

	protected TreeViewerColumn createTreeViewerColumn(final String title, int bound, int colNumber) {
		final TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.NONE);
		final TreeColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	protected int addColumnFunction(int bounds, int column, final Function1 function, String columnName, CellLabelProvider labelProvider) {
		addFunction(function);
		TreeViewerColumn col = createTreeViewerColumn(columnName,
				bounds, column++);
		col.setLabelProvider(labelProvider);
		return column;
	}

	protected int addColumnFunction(int bounds, int column, final Function1 function, String columnName) {
		return addColumnFunction(bounds, column, function, columnName, new FunctionColumnLabelProvider(function));
	}

	protected SelectionAdapter getSelectionAdapter(final TreeColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				if (Viewers.isValid(viewer)) {
					int dir = viewer.getTree().getSortDirection();
					if (viewer.getTree().getSortColumn() == column) {
						dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
					} else {

						dir = SWT.DOWN;
					}
					viewer.getTree().setSortDirection(dir);
					viewer.getTree().setSortColumn(column);
					viewer.refresh();
				}
			}
		};
		return selectionAdapter;
	}

	@Override
	protected void showChartDialog() {
		// TODO
	}

	public Tree getTree() {
		return viewer.getTree();
	}

	@Override
	public TreeViewer getViewer() {
		return viewer;
	}

}
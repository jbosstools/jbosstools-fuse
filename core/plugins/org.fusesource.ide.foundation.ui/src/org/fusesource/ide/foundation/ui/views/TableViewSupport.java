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
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.chart.TableChartColumnInfo;
import org.fusesource.ide.foundation.ui.chart.TableChartDialog;
import org.fusesource.ide.foundation.ui.chart.TableChartOptions;
import org.fusesource.ide.foundation.ui.config.ColumnConfiguration;
import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.fusesource.ide.foundation.ui.label.FunctionColumnLabelProvider;
import org.fusesource.ide.foundation.ui.label.WrappedCellLabelProvider;
import org.fusesource.ide.foundation.ui.util.Tables;
import org.fusesource.ide.foundation.ui.util.Viewers;
import org.fusesource.ide.foundation.ui.util.Widgets;


public abstract class TableViewSupport extends ColumnViewSupport {
	protected TableViewer viewer;
	private Composite parent;
	private TextViewFilter filter = new TextViewFilter();
	private ColumnFunctionComparator comparator = new ColumnFunctionComparator();
	private List<String> columnNames;
	private boolean showSearchBox = true;
	private Composite inner;
	private Composite disposeInner;
	private Text searchText;

	protected abstract void createColumns();

	protected abstract void configureViewer();

	protected abstract IStructuredContentProvider createContentProvider();


	@Override
	public List<String> getColumns() {
		if (columnNames == null) {
			columnNames = new ArrayList<>();
			if (viewer != null) {
				Table table = viewer.getTable();
				if (table != null) {
					TableColumn[] tableColumns = table.getColumns();
					for (TableColumn column : tableColumns) {
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
		Tables.disposeColumns(viewer);
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

		Widgets.dispose(disposeInner);
		disposeInner = null;
		GridLayout layout = new GridLayout(2, false);

		Layout parentLayout = parent.getLayout();
		boolean useNestedComposite = parentLayout != null && !(parentLayout instanceof FillLayout || parentLayout instanceof GridLayout);
		if (useNestedComposite) {
			inner = new Composite(parent, SWT.NONE);
			disposeInner = inner;
		} else {
			inner = parent;
		}
		inner.setLayout(layout);

		if (useNestedComposite && parentLayout instanceof GridLayout) {
			inner.setLayoutData(new GridData(GridData.FILL_BOTH));
		}

		if (isShowSearchBox()) {
			Label searchLabel = new Label(inner, SWT.NONE);
			searchLabel.setText("Search: ");
			searchText = new Text(inner, SWT.BORDER | SWT.SEARCH);
			searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			searchText.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent ke) {
					setFilterText(searchText.getText());
				}

			});
		}
		createViewer();
		if (isShowSearchBox()) {
			String initialSearchText = getInitialSearchText();
			if (initialSearchText != null && initialSearchText.length() > 0) {
				setFilterText(initialSearchText);
				if (searchText != null) {
					searchText.setText(initialSearchText);
				}
			}
		}

		// Create the help context id for the viewer's control
		makeActions();
	}

	protected String getInitialSearchText() {
		return "";
	}

	protected Text getSearchText() {
	    return searchText;
	}

	protected void setFilterText(String text) {
		filter.setSearchText(text);
		onFilterChanged();
	}

	protected void onFilterChanged() {
		Viewers.refresh(viewer);
	}

	protected void createViewer() {
		// dispose of previous if we have them
		if (viewer != null) {
			viewer.getControl().dispose();
		}

		viewer = new TableViewer(inner, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(createContentProvider());
		recreateColumns();

		viewer.addFilter(filter);
		viewer.setComparator(comparator);
		configureViewer();

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

		setSelectionProvider();

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
		Table table = viewer.getTable();
		if (!Widgets.isValid(table)) {
			return;
		}

		Map<String, TableColumn> columnMap = new HashMap<>();
		Map<String, Integer> columnIndexes = new HashMap<>();
		List<String> columnNameOrderList = new ArrayList<>();
		List<Integer> columnOrderList = new ArrayList<>();
		TableColumn[] columnArray = Tables.getColumns(table);
		for (int i = 0, size = columnArray.length; i < size; i++) {
			TableColumn column = columnArray[i];
			String name = column.getText();
			columnMap.put(name, column);
			columnIndexes.put(name, i);
		}

		List<ColumnConfiguration> list = getConfiguration().getColumnConfigurations();
		for (ColumnConfiguration config: list) {
			String columnName = config.getName();
			boolean columnVisibility = config.isVisible();

			TableColumn column = columnMap.get(columnName);
			if (!columnVisibility) {
				Widgets.dispose(column);
			} else {
				if (column != null) {
					if (!columnNameOrderList.contains(columnName)) {
						columnNameOrderList.add(columnName);
					}
					int width = config.getWidth();
					if (width > 0) {
						column.setWidth(width);
					}
					Integer index = columnIndexes.get(columnName);
					if (index != null) {
						int idx = index;
						CellLabelProvider oldLabelProvider = getCellLabelProvider(idx);
						ViewerColumn viewerColumn = Tables.getTableViewerColumn(column);

						configureLabelProvider(viewerColumn, config, oldLabelProvider);
					}
				}
			}
		}

		// now lets update the column indices as we may have disposed of some
		columnArray = table.getColumns();
		Map<String, Integer> indexMap = new HashMap<>();
		for (int i = 0, size = columnArray.length; i < size; i++) {
			TableColumn column = columnArray[i];
			String name = column.getText();
			indexMap.put(name, i);
		}
		for (String columnName : columnNameOrderList) {
			// lets find the order of this column
			Integer i = indexMap.get(columnName);
			if (i != null) {
				columnOrderList.add(i);
			}
		}

		int[] columnOrder = table.getColumnOrder();
		for (int i = 0, size = columnOrder.length; i < size; i++) {
			int idx = columnOrder[i];
			Integer key = idx;
			if (idx >= 0 && idx < columnArray.length && !columnOrderList.contains(key)) {
				TableColumn column = columnArray[i];
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

	/**
	 * Sets the column index we should sort rows by default
	 */
	protected void setDefaultSortColumnIndex(int idx) {
		comparator.setDefaultSortColumn(idx);
	}

	@Override
	protected TableChartOptions createChartOptions() {
		TableChartOptions options = new TableChartOptions();
		Table table = viewer.getTable();
		for (int i = 0, size = table.getColumnCount(); i < size; i++) {
			CellLabelProvider labelProvider = getCellLabelProvider(i);
			Class<?> returnType = Objects.getReturnType(labelProvider);
			if (Objects.isNumberType(returnType)) {
				TableColumn column = table.getColumn(i);
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

	protected TableViewerColumn createTableViewerColumn(final String title, int bound, int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	protected int addColumnFunction(int bounds, int column, final Function1<Object, ?> function, String columnName) {
		addFunction(function);
		TableViewerColumn col = createTableViewerColumn(columnName,
				bounds, column++);
		col.setLabelProvider(new FunctionColumnLabelProvider(function));
		return column;
	}

	protected int addColumnFunction(int bounds, int column, final Function1<Object, Object> function, String columnName, CellLabelProvider labelProvider) {
		addFunction(function);
		TableViewerColumn col = createTableViewerColumn(columnName,
				bounds, column++);
		col.setLabelProvider(labelProvider);
		return column;
	}

	protected SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				if (Viewers.isValid(viewer)) {
					int dir = viewer.getTable().getSortDirection();
					if (viewer.getTable().getSortColumn() == column) {
						dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
					} else {
						dir = SWT.DOWN;
					}
					viewer.getTable().setSortDirection(dir);
					viewer.getTable().setSortColumn(column);
					viewer.refresh();
				}
			}
		};
	}

	public ColumnFunctionComparator getComparator() {
		return comparator;
	}

	public void setComparator(ColumnFunctionComparator comparator) {
		this.comparator = comparator;
	}

	@Override
	protected void showChartDialog() {
		TableChartDialog dialog = new TableChartDialog(this);
		dialog.open();
	}

	public Table getTable() {
		return viewer.getTable();
	}

	@Override
	public TableViewer getViewer() {
		return viewer;
	}

	public boolean isShowSearchBox() {
		return showSearchBox;
	}

	public void setShowSearchBox(boolean showSearchBox) {
		this.showSearchBox = showSearchBox;
	}

}
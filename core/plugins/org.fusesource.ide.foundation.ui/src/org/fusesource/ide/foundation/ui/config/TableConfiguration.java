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

package org.fusesource.ide.foundation.ui.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.util.IConfigurableColumns;
import org.fusesource.ide.foundation.ui.util.IFlushable;
import org.fusesource.ide.foundation.ui.util.PreferencesHelper;
import org.fusesource.ide.foundation.ui.util.Tables;
import org.osgi.service.prefs.Preferences;


/**
 * Configuration details of a table. These will be stored in
 * 
 * .metadata/.plugins/org.eclipse.core.runtime/.settings/org.fusesource.ide.commons.prefs
 */
public class TableConfiguration implements IFlushable {
	protected static final int CONFIG_UPDATE_ONMOVE_DELAY = 1500;
	protected static final String COLUMN_NAME_SEPARATOR = ",";
	protected static final String COLUMN_ORDER = "columnOrder";
	private AtomicInteger moveEventCounter = new AtomicInteger();
	private static Map<String,TableConfiguration> defaultCache = new HashMap<>();
	private List<ColumnConfiguration> columnConfigurations;
	private Map<String,ColumnConfiguration> columnMap;
	private final Preferences node;
	private boolean cleared;
	private Listener tableColumnListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            final int value = moveEventCounter.incrementAndGet();
            final TableColumn column = (TableColumn) event.widget;
            // when we've stopped moving update the columns
            event.display.timerExec(CONFIG_UPDATE_ONMOVE_DELAY, new Runnable() {
                @Override
                public void run() {
                    if (moveEventCounter.compareAndSet(value, 0)) {
                        //Activator.getLogger().debug("We are the last move event at counter: " + value + " so updating the config model");
                        if (column == null || column.isDisposed()) {
                            return;
                        }
                        onColumnsMoved(column.getParent());
                    }
                }
            });
        }
    };
    private Listener treeColumnListener = new Listener() {
        @Override
        public void handleEvent(Event event) {
            final int value = moveEventCounter.incrementAndGet();
            final TreeColumn column = (TreeColumn) event.widget;
            // when we've stopped moving update the columns
            event.display.timerExec(CONFIG_UPDATE_ONMOVE_DELAY, new Runnable() {
                @Override
                public void run() {
                    if (moveEventCounter.compareAndSet(value, 0)) {
                        if (column == null || column.isDisposed()) {
                            return;
                        }
                        //Activator.getLogger().debug("We are the last move event at counter: " + value + " so updating the config model");
                        onColumnsMoved(column.getParent());
                    }
                }
            });
        }
    };

	public TableConfiguration(Preferences node) {
		this.node = node;
	}

	@Override
	public String toString() {
		return "TableConfguration(" + node + ")";
	}

	/**
	 * Loads a set of defaults for the table for the given view class
	 */
	public static TableConfiguration loadDefault(Class<?> aClass) {
		return loadDefault(aClass.getName());
	}

	/**
	 * Loads a set of defaults for the table for the given view ID
	 */
	public static synchronized TableConfiguration loadDefault(String className) {
		TableConfiguration answer = defaultCache.get(className);
		if (answer == null) {
			Preferences node = PreferencesHelper.defaultNode(className, PreferencesHelper.KEY_TABLE_COLUMNS);
			answer = new TableConfiguration(node);
			defaultCache.put(className, answer);
		}
		return answer;
	}

	/**
	 * Loads a set of configurations for the table for the given view class
	 */
	public static TableConfiguration loadConfiguration(Class<?> aClass) {
		return loadConfiguration(aClass.getName());
	}

	/**
	 * Loads a set of configurations for the table for the given view ID, applying the defaults
	 * for any missing configurations
	 */
	public static TableConfiguration loadConfiguration(String className) {
		// lets apply the defaults
		TableConfiguration defaults = TableConfiguration.loadDefault(className);

		Preferences node = PreferencesHelper.configurationNode(className, PreferencesHelper.KEY_TABLE_COLUMNS);
		TableConfiguration answer = new TableConfiguration(node);

		// lets add the defaults for columns with no current configuration
		answer.addDefaults(defaults);
		return answer;
	}


	/**
	 * Flushes any changes to the configuration to the underlying preferences store
	 */
	@Override
	public void flush() {
		PreferencesHelper.flush(node);
	}

	/**
	 * Clears all the configuration
	 */
	public void clear() {
		cleared = true;
		try {
			Preferences parent = node.parent();
			node.removeNode();
			if (parent != null) {
				PreferencesHelper.flush(parent);
			}
		} catch (Exception e) {
			FoundationUIActivator.pluginLog().logError("Failed to clear " + this + ". " + e, e);
		}
	}

	public boolean isCleared() {
		return cleared;
	}

	public void reload() {
		columnConfigurations = null;
	}

	public boolean hasColumns() {
		return !getColumnConfigurations().isEmpty();
	}


	/**
	 * Returns the configuration for the given column, creating a new one if necessary
	 */
	public ColumnConfiguration column(String name) {
		return column(name, node.node(name));
	}

	/**
	 * Returns the configuration for the given column, creating a new one if necessary
	 */
	public ColumnConfiguration column(String name, Preferences node) {
		ColumnConfiguration config = getConfiguration(name);
		if (config == null) {
			config = new ColumnConfiguration(name, node);
			addColumn(config);
		}
		return config;
	}

	protected ColumnConfiguration addColumn(ColumnConfiguration config) {
		getColumnConfigurations().add(config);
		updateColumnOrder(this.columnConfigurations);
		flushIndexes();
		return config;
	}

	public List<ColumnConfiguration> getColumnConfigurations() {
		if (columnConfigurations == null) {
			// lazy load from the preferences
			loadConfigurations();
		}
		return columnConfigurations;
	}

	protected void loadConfigurations() {
		List<ColumnConfiguration> list = new ArrayList<ColumnConfiguration>();
		// String[] childrenNames = node.childrenNames();
		// lets load them in order they were sorted
		String text = node.get(COLUMN_ORDER, "");
		if (text != null && text.length() > 0) {
			String[] childrenNames = text.split(COLUMN_NAME_SEPARATOR);
			for (String name : childrenNames) {
				ColumnConfiguration config = new ColumnConfiguration(name, node.node(name));
				list.add(config);
			}
		}
		setColumnConfigurations(list);
	}

	public void setColumnConfigurations(List<ColumnConfiguration> columnConfigurations) {
		List<String> names = updateColumnOrder(columnConfigurations);

		// now lets remove any names not in the new ordered collection
		if (this.columnConfigurations != null) {
			for (ColumnConfiguration oldConfig : this.columnConfigurations) {
				String name = oldConfig.getName();
				if (!names.contains(name)) {
					node.remove(name);
				}
			}
		}
		this.columnConfigurations = columnConfigurations;
		flushIndexes();
	}

	protected List<String> updateColumnOrder(List<ColumnConfiguration> columnConfigurations) {
		List<String> names = getColumnNames(columnConfigurations);
		String columnOrderText = Strings.join(COLUMN_NAME_SEPARATOR, names.toArray());
		node.put(COLUMN_ORDER, columnOrderText);

		//Activator.getLogger().debug("Updated columns to: " + names);
		return names;
	}

	private List<String> getColumnNames(List<ColumnConfiguration> list) {
		List<String> answer = new ArrayList<String>();
		for (ColumnConfiguration config : list) {
			answer.add(config.getName());
		}
		return answer;
	}

	protected void flushIndexes() {
		synchronized (this) {
			this.columnMap = null;
		}
	}

	public Map<String,ColumnConfiguration> getColumnMap() {
		synchronized (this) {
			List<ColumnConfiguration> list = getColumnConfigurations();
			if (columnMap == null) {
				columnMap = new HashMap<String, ColumnConfiguration>();
				for (ColumnConfiguration column : list) {
					columnMap.put(column.getName(), column);
				}
			}
			return columnMap;
		}
	}

	/**
	 * Returns the column configuration for the given column name or returns null if it does not exist
	 */
	public ColumnConfiguration getConfiguration(String columnName) {
		return getColumnMap().get(columnName);
	}

	/**
	 * Returns a sorted list of default column names by using the sort order defined in the table configuration first,
	 * then any remaining columns afterwards
	 */
	public List<String> sortDefaultColumnNames(Collection<String> columnNames) {
		Set<String> existing = new HashSet<String>(columnNames);
		List<String> answer = new ArrayList<String>();
		List<ColumnConfiguration> list = getColumnConfigurations();
		for (ColumnConfiguration config : list) {
			String name = config.getName();
			if (existing.contains(name)) {
				answer.add(name);
			}
		}
		for (String name : columnNames) {
			if (!answer.contains(name)) {
				answer.add(name);
			}
		}
		return answer;
	}

	/**
	 * Ensures that the given list of columns are created and visible if we have any configuration so far.
	 */
	public void addDefaultColumns(List<String> columns) {
		for (String name : columns) {
			column(name);
		}
	}

	/**
	 * Lets add the default configurations if there are any to any column not already configured in some way
	 */
	public void addDefaults(TableConfiguration defaults) {
		List<ColumnConfiguration> list = defaults.getColumnConfigurations();
		for (ColumnConfiguration column : list) {
			defaultColumn(column);
		}

	}

	/**
	 * Ensures we have the default column configuration created; or add this one if not
	 */
	public void defaultColumn(ColumnConfiguration defaultValues) {
		ColumnConfiguration current = getConfiguration(defaultValues.getName());
		if (current == null) {
			current = addColumn(defaultValues);
		}
		// lets default size if present
		int width = defaultValues.getWidth();
		if (width > 0 && current.getWidth() == 0) {
			current.setWidth(width);
		}
		String labelProviderStyle = defaultValues.getLabelProviderStyle();
		if (labelProviderStyle != null) {
			current.setLabelProviderStyle(labelProviderStyle);

		}
		CellLabelProvider labelProvider = defaultValues.getLabelProvider();
		if (labelProvider != null) {
			current.setLabelProvider(labelProvider);
		}
	}

	/**
	 * Called when the user moves columns around themselves using drag and drop in the table viewer
	 */
	public void onColumnsMoved(Table table) {
		if (isCleared() || table == null || table.isDisposed()) {
			//Activator.getLogger().debug("Ignoring move events as configuration is cleared");
			return;
		}
		List<ColumnConfiguration> newOrder = new ArrayList<ColumnConfiguration>();
		TableColumn[] columns = Tables.getColumns(table);
		if (columns.length > 0) {
			int[] columnOrder = table.getColumnOrder();
			for (int idx : columnOrder) {
				if (idx >= 0 && idx < columns.length) {
					TableColumn column = columns[idx];
					if (column != null && !column.isDisposed()) {
						String name = column.getText();
						ColumnConfiguration config = column(name);
						newOrder.add(config);
					}
				}
			}
		}
		if (columns.length > 1 && newOrder.size() > 1) {
			makeOtherColumnsInvisilbe(newOrder);
			FoundationUIActivator.pluginLog().logInfo("Flushing column configuration with newOrder: " + newOrder + " and columns: " + columns.length);
			flush();
		}
	}

	/**
	 * Called when the user moves columns around themselves using drag and drop in the tree viewer
	 */
	public void onColumnsMoved(Tree tree) {
		if (isCleared() || tree == null || tree.isDisposed()) {
			//Activator.getLogger().debug("Ignoring move events as configuration is cleared");
			return;
		}
		List<ColumnConfiguration> newOrder = new ArrayList<ColumnConfiguration>();
		TreeColumn[] columns = tree.getColumns();
		int[] columnOrder = tree.getColumnOrder();
		for (int idx : columnOrder) {
			if (idx >= 0 && idx < columns.length) {
				TreeColumn column = columns[idx];
				if (column != null && !column.isDisposed()) {
					String name = column.getText();
					ColumnConfiguration config = column(name);
					newOrder.add(config);
				}
			}
		}
		makeOtherColumnsInvisilbe(newOrder);
		flush();
	}

	protected void makeOtherColumnsInvisilbe(List<ColumnConfiguration> newOrder) {
		// lets add any ignored columns to the configuration so they are hidden next time
		Set<ColumnConfiguration> addedColumns = new HashSet<ColumnConfiguration>(newOrder);
		List<ColumnConfiguration> list = getColumnConfigurations();
		for (ColumnConfiguration config : list) {
			if (!addedColumns.contains(config)) {
				config.setVisible(false);
				newOrder.add(config);
			}
		}
		setColumnConfigurations(newOrder);
	}

	public void addListener(IConfigurableColumns configurableColumns) {
		// TODO there's no generic hook yet for doing listening of the underlying node...
	}

	public void removeListener(IConfigurableColumns configurableColumns) {
		// TODO there's no generic hook yet for doing listening of the underlying node...
	}

    public void addColumnListeners(final ColumnViewer viewer) {
        if (viewer instanceof TreeViewer) {
            addColumnListeners((TreeViewer) viewer);
        } else if (viewer instanceof TableViewer) {
            addColumnListeners((TableViewer) viewer);
        }
    }
    
	public void addColumnListeners(final TableViewer viewer) {
		TableColumn[] columns = viewer.getTable().getColumns();
		for (TableColumn column : columns) {
			addColumnListeners(viewer, column);
		}
	}

	public void addColumnListeners(final TreeViewer viewer) {
		TreeColumn[] columns = viewer.getTree().getColumns();
		for (TreeColumn column : columns) {
			addColumnListeners(viewer, column);
		}
	}

	protected void addColumnListeners(final TableViewer viewer, final TableColumn column) {
		// TODO ignore listeners if we're disposing the columns to update it!!!
		column.addListener(SWT.Move, tableColumnListener);

	}

	protected void addColumnListeners(final TreeViewer viewer, final TreeColumn column) {
		column.addListener(SWT.Move, treeColumnListener);
	}

    public void removeColumnListeners(final ColumnViewer viewer) {
        if (viewer instanceof TreeViewer) {
            removeColumnListeners((TreeViewer) viewer);
        } else if (viewer instanceof TableViewer) {
            removeColumnListeners((TableViewer) viewer);
        }
    }
    
    public void removeColumnListeners(final TableViewer viewer) {
        moveEventCounter.set(0);
        TableColumn[] columns = viewer.getTable().getColumns();
        for (TableColumn column : columns) {
            removeColumnListeners(viewer, column);
        }
    }

    public void removeColumnListeners(final TreeViewer viewer) {
        moveEventCounter.set(0);
        TreeColumn[] columns = viewer.getTree().getColumns();
        for (TreeColumn column : columns) {
            removeColumnListeners(viewer, column);
        }
    }

    protected void removeColumnListeners(final TableViewer viewer, final TableColumn column) {
        // TODO ignore listeners if we're disposing the columns to update it!!!
        column.removeListener(SWT.Move, tableColumnListener);

    }

    protected void removeColumnListeners(final TreeViewer viewer, final TreeColumn column) {
        column.removeListener(SWT.Move, treeColumnListener);
    }

}

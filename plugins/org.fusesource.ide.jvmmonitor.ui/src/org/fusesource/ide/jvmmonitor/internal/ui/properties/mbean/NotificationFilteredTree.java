/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import java.util.ArrayList;
import java.util.List;

import javax.management.Notification;
import javax.management.ObjectName;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.dialogs.PatternFilter;
import org.fusesource.ide.commons.ui.IConfigurableColumns;
import org.fusesource.ide.commons.ui.Trees;
import org.fusesource.ide.commons.ui.actions.ConfigureColumnsAction;
import org.fusesource.ide.commons.ui.config.ColumnConfiguration;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.thread.ConfigurableFilteredTree;


/**
 * The notification filtered tree.
 */
public class NotificationFilteredTree extends ConfigurableFilteredTree implements IConfigurableColumns, IDoubleClickListener {

	/** The configure columns action. */
	ConfigureColumnsAction configureColumnsAction;

	/** The action to clear. */
	Action clearAction;

	/** The action to open details dialog. */
	NotificationDetailsDialogAction detailsAction;

	/** The notifications tab. */
	NotificationsTab notificationsTab;

	/** The property section. */
	private AbstractJvmPropertySection section;

	/**
	 * The constructor.
	 * 
	 * @param notificationsTab
	 *            The notifications tab
	 * @param section
	 *            The property section
	 */
	protected NotificationFilteredTree(NotificationsTab notificationsTab,
			AbstractJvmPropertySection section) {
		super(notificationsTab, SWT.MULTI | SWT.FULL_SELECTION,
				new PatternFilter(), true);

		this.notificationsTab = notificationsTab;
		this.section = section;
		treeViewer.setLabelProvider(new NotificationsLabelProvider(treeViewer));
		treeViewer.setContentProvider(new NotificationsContentProvider());
		treeViewer.addDoubleClickListener(this);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalIndent = 0;
		setLayoutData(gridData);

		configureTree();
		createContextMenu(section.getActionBars());
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
		NotificationColumn[] values = NotificationColumn.values();
		for (NotificationColumn value : values) {
			columnLabels.add(value.label);
		}
		return columnLabels;
	}

	public void updateColumnConfiguration() {
		configureTree();
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
		return NotificationColumn.getColumn(column).initialVisibility;
	}

	/*
	 * @see IDoubleClickListener#doubleClick(Double@Override ClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		detailsAction.run();
	}

	/**
	 * Sets the input.
	 * 
	 * @param objectName
	 *            The object name
	 */
	public void setInput(ObjectName objectName) {
		IActiveJvm jvm = section.getJvm();
		if (objectName == null || jvm == null) {
			treeViewer.setInput(null);
			return;
		}

		treeViewer.setInput(jvm.getMBeanServer().getMBeanNotification()
				.getNotifications(objectName));
	}

	/**
	 * Gets the previous item.
	 * 
	 * @return The previous item
	 */
	protected Notification getPrevItem() {
		Object selectedItem = ((StructuredSelection) getViewer().getSelection())
				.getFirstElement();
		TreeItem[] items = getViewer().getTree().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getData().equals(selectedItem) && i > 0) {
				return (Notification) items[i - 1].getData();
			}
		}
		return null;
	}

	/**
	 * Gets the next item.
	 * 
	 * @return The next item
	 */
	protected Notification getNextItem() {
		Object selectedItem = ((StructuredSelection) getViewer().getSelection())
				.getFirstElement();
		TreeItem[] items = getViewer().getTree().getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getData().equals(selectedItem) && i < items.length - 1) {
				return (Notification) items[i + 1].getData();
			}
		}
		return null;
	}

	/**
	 * Selects the previous item.
	 */
	protected void selectPrevItem() {
		Notification prevItem = getPrevItem();
		if (prevItem != null) {
			getViewer().setSelection(new StructuredSelection(prevItem), true);
		}
	}

	/**
	 * Selects the next item.
	 */
	protected void selectNextItem() {
		Notification nextItem = getNextItem();
		if (nextItem != null) {
			getViewer().setSelection(new StructuredSelection(nextItem), true);
		}
	}

	/**
	 * Configure the tree adding columns.
	 */
	@Override
	protected void configureTree() {
		Trees.disposeColumns(getViewer());
		Tree tree = getViewer().getTree();

		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		List<ColumnConfiguration> columns = getConfiguration().getColumnConfigurations();
		for (ColumnConfiguration config : columns) {
			if (!config.isVisible()) {
				continue;
			}
			NotificationColumn column = NotificationColumn.getColumn(config.getName());

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
		NotificationComparator sorter = (NotificationComparator) getViewer()
				.getComparator();

		if (sorter != null && columnIndex == sorter.getColumnIndex()) {
			sorter.reverseSortDirection();
		} else {
			sorter = new NotificationComparator(columnIndex);
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
		final CopyAction copyAction = CopyAction.createCopyAction(actionBars);
		clearAction = new Action(Messages.clearLabel) {
			@Override
			public void run() {
				notificationsTab.clear();
			}
		};
		detailsAction = new NotificationDetailsDialogAction(this);
		configureColumnsAction = new ConfigureColumnsAction(this);
		getViewer().addSelectionChangedListener(copyAction);
		getViewer().addSelectionChangedListener(detailsAction);

		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(copyAction);
				manager.add(new Separator());
				manager.add(clearAction);
				manager.add(new Separator());
				manager.add(configureColumnsAction);
				manager.add(new Separator());
				manager.add(detailsAction);
			}
		});

		Menu menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
	}
}

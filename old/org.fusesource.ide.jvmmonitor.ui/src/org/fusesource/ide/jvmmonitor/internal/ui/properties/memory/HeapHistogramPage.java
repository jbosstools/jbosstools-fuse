/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved.
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.fusesource.ide.commons.ui.IConfigurableColumns;
import org.fusesource.ide.commons.ui.Trees;
import org.fusesource.ide.commons.ui.actions.ConfigureColumnsAction;
import org.fusesource.ide.commons.ui.config.ColumnConfiguration;
import org.fusesource.ide.commons.ui.config.TableConfiguration;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.core.JvmCoreException;
import org.fusesource.ide.jvmmonitor.internal.ui.RefreshJob;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.CopyAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.OpenDeclarationAction;
import org.fusesource.ide.jvmmonitor.internal.ui.actions.RefreshAction;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The heap histogram page.
 */
public class HeapHistogramPage extends Composite implements IConfigurableColumns {

	/** The 64 bit OS architecture. */
	private static final String ARCH_64BIT = "64"; //$NON-NLS-1$

	private TableConfiguration configuration;

	/** The heap viewer. */
	TreeViewer viewer;

	/** The separator. */
	private Separator separator;

	/** The action to refresh section. */
	RefreshAction refreshAction;

	/** The action to run garbage collector. */
	GarbageCollectorAction garbageCollectorAction;

	/** The action to clear heap delta. */
	ClearHeapDeltaAction clearHeapDeltaAction;

	/** The action to dump heap. */
	DumpHeapAction dumpHeapAction;

	/** The action to dump hprof. */
	DumpHprofAction dumpHprofAction;

	/** The action to configure columns. */
	ConfigureColumnsAction configureColumnsAction;

	/** The memory section. */
	MemorySection section;

	/**
	 * The constructor.
	 * 
	 * @param section
	 *            The memory section
	 * @param parent
	 *            The parent composite
	 * @param tabFolder
	 *            The tab folder
	 * @param actionBars
	 *            The action bars
	 */
	public HeapHistogramPage(MemorySection section, Composite parent,
			final CTabFolder tabFolder, IActionBars actionBars) {
		super(parent, SWT.NONE);
		this.section = section;

		final CTabItem tabItem = section.getWidgetFactory().createTabItem(
				tabFolder, SWT.NONE);
		tabItem.setText(Messages.heapHistogramLabel);
		tabItem.setControl(parent);

		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshBackground();
				updateLocalToolBar(tabFolder.getSelection().equals(tabItem));
			}
		});

		init(actionBars);
	}

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param actionBars
	 *            The action bars
	 */
	public HeapHistogramPage(Composite parent, IActionBars actionBars) {
		super(parent, SWT.NONE);
		init(actionBars);
	}

	/*
	 * @see IConfigurableColumn#getColumns()
	 */
	@Override
	public List<String> getColumns() {
		ArrayList<String> columnLabels = new ArrayList<String>();
		HeapColumn[] values = HeapColumn.values();
		for (HeapColumn value : values) {
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


	@Override
	public TableConfiguration getConfiguration() {
		if (configuration == null) {
			configuration = TableConfiguration.loadConfiguration(getColumnConfigurationId());
			configuration.addDefaultColumns(getColumns());
		}
		return configuration;
	}


	@Override
	public void updateColumnConfiguration(TableConfiguration configuration) {
		this.configuration = configuration;
		if (getViewer().getTree().isDisposed()) {
			return;
		}
		configureTree();
		getViewer().refresh();
	}

	@Override
	public void dispose() {
		IToolBarManager manager = section.getActionBars().getToolBarManager();
		removeToolBarActions(manager);
		super.dispose();
		getConfiguration().removeListener(this);
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Sets the heap input.
	 * 
	 * @param heapInput
	 *            The heap input
	 */
	public void setInput(IHeapInput heapInput) {
		viewer.setInput(heapInput);
	}

	/**
	 * Refreshes the appearance.
	 */
	public void refresh() {
		// for dump editor
		if (section == null) {
			return;
		}

		final boolean isVisible = isVisible();

		new RefreshJob(NLS.bind(Messages.refreshMemorySectionJobLabel, section
				.getJvm().getPid()), getColumnConfigurationId()) {
			@Override
			protected void refreshModel(IProgressMonitor monitor) {
				if (!isSupported() || !isVisible) {
					return;
				}
				try {
					IActiveJvm jvm = section.getJvm();
					if (jvm != null && jvm.isConnected() && !jvm.isRemote()
							&& !section.isRefreshSuspended()) {
						jvm.getMBeanServer().refreshHeapCache();
					}
				} catch (JvmCoreException e) {
					Activator.log(Messages.refreshHeapDataFailedMsg, e);
				}
			}

			@Override
			protected void refreshUI() {
				IActiveJvm jvm = section.getJvm();
				boolean isConnected = jvm != null && jvm.isConnected();
				boolean isRemote = jvm != null && jvm.isRemote();
				boolean isSupported = isSupported();
				if (!viewer.getControl().isDisposed()
						&& viewer.getControl().isVisible() && isSupported) {
					viewer.refresh();
				}
				refreshBackground();

				dumpHprofAction.setEnabled(isConnected);
				dumpHeapAction.setEnabled(!section.hasErrorMessage()
						&& !isRemote && isSupported);
				refreshAction.setEnabled(isConnected && !isRemote
						&& isSupported);
				garbageCollectorAction.setEnabled(isConnected);
				clearHeapDeltaAction.setEnabled(isConnected && !isRemote
						&& isSupported);
			}
		}.schedule();
	}

	/**
	 * Invoked when section is deactivated.
	 */
	protected void deactivated() {
		Job.getJobManager().cancel(getColumnConfigurationId());
		IToolBarManager manager = section.getActionBars().getToolBarManager();
		removeToolBarActions(manager);
	}

	/**
	 * Refreshes the background.
	 */
	void refreshBackground() {
		IActiveJvm jvm = section.getJvm();
		boolean isConnected = jvm != null && jvm.isConnected();
		boolean isRemote = jvm != null && jvm.isRemote();
		section.refreshBackground(getChildren(), isConnected && !isRemote);
	}

	/**
	 * Adds the tool bar actions.
	 * 
	 * @param manager
	 *            The tool bar manager
	 */
	void addToolBarActions(IToolBarManager manager) {
		manager.insertAfter("defaults", separator); //$NON-NLS-1$
		if (manager.find(refreshAction.getId()) == null) {
			manager.insertAfter("defaults", refreshAction); //$NON-NLS-1$
		}
		if (manager.find(garbageCollectorAction.getId()) == null) {
			manager.insertAfter("defaults", garbageCollectorAction); //$NON-NLS-1$
		}
		if (manager.find(clearHeapDeltaAction.getId()) == null) {
			manager.insertAfter("defaults", clearHeapDeltaAction); //$NON-NLS-1$
		}
		if (manager.find(dumpHeapAction.getId()) == null) {
			manager.insertAfter("defaults", dumpHeapAction); //$NON-NLS-1$
		}
		if (manager.find(dumpHprofAction.getId()) == null) {
			manager.insertAfter("defaults", dumpHprofAction); //$NON-NLS-1$
		}
		manager.update(true);
	}

	/**
	 * Removes the tool bar actions.
	 * 
	 * @param manager
	 *            The tool bar manager
	 */
	void removeToolBarActions(IToolBarManager manager) {
		manager.remove(separator);
		manager.remove(refreshAction.getId());
		manager.remove(garbageCollectorAction.getId());
		manager.remove(clearHeapDeltaAction.getId());
		manager.remove(dumpHeapAction.getId());
		manager.remove(dumpHprofAction.getId());
		manager.update(true);
	}

	/**
	 * Updates the local tool bar.
	 * 
	 * @param activated
	 *            <tt>true</tt> if this tab item is activated
	 */
	void updateLocalToolBar(boolean activated) {
		IToolBarManager manager = section.getActionBars().getToolBarManager();
		if (activated) {
			addToolBarActions(manager);
		} else {
			removeToolBarActions(manager);
		}
	}

	private void init(IActionBars actionBars) {
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);

		viewer = new HeapFilteredTree(this).getViewer();
		viewer.setContentProvider(new HeapContentProvider(viewer));
		viewer.setLabelProvider(new HeapLabelProvider(viewer));

		configureTree();
		createActions();
		createContextMenu(actionBars);

		getConfiguration().addListener(this);
	}

	/**
	 * Gets the state indicating if heap histogram is supported.
	 * <p>
	 * WORKAROUND: Heap histogram is disabled on 64bit OS when monitoring
	 * eclipse itself, due to the issue that the method heapHisto() of the class
	 * HotSpotVirtualMachine causes continuously increasing the committed heap
	 * memory.
	 * 
	 * @return <tt>true</tt> if heap histogram is supported
	 */
	boolean isSupported() {
		IActiveJvm jvm = section.getJvm();
		if (jvm == null) {
			return false;
		}

		OperatingSystemMXBean osMBean = ManagementFactory
				.getOperatingSystemMXBean();
		RuntimeMXBean runtimeMBean = ManagementFactory.getRuntimeMXBean();
		if (osMBean.getArch().contains(ARCH_64BIT)
				&& runtimeMBean.getName()
				.contains(String.valueOf(jvm.getPid()))) {
			return false;
		}

		return true;
	}

	/**
	 * Creates the context menu.
	 * 
	 * @param actionBars
	 *            The action bars
	 */
	private void createContextMenu(IActionBars actionBars) {
		final OpenDeclarationAction openAction = OpenDeclarationAction
				.createOpenDeclarationAction(actionBars);
		final CopyAction copyAction = CopyAction.createCopyAction(actionBars);
		configureColumnsAction = new ConfigureColumnsAction(this);
		viewer.addSelectionChangedListener(openAction);
		viewer.getControl().addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				viewer.removeSelectionChangedListener(copyAction);
			}

			@Override
			public void focusGained(FocusEvent e) {
				viewer.addSelectionChangedListener(copyAction);
			}
		});
		viewer.addOpenListener(new IOpenListener() {
			@Override
			public void open(OpenEvent event) {
				openAction.run();
			}
		});

		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(openAction);
				manager.add(copyAction);
				manager.add(new Separator());
				manager.add(configureColumnsAction);
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	/**
	 * Creates the actions.
	 */
	private void createActions() {
		refreshAction = new RefreshAction(section);
		garbageCollectorAction = new GarbageCollectorAction(section);
		clearHeapDeltaAction = new ClearHeapDeltaAction(this, section);
		dumpHeapAction = new DumpHeapAction(section);
		dumpHprofAction = new DumpHprofAction(section);
		separator = new Separator();
	}

	/**
	 * Configure the tree adding columns.
	 */
	protected void configureTree() {
		Tree tree = viewer.getTree();
		if (tree.isDisposed()) {
			return;
		}

		Trees.disposeColumns(getViewer());

		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		List<ColumnConfiguration> columns = getConfiguration().getColumnConfigurations();
		for (ColumnConfiguration config : columns) {
			if (!config.isVisible()) {
				continue;
			}
			HeapColumn column = HeapColumn.getColumn(config.getName());

			TreeColumn treeColumn = new TreeColumn(getViewer().getTree(), SWT.NONE);
			treeColumn.setText(column.label);
			treeColumn.setWidth(column.defalutWidth);
			treeColumn.setAlignment(column.alignment);
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
		Tree tree = viewer.getTree();
		int columnIndex = tree.indexOf(treeColumn);
		HeapComparator sorter = (HeapComparator) viewer.getComparator();

		if (sorter != null && columnIndex == sorter.getColumnIndex()) {
			sorter.reverseSortDirection();
		} else {
			sorter = new HeapComparator(columnIndex);
			viewer.setComparator(sorter);
		}
		tree.setSortColumn(treeColumn);
		tree.setSortDirection(sorter.getSortDirection());
		refresh();
	}
}

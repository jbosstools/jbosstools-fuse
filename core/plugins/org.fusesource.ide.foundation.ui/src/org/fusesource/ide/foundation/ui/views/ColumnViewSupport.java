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
import java.util.List;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.tabbed.ISection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.actions.ConfigureColumnsAction;
import org.fusesource.ide.foundation.ui.chart.TableChartOptions;
import org.fusesource.ide.foundation.ui.config.ColumnConfiguration;
import org.fusesource.ide.foundation.ui.config.TableConfiguration;
import org.fusesource.ide.foundation.ui.internal.FoundationUIActivator;
import org.fusesource.ide.foundation.ui.label.ChartLabelProvider;
import org.fusesource.ide.foundation.ui.label.FunctionColumnLabelProvider;
import org.fusesource.ide.foundation.ui.label.ImageLabelProvider;
import org.fusesource.ide.foundation.ui.label.LongTimestampAsTimeThenDateLabelProvider;
import org.fusesource.ide.foundation.ui.label.TimeThenDateFunctionLabelProvider;
import org.fusesource.ide.foundation.ui.label.TimeThenDateLabelProvider;
import org.fusesource.ide.foundation.ui.tree.Refreshable;
import org.fusesource.ide.foundation.ui.util.IConfigurableColumns;
import org.fusesource.ide.foundation.ui.util.Menus;
import org.fusesource.ide.foundation.ui.util.Viewers;
import org.fusesource.ide.foundation.ui.util.Widgets;
import org.jboss.tools.jmx.jvmmonitor.ui.Activator;
import org.jboss.tools.jmx.jvmmonitor.ui.ISharedImages;


public abstract class ColumnViewSupport extends ViewPart implements IConfigurableColumns, IViewPage, ISection, Refreshable {

	private Action refreshAction;
	private Action chartAction;
	private Action doubleClickAction;
	private List<Function1<?, ?>> functions = new ArrayList<>();
	private IPageSite pageSite;
	private TableConfiguration configuration;
	private boolean showChartingOptions;
	protected TableChartOptions chartOptions;
	private boolean isSectionActivated;
	private ConfigureColumnsAction configureColumnsAction;
	private TabbedPropertySheetPage tabbedPropertySheetPage;
	private IChangeListener changeListener;
	private List<Object> localMenuActions = new ArrayList<>();
	private List<Object> toolBarActions = new ArrayList<>();

	public ColumnViewSupport() {
		super();
	}

	@Override
	public void init(IPageSite pageSite) {
		this.pageSite = pageSite;
	}

	public IPageSite getPageSite() {
		if (tabbedPropertySheetPage != null) {
			IPageSite answer = tabbedPropertySheetPage.getSite();
			if (answer != null) {
				return answer;
			}
		}
		return pageSite;
	}

	@Override
	public void refresh() {
		Viewers.refresh(getViewer());
	}

	public void createControl(Composite parent) {
		createPartControl(parent);
	}

	public void addLocalMenuActions(Object... newActions) {
		for (Object action : newActions) {
			localMenuActions.add(action);
		}
	}

	public void addToolBarActions(Object... newActions) {
		for (Object action : newActions) {
			toolBarActions.add(action);
		}
	}

	public Control getControl() {
		if (getViewer() == null) {
			return null;
		}
		return getViewer().getControl();
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	}

	public void setActionBars(IActionBars actionBars) {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage tabbedPropertySheetPage) {
		this.tabbedPropertySheetPage = tabbedPropertySheetPage;
		init(tabbedPropertySheetPage.getSite());
		createPartControl(parent);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
	}


	@Override
	public void dispose() {
		deactivateSection();
		super.dispose();
	}

	@Override
	public void aboutToBeShown() {
		if (!isSectionActivated) {
			isSectionActivated = true;
			addToolBarActions();
			addLocalMenus();
			if (getActionBars() != null) {
			    getActionBars().updateActionBars();
			}
			setSelectionProvider();
		}
	}

	protected void setSelectionProvider() {
		IWorkbenchPartSite site = getSite();
		IPageSite pageSite = getPageSite();
		if (site != null) {
			site.setSelectionProvider(getViewer());
		}
		if (tabbedPropertySheetPage instanceof TabFolderSupport2) {
			TabFolderSupport2 tfs = (TabFolderSupport2) tabbedPropertySheetPage;
			IViewSite viewSite = tfs.getViewSite();
			if (viewSite != null) {
				viewSite.setSelectionProvider(getViewer());
				/*
				PropertySheet propertySheet = tfs.getPropertySheet();
				IWorkbenchPage page = viewSite.getPage();
				IWorkbenchPart activePart = page.getActivePart();
				if (propertySheet != null) {
					propertySheet.getSite().getPage().addPostSelectionListener(propertySheet);
				}
				 */
			}
		}
		if (pageSite != null) {
			// NOTE the following code can cause ECLIPSE-692
			// so lets not do it for now :)
			//pageSite.setSelectionProvider(getViewer());
		}
		if (site == null && pageSite == null) {
			warnNoSite();
		}
	}


	@Override
	public void aboutToBeHidden() {
		//if (isFocused() || !propertySheet.isPinned()) {
		// hidden by selecting another tab
		deactivateSection();
		//}
	}

	protected void deactivateSection() {
		if (isSectionActivated) {
			isSectionActivated = false;

			// remove tool bar actions
			IToolBarManager toolBarManager = getToolBarManager();
			if (toolBarManager != null) {
				removeToolBarActions(toolBarManager);
				toolBarManager.update(false);
			}

			// remove local menus
			IMenuManager menuManager = getMenuManager();
			if (menuManager != null) {
				removeLocalMenus(menuManager);
				menuManager.update(false);
			}

			if (getActionBars() != null) {
			    getActionBars().updateActionBars();
			}
			// clear status line
			clearStatusLine();
		}
	}

	/**
	 * Clears the status line.
	 */
	public void clearStatusLine() {
		/*
        IStatusLineManager manager = propertySheet.getViewSite()
                .getActionBars().getStatusLineManager();

        IContributionItem[] items = manager.getItems();
        for (IContributionItem item : items) {
            if (item instanceof StatusLineContributionItem) {
                ((StatusLineContributionItem) item)
                        .setText(Util.ZERO_LENGTH_STRING);
            }
        }
		 */
	}

	/**
	 * Activates the section.
	 */
	protected void activateSection() {
	}

	@Override
	public int getMinimumHeight() {
		return 0;
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

	@Override
	public void updateColumnConfiguration(TableConfiguration configuration) {
	}

	@Override
	public void createPartControl(Composite parent) {
	}

	public abstract ColumnViewer getViewer();

	protected abstract String getHelpID();

	protected abstract void showChartDialog();

	/**
	 * Adds the tool bar actions.
	 */
	private void addToolBarActions() {
		IToolBarManager toolBarManager = getToolBarManager();
		if (toolBarManager != null) {
			addToolBarActions(toolBarManager);
		}
	}

	protected void addToolBarActions(IToolBarManager manager) {
		Menus.addAction(manager, refreshAction);
		if (showChartAction()) {
			Menus.addAction(manager, chartAction);
		}
		Menus.addAction(manager, toolBarActions);
	}

	protected void removeToolBarActions(IToolBarManager manager) {
		Menus.removeAction(manager, toolBarActions);
		Menus.removeAction(manager, refreshAction);
		Menus.removeAction(manager, chartAction);
	}


	protected void addLocalMenus(IMenuManager manager) {
		Menus.addAction(manager, refreshAction);
		Menus.addAction(manager, configureColumnsAction);
		if (showChartAction()) {
			Menus.addAction(manager, chartAction);
		}
		Menus.addAction(manager, localMenuActions);
	}

	protected void removeLocalMenus(IMenuManager manager) {
		Menus.removeAction(manager, refreshAction);
		Menus.removeAction(manager, configureColumnsAction);
		Menus.removeAction(manager, chartAction);
		Menus.removeAction(manager, localMenuActions);
	}


	/*
	protected void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);
		if (showChartAction()) {
			manager.add(chartAction);
		}
		manager.add(new ConfigureColumnsAction(this));
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	 */

	/**
	 * Adds the local menus.
	 */
	private void addLocalMenus() {
		IMenuManager menuManager = getMenuManager();
		if (menuManager != null) {
			addLocalMenus(menuManager);
			menuManager.setVisible(true);
		}
	}

	protected IMenuManager getMenuManager() {
		IActionBars actionBars = getActionBars();
		IMenuManager menuManager = null;
		if (actionBars != null) {
			menuManager = actionBars.getMenuManager();
		}
		return menuManager;
	}

	protected IToolBarManager getToolBarManager() {
		IActionBars actionBars = getActionBars();
		IToolBarManager answer = null;
		if (actionBars != null) {
			answer = actionBars.getToolBarManager();
		}
		return answer;
	}

	protected IActionBars getActionBars() {
		IPageSite pageSite = getPageSite();
		IActionBars actionBars = null;
		if (pageSite != null) {
			actionBars = pageSite.getActionBars();
		}
		if (actionBars == null) {
			IViewSite viewSite = getViewSite();
			if (viewSite != null) {
				actionBars = viewSite.getActionBars();
			}
		}
		return actionBars;
	}

	protected void addFunction(Function1<?, ?> function) {
		functions.add(function);
	}

	protected void clearColumns() {
		functions.clear();
	}

	/*
	protected void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				ColumnViewSupport.this.fillContextMenu(manager);
			}
		});
		menu = menuMgr.createContextMenu(getViewer().getControl());
		getViewer().getControl().setMenu(menu);
		IWorkbenchPartSite site = getSite();
		if (site != null) {
			site.registerContextMenu(menuMgr, getViewer());
		} else if (pageSite != null) {
			// TODO use a different ID?
			pageSite.registerContextMenu(getHelpID(), menuMgr, getViewer());
		} else {
			warnNoSite();
		}
	}

	protected void contributeToActionBars() {
		IActionBars bars = null;
		if (getViewSite() != null) {
			bars = getViewSite().getActionBars();
		} else if (pageSite != null) {
			bars = pageSite.getActionBars();
		} else {
			warnNoSite();
			return;
		}
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	 */

	protected void warnNoSite() {
		FoundationUIActivator.pluginLog().logWarning("No IViewSite or IPageSite registered for " + this);
	}


	protected void makeActions() {
		refreshAction = new Action() {
			@Override
			public void run() {
				refresh();
			}
		};
		refreshAction.setId(getClass().getName() + ".refresh");
		refreshAction.setText("Refresh");
		refreshAction.setToolTipText("Refreshes the view");
		refreshAction.setImageDescriptor(Activator.getImageDescriptor(ISharedImages.REFRESH_IMG_PATH));

		configureColumnsAction = new ConfigureColumnsAction(this);

		chartAction = new Action() {
			@Override
			public void run() {
				showChartDialog();
			}
		};
		chartAction.setId(getClass().getName() + ".chart");
		chartAction.setText("Create Chart");
		chartAction.setToolTipText("Create a chart from the current table");
		chartAction.setImageDescriptor(FoundationUIActivator.getDefault().getSharedImages().descriptor(FoundationUIActivator.IMAGE_CHART_ICON));

		if (doubleClickAction == null) {
			doubleClickAction = new Action() {
				@Override
				public void run() {
					ISelection selection = getViewer().getSelection();
					doubleClickSelection(selection);
				}
			};
			doubleClickAction.setId(getClass().getName() + ".doubleClick");
		}
	}

	public Action getDoubleClickAction() {
		return doubleClickAction;
	}

	public void setDoubleClickAction(Action doubleClickAction) {
		this.doubleClickAction = doubleClickAction;
	}

	protected void doubleClickSelection(ISelection selection) {
		((IStructuredSelection) selection)
		.getFirstElement();
	}

	protected void hookDoubleClickAction() {
		Widgets.setDoubleClickAction(getViewer(), doubleClickAction);
	}

	protected void showMessage(String message) {
		MessageDialog.openInformation(getViewer().getControl().getShell(),
				"Message View", message);
	}



	@Override
	public abstract List<String> getColumns();

	@Override
	public boolean getDefaultVisibility(String column) {
		return true;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		getViewer().getControl().setFocus();
	}

	@Override
	public TableConfiguration getConfiguration() {
		if (configuration == null) {
			configuration = TableConfiguration.loadConfiguration(getColumnConfigurationId());
			configuration.addDefaultColumns(getColumns());
		}
		return configuration;
	}

	protected abstract TableChartOptions createChartOptions();

	public void setConfiguration(TableConfiguration configuration) {
	    if (configuration != this.configuration) {
	        if (this.configuration != null) {
	            this.configuration.removeColumnListeners(getViewer());
	        }
	        this.configuration = configuration;
            if (this.configuration != null) {
                this.configuration.addColumnListeners(getViewer());
            }
	    }
	}

	protected boolean showChartAction() {
		return showChartingOptions && chartOptions.isValid();
	}

	public TableChartOptions getChartOptions() {
		if (chartOptions == null) {
			chartOptions = createChartOptions();
		}
		return chartOptions;
	}


	/**
	 * Based on the table configuration and defaults lets update the label provider for the column
	 */
	protected void configureLabelProvider(ViewerColumn viewerColumn, ColumnConfiguration config, CellLabelProvider labelProvider) {
		Class<?> returnType = Objects.getReturnType(labelProvider);
		String style = config.getLabelProviderStyle();
		FunctionColumnLabelProvider flp = null;
		if (labelProvider instanceof FunctionColumnLabelProvider) {
			flp = (FunctionColumnLabelProvider) labelProvider;
			returnType = flp.getReturnType();
		}
		if (viewerColumn != null) {
			CellLabelProvider provider = config.getLabelProvider();
			if (provider != null) {
				viewerColumn.setLabelProvider(provider);
			} else {
				if (style != null && style.equals("timeThenDate")) {
					if (flp != null) {
						viewerColumn.setLabelProvider(new TimeThenDateFunctionLabelProvider(flp.getFunction()));
					} else {
						viewerColumn.setLabelProvider(new TimeThenDateLabelProvider());
					}
				} else if (style != null && style.equals("longTimestampAsTimeThenDate")) {
					viewerColumn.setLabelProvider(new LongTimestampAsTimeThenDateLabelProvider());
				} else if (Objects.isNumberType(returnType)) {
					if (style == null || !style.equals("plain")) {
						viewerColumn.setLabelProvider(new ChartLabelProvider(labelProvider, getViewer()));
					}
				} else if (Objects.equal(style, "image") && flp != null) {
					viewerColumn.setLabelProvider(new ImageLabelProvider(flp.getFunction()));
				}
			}
		}
	}


	/*
	protected boolean hasLabelProvider(ViewerColumn viewerColumn) {
		// lets use reflection
		Field field;
		try {
			field = ViewerColumn.class.getDeclaredField("labelProvider");
			field.setAccessible(true);
			Object provider = field.get(viewerColumn);
			return provider != null;
		} catch (Exception e) {
			Activator.getLogger().debug("Failed to get labelProvider field: " + e);
			return false;
		}
	}
	 */

	protected void setInput(Object input) {
		ObservableLists.removeListener(getViewer().getInput(), getChangeListener());
		ObservableLists.addListener(input, getChangeListener());
		Viewers.setInput(getViewer(), input);
		// TODO is this needed?
		//getViewer().refresh();
	}


	protected IChangeListener getChangeListener() {
		if (changeListener == null) {
			changeListener = new IChangeListener() {
				@Override
				public void handleChange(ChangeEvent event) {
					refreshViewerOnChange();
				}
			};
		}
		return changeListener;
	}

	/**
	 * The underlying model has changed so refresh the viewer
	 */
	protected void refreshViewerOnChange() {
		Viewers.refresh(getViewer());
	}

	public class ColumnFunctionComparator extends ViewerComparator {
		public static final int ASCENDING = 0;
		public static final int DESCENDING = 1;

		private int propertyIndex;
		private int defaultSortColumnIndex;
		private int direction = DESCENDING;

		public ColumnFunctionComparator() {
			this.propertyIndex = 0;
			direction = DESCENDING;
		}

		public void setDefaultSortColumn(int column) {
			defaultSortColumnIndex = column;
			setColumn(column);
		}

		public void setColumn(int column) {
			if (column == this.propertyIndex) {
				// Same column as last sort; toggle the direction
				direction = 1 - direction;
			} else {
				// New column; do an ascending sort
				this.propertyIndex = column;
				direction = ASCENDING;
			}
		}

		public void setDescending(boolean descending) {
			if (descending) {
				direction = DESCENDING;
			} else {
				direction = ASCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			return compareByColumn(e1, e2, propertyIndex);
		}

		protected int compareByColumn(Object e1, Object e2, int sortIndex) {
			int answer = 0;
			if (sortIndex < 0 || sortIndex >= functions.size()) {
				answer = compareDefaultSortColumn(e1, e2, sortIndex);
			} else {
				Function1 function = functions.get(sortIndex);
				Object v1 = function.apply(e1);
				Object v2 = function.apply(e2);
				answer = Objects.compare(v1, v2);
				if (answer == 0) {
					answer = compareDefaultSortColumn(e1, e2, sortIndex);
				}
			}
			if (direction == DESCENDING) {
				answer = -answer;
			}
			return answer;
		}

		public int compareDefaultSortColumn(Object e1, Object e2, int sortIndex) {
			if (sortIndex == defaultSortColumnIndex) {
				return Objects.compare(e1, e2);
			} else {
				return compareByColumn(e1, e2, defaultSortColumnIndex);
			}
		}

	}
}
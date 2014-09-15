/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.fabric8.ui.view.logs;

import io.fabric8.insight.log.LogFilter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.ui.Menus;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.Widgets;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;
import org.fusesource.ide.fabric8.core.dto.LogEventDTO;
import org.fusesource.ide.fabric8.ui.FabricPlugin;
import org.fusesource.ide.fabric8.ui.view.logs.actions.OpenStackTraceAction;

import com.google.common.base.Objects;

/**
 * Browse log events
 */
public class LogsView extends TableViewSupport implements LogContext, ISelectionListener { //, ITabbedPropertySheetPageContributor {
	public static final String ID = LogsView.class.getName();

	private ILogBrowser browser;
	private final Set<LogEventDTO> logEventList = Collections.synchronizedSet(new TreeSet<LogEventDTO>());
	private long logPollTime = 5000L;
	private Job job;
	private boolean newColumnsMayAppear = false;
	private boolean columnsEverChange = false;

	private LogFilter logFilter = new LogFilter();

	private boolean filterChanged;
	private OpenStackTraceAction openStackAction;
	
	public LogsView() {
		job = new Job("Querying logs...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (!logEventList.isEmpty()) {
					logFilter.setCount(50);
				}
				logFilter.setBeforeTimestamp(System.currentTimeMillis());
			
				queryLogs();
				
				// lets only stop re-scheduling when the widget is disposed
				if (!Widgets.isDisposed(getControl())) {
					schedule(logPollTime);
				} else {
					FabricPlugin.getLogger().warning("Control disposed to not rescheduling query job");
				}
				return Status.OK_STATUS;
			}

		};
		openStackAction = new OpenStackTraceAction(this, LogMessages.openStackTrace);
		setDoubleClickAction(openStackAction);
	}

	@Override
	protected String getHelpID() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.IConfigurableColumns#getColumnConfigurationId()
	 */
	@Override
	public String getColumnConfigurationId() {
		return ID;
	}

	@Override
	protected void addToolBarActions(IToolBarManager manager) {
		super.addToolBarActions(manager);
		Menus.addAction(manager, openStackAction);
	}

	@Override
	protected void addLocalMenus(IMenuManager manager) {
		super.addLocalMenus(manager);
		Menus.addAction(manager, openStackAction);
	}

	@Override
	protected void removeToolBarActions(IToolBarManager manager) {
		Menus.removeAction(manager, openStackAction);
		super.removeToolBarActions(manager);
	}

	@Override
	protected void removeLocalMenus(IMenuManager manager) {
		Menus.removeAction(manager, openStackAction);
		super.removeLocalMenus(manager);
	}

	@Override
	public LogFilter getLogFilter() {
		return logFilter;
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		if (site != null) {
			site.getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		}

		job.schedule();
	}

	public LogEventDTO getSelectedEvent() {
		return (LogEventDTO)Selections.getFirstSelection(getViewer());
	}

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        aboutToBeShown();
    }

	@Override
	public void dispose() {
		job.cancel();
		final IViewSite site = getViewSite();
		if (site != null) {
            site.getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		}
		super.dispose();
	}

	protected void queryLogs() {
		try {
			if (!Widgets.isValidFromOtherThread(getControl())) {
				FabricPlugin.getLogger().warning("Control not valid so not querying logs...");
				return;
			}
			boolean value = filterChanged;
			if (browser != null) {
				filterChanged = false;
				if (value) {
					// lets clear the from/to time to reset the search then switch afterwards to incremental searches
					logFilter.setAfterTimestamp(null);
					logFilter.setBeforeTimestamp(null);
				}
				browser.queryLogs(this, value);
			}
		} catch (Throwable e) {
			Activator.getLogger().warning("Failed to query logs: " + e, e);
		}
	}

	@Override
	public void addLogResults(final List<LogEventDTO> events) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				for (LogEventDTO event : events) {
					if (event != null) {
						// lets delete the existing event with the same sequence number?
						logEventList.add(event);
					}
				}
				onLogEventsChanged();
			}
		});
	}


	protected void onLogEventsChanged() {
		if (columnsEverChange) {
			if (newColumnsMayAppear ) {
				createViewer();
			} else {
				recreateColumns();
				Viewers.refresh(getViewer());
			}
		} else {
			// No need to refresh as the refresh happens on list change?
			Viewers.refresh(getViewer());
		}

	}

	protected void setLogEvents(final List<LogEventDTO> list) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				logEventList.clear();
				logEventList.addAll(list);
				onLogEventsChanged();
			}
		});
	}

	@Override
	protected void createViewer() {
		super.createViewer();
	}

	@Override
	public void refresh() {
		job.schedule();
	}


	@Override
	protected void createColumns() {
		int bounds = 100;
		int column = 0;
		clearColumns();

		Function1 function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getEventTimestamp();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Time");
		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getEventhost();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Host");


		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getContainer();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Container");


		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getLogLevel();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Level", new LogLevelImageProvider());

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getLogger();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Category");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getThreadName();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Thread");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					String msg = log.getLogMessage();
					if (msg.indexOf('\n') != -1) {
						return log.getLogMessage().substring(0, log.getLogMessage().indexOf('\n'));
					}
					return log.getLogMessage();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Message");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				return element;
			}
		};
		column = addColumnFunction(bounds, column, function, "Location", new LocationLabelProvider());

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getPropertiesMap();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Properties");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getSeq();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "ID");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventDTO log = (LogEventDTO)element;
				if (log != null) {
					return log.getException();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Exception");

	}

	@Override
	protected void configureViewer() {
		setInput(logEventList);
	}


	@Override
	protected IStructuredContentProvider createContentProvider() {
		return ArrayContentProvider.getInstance();
	}

	public void setExchangeBrowser(ILogBrowser browser) {
		if (!Objects.equal(this.browser, browser)) {
			this.browser = browser;
			onFilterChanged();
			logEventList.clear();
			setInput(logEventList);
			Viewers.refreshAsync(getViewer());
		}
		refresh();
	}

	@Override
	protected void onFilterChanged() {
		filterChanged = true;
		super.onFilterChanged();
		logFilter.setCount(0);
		logFilter.setBeforeTimestamp(System.currentTimeMillis());
		refresh();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.ColumnViewSupport#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// we only want to process selection change events from few selected sources...so filtering here
        if (!isRelevantSelectionSource(part, selection)) {
            return;
        }

        ILogBrowser browser = null;
        Object o = Selections.getFirstSelection(selection);
        if (o instanceof ILogBrowser) {
        	browser = (ILogBrowser)o;
        } else if (o instanceof HasLogBrowser) {
        	browser = ((HasLogBrowser)o).getLogBrowser();
        } 
        setExchangeBrowser(browser);
        onFilterChanged();
	}
	
	private boolean isRelevantSelectionSource(IWorkbenchPart part, ISelection selection) {
		boolean process = false;

		// we filter for specific selection sources...
		if (part.getClass().getName().equals("org.fusesource.ide.fabric8.ui.navigator.FabricNavigator")) {
			process = true;
		}

		return process;
	}
}
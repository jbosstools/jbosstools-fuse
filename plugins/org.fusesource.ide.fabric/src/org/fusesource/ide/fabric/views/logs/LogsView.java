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

package org.fusesource.ide.fabric.views.logs;

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
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.Activator;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.jobs.Jobs;
import org.fusesource.ide.commons.ui.Menus;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.Widgets;
import org.fusesource.ide.commons.ui.form.FormPropertySheetPage;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;
import org.fusesource.ide.fabric.views.logs.actions.OpenStackTraceAction;
import org.fusesource.insight.log.LogFilter;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

/**
 * Browse log events
 */
public class LogsView extends TableViewSupport implements LogContext { // implements ITabbedPropertySheetPageContributor {
	public static final String ID = LogsView.class.getName();
	private static Joiner joiner = Joiner.on("\n");

	private ILogBrowser browser;
	private final Set<LogEventBean> logEventList = Collections.synchronizedSet(new TreeSet<LogEventBean>());
	private long logPollTime = 5000L;
	private Job job;
	private boolean newColumnsMayAppear = false;
	private boolean columnsEverChange = false;

	private LogFilter logFilter = new LogFilter();

	private boolean filterChanged;
	private OpenStackTraceAction openStackAction;
	private ISelectionListener selectionListener = new ISelectionListener() {

        @Override
        public void selectionChanged(IWorkbenchPart part,
                ISelection selection) {

            // ignore selection from myself
            if (part instanceof LogsView) {
                return;
            }
            ILogBrowser browser = Logs.getSelectionLogBrowser(selection);
            setExchangeBrowser(browser);
        }

    };

	public LogsView() {
		// avoid fetching too many logs up front
		logFilter.setCount(50);
		getComparator().setDescending(false);

		job = new Job("Querying logs") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				queryLogs();

				// lets only stop re-scheduling when the widget is disposed
				if (!Widgets.isDisposed(getControl())) {
					Jobs.schedule(logPollTime, this);
				} else {
					System.out.println("Control disposed to not rescheduling query job");
				}
				return Status.OK_STATUS;
			}

		};

		job.setSystem(true);

		openStackAction = new OpenStackTraceAction(this, LogMessages.openStackTrace);
		setDoubleClickAction(openStackAction);
	}

	@Override
	protected String getHelpID() {
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
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			ISelection selection = viewer.getSelection();
			//TabbedPropertySheetPage answer = new TabbedPropertySheetPage(this);
			return new FormPropertySheetPage(new LogDetailForm());
		}
		return super.getAdapter(adapter);
	}


	@Override
	public LogFilter getLogFilter() {
		return logFilter;
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		if (site != null) {
			site.getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		}

		job.schedule();
	}


	public LogEventBean getSelectedEvent() {
		return LogEventBean.toLogEventBean(Selections.getFirstSelection(getViewer()));
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
            site.getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
		}
		super.dispose();
	}

	protected void queryLogs() {
		try {
			if (!Widgets.isValidFromOtherThread(getControl())) {
				System.out.println("Control not valid so not querying logs...");
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
	public void addLogResults(final List<LogEventBean> events) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				for (LogEventBean event : events) {
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

	protected void setLogEvents(final List<LogEventBean> list) {
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
				LogEventBean log = Logs.toLogEvent(element);
				if (log != null) {
					return log.getTimestamp();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Time");
		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventBean log = Logs.toLogEvent(element);
				if (log != null) {
					return log.getHost();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Host");


		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventBean log = Logs.toLogEvent(element);
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
				LogEventBean log = Logs.toLogEvent(element);
				if (log != null) {
					return log.getLevel();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Level", new LogLevelImageProvider());

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventBean log = Logs.toLogEvent(element);
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
				LogEventBean log = Logs.toLogEvent(element);
				if (log != null) {
					return log.getThread();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Thread");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventBean log = Logs.toLogEvent(element);
				if (log != null) {
					return log.getMessage();
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
				LogEventBean log = Logs.toLogEvent(element);
				if (log != null) {
					return log.getProperties();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Properties");

		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEventBean log = Logs.toLogEvent(element);
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
				LogEventBean log = Logs.toLogEvent(element);
				if (log != null) {
					String[] exception = log.getException();
					if (exception != null) {
						return joiner.join(exception);
					}
					return exception;
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Exception");

	}

	@Override
	protected void configureViewer() {
		setInput(logEventList);
		//new MessageDragListener(viewer).register();
	}


	@Override
	protected IStructuredContentProvider createContentProvider() {
		return ArrayContentProvider.getInstance();
	}

	public void setExchangeBrowser(ILogBrowser browser) {
		if (!Objects.equal(this.browser, browser)) {
			this.browser = browser;
			filterChanged = true;
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
		refresh();
	}



}
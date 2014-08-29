/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.jmx.fabric8.navigator.properties;

import io.fabric8.insight.log.LogEvent;

import java.util.Collection;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.ui.IWorkbenchPart;
import org.fusesource.ide.commons.ui.Selections;
import org.fusesource.ide.commons.ui.actions.SeparatorFactory;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.commons.util.Function1;
import org.fusesource.ide.jmx.fabric8.Fabric8JMXPlugin;
import org.fusesource.ide.jmx.fabric8.navigator.ContainerNode;
import org.fusesource.ide.jmx.fabric8.navigator.Fabric8Node;
import org.fusesource.ide.jmx.fabric8.navigator.properties.actions.OpenStackTraceAction;

/**
 * @author lhein
 */
public class LogViewTabSection extends TableViewSupport {

	protected static SeparatorFactory separatorFactory = new SeparatorFactory(LogViewTabSection.class.getName());
	
	private Fabric8Node fabric;
	private ContainerNode container;
	private Separator separator1 = separatorFactory.createSeparator();
	private OpenStackTraceAction openStackTraceAction;
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.IConfigurableColumns#getColumnConfigurationId()
	 */
	@Override
	public String getColumnConfigurationId() {
		return LogViewTabSection.class.getName();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.TableViewSupport#createColumns()
	 */
	@Override
	protected void createColumns() {
		int bounds = 100;
		int column = 0;
		clearColumns();
		Function1 function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEvent log = toLogEvent(element);
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
				LogEvent log = toLogEvent(element);
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
				LogEvent log = toLogEvent(element);
				if (log != null) {
					return log.getContainerName();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Container");
		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEvent log = toLogEvent(element);
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
				LogEvent log = toLogEvent(element);
				if (log != null) {
					return log.getLogger();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Logger");
		function = new Function1() {
			@Override
			public Object apply(Object element) {
				LogEvent log = toLogEvent(element);
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
				LogEvent log = toLogEvent(element);
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
				LogEvent log = toLogEvent(element);
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
				LogEvent log = toLogEvent(element);
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
				LogEvent log = toLogEvent(element);
				if (log != null && log.getException() != null) {
					StringBuffer sb = new StringBuffer();
					for (String s : log.getException()) {
						sb.append(s);
						sb.append('\n');
					}
					return sb.toString();
				}
				return null;
			}
		};
		column = addColumnFunction(bounds, column, function, "Exception");
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.TableViewSupport#configureViewer()
	 */
	@Override
	protected void configureViewer() {
		addLocalMenuActions(separator1, 
				getOpenStackTraceAction());

		addToolBarActions(getOpenStackTraceAction());
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.TableViewSupport#createContentProvider()
	 */
	@Override
	protected IStructuredContentProvider createContentProvider() {
		return ArrayContentProvider.getInstance();
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.ColumnViewSupport#getHelpID()
	 */
	@Override
	protected String getHelpID() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.ColumnViewSupport#setInput(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		Object o = Selections.getFirstSelection(selection);
		if (o instanceof Fabric8Node) {
			if (this.fabric == o) {
				return;
			}
			this.fabric = (Fabric8Node)o;
		
		} else if (o instanceof ContainerNode) {
			if (this.container == o) {
				return;
			}
			this.container = (ContainerNode)o;
			this.fabric = this.container.getFabric();
		}
		
//		if (current != null) {
//			current.removeFabricUpdateRunnable(refreshRunnable);
//		}
//		current = fabric;
//		if (current != null) {
//			current.addFabricUpdateRunnable(refreshRunnable);
//		}
		
		try {
			Collection<LogEvent> logs = this.fabric.getFacade().queryLogs(-1);
			setInput(logs);
			getViewer().setInput(logs);
		} catch (Exception ex) {
			Fabric8JMXPlugin.getLogger().error(ex);
		}

		getViewer().refresh(true);

//		updateActionStatus();
	}
	
	protected OpenStackTraceAction getOpenStackTraceAction() {
		if (openStackTraceAction == null) {
			openStackTraceAction = createOpenStackTraceAction();
		}
		return openStackTraceAction;
	}
	
	protected OpenStackTraceAction createOpenStackTraceAction() {
		return new OpenStackTraceAction(this, "Open Stacktrace");
	}
	
	public static LogEvent toLogEvent(Object element) {
		if (element != null && element instanceof LogEvent) {
			return (LogEvent)element;
		}
		return null;
	}
	
	public LogEvent getSelectedEvent() {
		int idx = getTable().getSelectionIndex();
		if (idx != -1) {
			if (getTable().getItem(idx).getData() instanceof LogEvent) {
				return (LogEvent)getTable().getItem(idx).getData();
			}
		}
		return null;
	}
}

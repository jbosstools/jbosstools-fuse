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

package org.fusesource.ide.jmx.commons.views.messages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.fusesource.ide.commons.Viewers;
import org.fusesource.ide.commons.ui.config.TableConfiguration;
import org.fusesource.ide.commons.ui.views.TableViewSupport;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.functions.FunctionLong;
import org.fusesource.ide.jmx.commons.messages.Exchange;
import org.fusesource.ide.jmx.commons.messages.Exchanges;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IExchangeBrowser;
import org.fusesource.ide.jmx.commons.messages.IMessage;
import org.fusesource.ide.jmx.commons.messages.ITraceExchangeBrowser;


/**
 * UI for viewing messages
 */
public class MessagesView extends TableViewSupport { // implements ITabbedPropertySheetPageContributor {

	public static final String ID = "org.fusesource.ide.fabric.views.MessagesView";

	IExchangeBrowser browser;
	List<IExchange> exchanges = new ArrayList<IExchange>();

	private boolean showBody = true;
	private boolean showTimestamp = true;
	private boolean showRelativeTime = true;
	private boolean showToNode = true;
	private boolean showTraceExchangeId = true;
	private boolean showElapsedTime = true;
	private ISelectionListener selectionListener = new ISelectionListener() {

        @Override
        public void selectionChanged(IWorkbenchPart part,
                ISelection selection) {

            // we only want to process selection change events from few selected sources...so filtering here
            if (!isRelevantSelectionSource(part, selection)) {
                return;
            }

            IExchangeBrowser browser = ExchangeBrowsers.getSelectedExchangeBrowser(selection);
            setExchangeBrowser(browser);
        }

    };

	public MessagesView() {
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
		String key = ID;
		if (browser instanceof ITraceExchangeBrowser) {
			key += "_tracing";
		}
		return key;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == IPropertySheetPage.class) {
			ISelection selection = viewer.getSelection();
			MessageDetailView view = new MessageDetailView();
			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				Object o = ((IStructuredSelection)selection).getFirstElement();
				if (o instanceof Exchange) {
					view.setSelectedExchange((Exchange)o);
				}
			}
			return view;
		}
		return super.getAdapter(adapter);
	}


	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		if (site != null) {
			site.getWorkbenchWindow().getSelectionService().addSelectionListener(selectionListener);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.commons.ui.views.TableViewSupport#getColumns()
	 */
	@Override
	public List<String> getColumns() {
		TableConfiguration tc = getConfiguration();
		List<String> cols = new ArrayList<String>();
		
		if (tc == null || tc.hasColumns() == false) return super.getColumns();
		
		tc.reload();
		Iterator<String> names = tc.getColumnMap().keySet().iterator();
		while (names.hasNext()) {
			cols.add(names.next());
		}
		tc.sortDefaultColumnNames(cols);
		
		return cols;
	}
	
	@Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        aboutToBeShown();
    }

    @Override
    public void dispose() {
	    IViewSite site = getViewSite();
	    if (site != null) {
	        site.getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionListener);
	    }
        super.dispose();
    }

    private boolean isRelevantSelectionSource(IWorkbenchPart part, ISelection selection) {
		boolean process = false;

		// we filter for specific selection sources...
		if (part.getClass().getName().equals("org.jboss.tools.jmx.ui.internal.views.navigator.JMXNavigator") ||
			part.getClass().getName().equals("org.fusesource.ide.fabric8.ui.navigator.FabricNavigator") ||
			part.getClass().getName().equals("org.fusesource.ide.jmx.commons.views.diagram.DiagramView") ||
			part.getClass().getName().equals("org.eclipse.wst.server.ui.internal.view.servers.ServersView") ||
			part.getClass().getName().equals("org.eclipse.wst.server.ui.internal.cnf.ServersView2")
			) {
			process = true;
		}

		return process;
	}

	@Override
	protected void createViewer() {
		super.createViewer();
		// set the comparator to be default ascending sorted so oldest is in the top
		// TODO: setDefaultSortColumnIndex(0);
		getComparator().setDescending(false);
	}

	@Override
	public void refresh() {
		if (browser != null) {
			List<IExchange> list = browser.browseExchanges();
			if (list != null) {
				// FabricPlugin.getLogger().debug("==== Browsed exchanges: " + list);
				this.exchanges = list;
				viewer.setInput(exchanges);
				/*
				recreateColumns();
				getViewer().refresh();
				 */
				// only recreate viewer if its valid and visible
				//if (Viewers.isValid(viewer)) {
				//	createViewer();
				//}
			}
		} else {
			// create an empty viewer in case its not a browsable node
			this.exchanges.clear();
			viewer.refresh();
		}
		
		setConfiguration(null);
		updateColumnConfiguration(getConfiguration());
		Viewers.refresh(getViewer());
	}

	@Override
	protected void createColumns() {
		int bounds = 100;
		int column = 0;
		clearColumns();

		if (browser instanceof ITraceExchangeBrowser) {
			final Function1 function = new Function1() {
				@Override
				public Object apply(Object element) {
					Exchange exchange = Exchanges.asExchange(element);
					if (exchange != null) {
						return exchange.getExchangeIndex();
					}
					return null;
				}
			};
			column = addColumnFunction(bounds, column, function, "Trace ID");
		}

		// TODO add exchange / message ID??
		if (browser instanceof ITraceExchangeBrowser) {
			if (showTraceExchangeId) {
				final Function1 function = new Function1() {
					@Override
					public Object apply(Object element) {
						Exchange exchange = Exchanges.asExchange(element);
						if (exchange != null) {
							return exchange.getId();
						}
						return null;
					}
				};
				column = addColumnFunction(bounds, column, function, "Exchange ID");
			}
		}
		if (showBody) {
			final Function1 function = new Function1() {
				@Override
				public Object apply(Object element) {
					if (element instanceof IExchange) {
						IExchange exchange = (IExchange) element;
						String answer = MessageUIHelper.getBody(exchange);
						if (answer != null) {
							// lets replace newlines
							return answer.replace('\n', ' ');
						}
					}
					return null;
				}
			};
			column = addColumnFunction(bounds, column, function, "Message Body");
		}

		SortedSet<String> headers = new TreeSet<String>();
		for (IExchange exchange : exchanges) {
			IMessage in = exchange.getIn();
			if (in != null) {
				headers.addAll(in.getHeaders().keySet());
			}
		}
		for (final String header : headers) {
			final Function1 function = new Function1() {
				@Override
				public Object apply(Object element) {
					if (element instanceof IExchange) {
						IExchange exchange = (IExchange) element;
						IMessage in = exchange.getIn();
						if (in != null) {
							return in.getHeaders().get(header);
						}
					}
					return null;
				}
			};
			column = addColumnFunction(bounds, column, function, header);
		}
		if (browser instanceof ITraceExchangeBrowser) {
			if (showToNode) {
				final Function1 function = new Function1() {
					@Override
					public Object apply(Object element) {
						IMessage message = Exchanges.asMessage(element);
						if (message != null) {
							return message.getToNode();
						}
						return null;
					}
				};
				column = addColumnFunction(bounds, column, function, "Trace Node Id");
			}
			if (showTimestamp) {
				final Function1 function = new Function1() {
					@Override
					public Object apply(Object element) {
						IMessage message = Exchanges.asMessage(element);
						if (message != null) {
							return message.getTimestamp();
						}
						return null;
					}
				};
				column = addColumnFunction(bounds, column, function, "Trace Timestamp");
			}
			if (showRelativeTime) {
				final Function1 function = new FunctionLong() {
					@Override
					public Long apply(Object element) {
						IMessage message = Exchanges.asMessage(element);
						if (message != null) {
							return message.getRelativeTime();
						}
						return null;
					}
				};
				column = addColumnFunction(bounds, column, function, "Relative Time (ms)");
			}
			if (showElapsedTime) {
				final Function1 function = new FunctionLong() {
					@Override
					public Long apply(Object element) {
						IMessage message = Exchanges.asMessage(element);
						if (message != null) {
							return message.getElapsedTime();
						}
						return null;
					}
				};
				column = addColumnFunction(bounds, column, function, "Elapsed Time (ms)");
			}
		}
	}


	@Override
	protected void configureViewer() {
		viewer.setInput(exchanges);
		new MessageDragListener(viewer).register();
	}

	@Override
	protected IStructuredContentProvider createContentProvider() {
		return new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object parent) {
				return exchanges.toArray();
			}

		};
	}

	public void setExchangeBrowser(IExchangeBrowser browser) {
		this.browser = browser;
		refresh();
	}

}
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

package org.fusesource.ide.jmx.camel.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.RuntimeCamelException;
import org.apache.camel.api.management.mbean.BacklogTracerEventMessage;
import org.apache.camel.api.management.mbean.ManagedBacklogTracerMBean;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.camel.model.AbstractNode;
import org.fusesource.ide.camel.model.AbstractNodeFacade;
import org.fusesource.ide.camel.model.Activator;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.io.XmlContainerMarshaller;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.commons.tree.Refreshable;
import org.fusesource.ide.commons.ui.ContextMenuProvider;
import org.fusesource.ide.commons.ui.Workbenches;
import org.fusesource.ide.commons.util.Nodes;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.Messages;
import org.fusesource.ide.jmx.camel.editor.CamelContextNodeEditorInput;
import org.fusesource.ide.jmx.camel.internal.CamelContextMBean;
import org.fusesource.ide.jmx.camel.internal.CamelFabricTracerMBean;
import org.fusesource.ide.jmx.camel.internal.CamelFacade;
import org.fusesource.ide.jmx.camel.internal.CamelProcessorMBean;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IMessage;
import org.fusesource.ide.jmx.commons.messages.ITraceExchangeBrowser;
import org.fusesource.ide.jmx.commons.messages.ITraceExchangeList;
import org.fusesource.ide.jmx.commons.messages.NodeStatisticsContainer;
import org.jboss.tools.jmx.ui.ImageProvider;


public class CamelContextNode extends NodeSupport implements Refreshable, AbstractNodeFacade, ContextMenuProvider, ITraceExchangeBrowser,
ImageProvider {

	public static final String CAMEL_EDITOR_ID = "org.fusesource.ide.camel.editor";
	private final CamelContextsNode camelContextsNode;
	private final CamelFacade facade;
	private final CamelContextMBean camelContextMBean;
	private XmlContainerMarshaller marshaller = new XmlContainerMarshaller();
	private final RoutesNode routes;
	private static Map<String, TraceExchangeList> traceMessageMap = new ConcurrentHashMap<String, TraceExchangeList>();
	private NodeStatisticsContainer runtimeNodeStatisticsContainer;

	public CamelContextNode(CamelContextsNode camelContextsNode, CamelFacade facade, CamelContextMBean camelContext) throws Exception {
		super(camelContextsNode);
		this.camelContextsNode = camelContextsNode;
		this.facade = facade;
		this.camelContextMBean = camelContext;

		routes = new RoutesNode(this);
		addChild(routes);
		addChild(new EndpointsNode(this));
		setPropertyBean(camelContext);
	}

	@Override
	public String toString() {
		return getContextId();
	}

	@Override
	public void refresh() {
		Nodes.refreshParent(this);
	}

	public CamelContextsNode getCamelContextsNode() {
		return camelContextsNode;
	}

	public String getContextId() {
		try {
			return camelContextMBean.getCamelId();
		} catch (Exception e) {
			return "";
		}
	}

	public String getManagementName() {
		try {
			return camelContextMBean.getManagementName();
		} catch (Exception e) {
			return "";
		}
	}

	public CamelFacade getFacade() {
		return facade;
	}

	public CamelContextMBean getCamelContextMBean() {
		return camelContextMBean;
	}

	public String getXmlString() {
		try {
			return camelContextMBean.dumpRoutesAsXml();
		} catch (Exception e) {
			return "";
		}
	}

	public RouteContainer getModelContainer() {
		String xml = getXmlText();
		return marshaller.loadRoutesFromText(xml);
	}

	public String getXmlText() {
		String xml = getXmlString();
		// TODO - REMOVE ASAP
		// lets add in the camel context...
		if (!xml.contains("<camelContext")) {
			String routeElement = "<routes";
			if (xml.contains(routeElement)) {
				xml = xml.replace(routeElement, "<camelContext").replace("</routes>", "</camelContext>");
			}
		}
		return xml;
	}

	public void updateXml(String xml) {
		camelContextMBean.addOrUpdateRoutesFromXml(xml);
		refresh();
	}

	@Override
	public AbstractNode getAbstractNode() {
		return getRoutes().getAbstractNode();
	}

	public XmlContainerMarshaller getMarshaller() {
		return marshaller;
	}

	public void setMarshaller(XmlContainerMarshaller marshaller) {
		this.marshaller = marshaller;
	}

	public RoutesNode getRoutes() {
		return routes;
	}

	public boolean isTracing() {
		Object tracer = getTracer();
		if (tracer == null) return false;
		
		if (tracer instanceof ManagedBacklogTracerMBean) {
			return ((ManagedBacklogTracerMBean)tracer).isEnabled();
		} else {
			return ((CamelFabricTracerMBean)tracer).isEnabled();
		}
	}

	// Operations

	/**
	 * Sends a message to the given URI
	 */
	public void send(String endpointUri, IMessage message) {
		Object body = message.getBody();
		Map<String, Object> headers = message.getHeaders();
		getCamelContextMBean().sendBodyAndHeaders(endpointUri, body, headers);

		reloadRoutes();
	}

	public void startTracing() {
		try {
			Object tracer = getTracer();
			if (tracer instanceof ManagedBacklogTracerMBean) {
				((ManagedBacklogTracerMBean)tracer).setEnabled(true);
			} else {
				((CamelFabricTracerMBean)tracer).setEnabled(true);
			}
			reloadRoutes();
			refresh();
		} catch (Exception e) {
			CamelJMXPlugin.showUserError("Failed to start tracing", "Failed to start tracing context " + this, e);
		}
	}

	protected void reloadRoutes() {
		// we now need to force a reload of the Camel routes models; so that they include any generated IDs...
		routes.refresh();
	}

	public void stopTracing() {
		Object tracer = getTracer();
		if (tracer instanceof ManagedBacklogTracerMBean) {
			((ManagedBacklogTracerMBean)tracer).setEnabled(false);
		} else {
			((CamelFabricTracerMBean)tracer).setEnabled(false);
		}
		traceMessageMap.remove(getContextId());
		reloadRoutes();
		refresh();
	}

	public void editRoutes() {
		IWorkbenchPage page = Workbenches.getActiveWorkbenchPage();
		if (page == null) {
			Activator.getLogger().warning("No active page!");
		} else {
			IEditorInput input = new CamelContextNodeEditorInput(this);
			try {
				page.openEditor(input, CAMEL_EDITOR_ID, true);
			} catch (PartInitException e) {
				Activator.getLogger().warning("Could not open editor: " + CAMEL_EDITOR_ID + ". Reason: " + e, e);
			}
			/*
			IViewPart view = page.findView(CAMEL_EDITOR_ID);
			if (view == null) {
				try {
					view = page.showView(CAMEL_EDITOR_ID);
				} catch (PartInitException e) {
					Activator.getLogger().warning("Could not find editor: " + CAMEL_EDITOR_ID);
				}
			}
			if (view != null) {
				if (view instanceof IEditorPart) {
					IEditorPart editor = (IEditorPart) view;
					IEditorInput input = new CamelContextNodeEditorInput(this);
					IEditorSite site = null;
					try {
						editor.init(site , input);
						editor.setFocus();
					} catch (PartInitException e) {
						Activator.getLogger().warning("Could not initialise editor: " + view);
					}
				} else {
					Activator.getLogger().warning("View is not an IEditorPart: " + view);
				}
			}
			 */
		}


	}

	@Override
	public void provideContextMenu(IMenuManager menu) {
		provideContextMenu(menu, null);

	}

	public void provideContextMenu(IMenuManager menu, Function1<IMenuManager, Void> fn) {
		Action editAction = new Action(Messages.EditRoutesAction, SWT.CHECK) {
			@Override
			public void run() {
				editRoutes();
			}
			/* (non-Javadoc)
			 * @see org.eclipse.jface.action.Action#isEnabled()
			 */
			@Override
			public boolean isEnabled() {
				return routes.getChildCount()>0;
			}
		};
		editAction.setToolTipText(Messages.EditRoutesActionToolTip);
		editAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("edit_camel_route.png"));
		menu.add(editAction);

		if (fn != null) {
			fn.apply(menu);
		} else {
			if (isMbeanSuspended()) {
				Action resumeContextAction = new Action(Messages.ResumeCamelContextAction, SWT.CHECK) {
					@Override
					public void run() {
						resumeMBean();
					}
				};
				resumeContextAction.setToolTipText(Messages.ResumeCamelContextActionToolTip);
				resumeContextAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("start_camel.png"));
				menu.add(resumeContextAction);
			} else {
				Action suspendContextAction = new Action(Messages.SuspendCamelContextAction, SWT.CHECK) {
					@Override
					public void run() {
						suspendMBean();
					}
				};
				suspendContextAction.setToolTipText(Messages.SuspendCamelContextActionToolTip);
				suspendContextAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("pause_camel.png"));
				menu.add(suspendContextAction);
			}
//			Action stopContextAction = new Action(Messages.StopCamelContextAction, SWT.CHECK) {
//				@Override
//				public void run() {
//					stopMBean();
//				}
//			};
//			stopContextAction.setToolTipText(Messages.StopCamelContextActionToolTip);
//			stopContextAction.setImageDescriptor(FabricPlugin.getDefault().getImageDescriptor("stop_camel.png"));
//			menu.add(stopContextAction);
		}

		if (canTrace()) {
			menu.add(new Separator());
			boolean tracing = isTracing();
			Action traceAction;
			if (tracing) {
				traceAction = new Action(Messages.StopTraceAction, SWT.CHECK) {
					@Override
					public void run() {
						stopTracing();
					}
					/* (non-Javadoc)
					 * @see org.eclipse.jface.action.Action#isEnabled()
					 */
					@Override
					public boolean isEnabled() {
						return routes.getChildCount()>0;
					}
				};
				traceAction.setToolTipText(Messages.StopTraceActionToolTip);
				traceAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("stop_tracing.png"));
			} else {
				traceAction = new Action(Messages.TraceAction, SWT.CHECK) {
					@Override
					public void run() {
						startTracing();
					}
					/* (non-Javadoc)
					 * @see org.eclipse.jface.action.Action#isEnabled()
					 */
					@Override
					public boolean isEnabled() {
						return routes.getChildCount()>0;
					}
				};
				traceAction.setToolTipText(Messages.TraceActionToolTip);
				traceAction.setImageDescriptor(CamelJMXPlugin.getDefault().getImageDescriptor("start_tracing.png"));
			}
			menu.add(traceAction);
		}
	}

	/**
	 * Returns true if the camel version allows tracing
	 */
	protected boolean canTrace() {
		return getTracer() != null;
	}

	protected void startMBean() {
		camelContextMBean.start();
		refresh();
	}

	protected void stopMBean() {
		camelContextMBean.stop();
		camelContextsNode.removeChild(this);
	}

	protected void suspendMBean() {
		camelContextMBean.suspend();
		refresh();
	}

	protected void resumeMBean() {
		camelContextMBean.resume();
		refresh();
	}


	public boolean isMbeanSuspended() {
		String state = camelContextMBean.getState();
		return Objects.equal("Suspended", state);
	}

	@Override
	public List<IExchange> browseExchanges() {
		// String nodeId = getContextId();
		// TODO remove when we can browse by context ID
		String nodeId = null;
		return getTraceExchanges(nodeId);
	}

	@Override
	public NodeStatisticsContainer getNodeStatisticsContainer() {
		//		if (isTracing()) {
		//			return getTraceExchangeList(null);
		//		} else {
		if (runtimeNodeStatisticsContainer == null) {
			runtimeNodeStatisticsContainer = new CachingCamelContextNodeStatisticsContainer(this);
		}
		return runtimeNodeStatisticsContainer;
		//		}
	}

	public NodeStatisticsContainer getNodeStatisticsContainer(String routeId) {
		//		if (isTracing()) {
		//			return getTraceExchangeList(routeId);
		//		} else {
		return getNodeStatisticsContainer();
		//		}
	}

	public ITraceExchangeList getTraceExchangeList(String id) {
		if (id != null) {
			// TODO lets find all the messages and filter them...
			return getTraceExchangeList(null);
		}
		TraceExchangeList traceList = null;
		String key = id;
		if (key == null) {
			key = getContextId();
		}
		traceList = traceMessageMap.get(key);
		if (traceList == null) {
			traceList = new TraceExchangeList();
			traceMessageMap.put(key, traceList);
		}
		try {
			// TODO we should add trace messages for a specific route ID to the
			// all routes
			Object tracer = getTracer();
			if (tracer instanceof ManagedBacklogTracerMBean) {
				ManagedBacklogTracerMBean camelTracer = (ManagedBacklogTracerMBean)tracer;
				if (camelTracer != null) {
					List<BacklogTracerEventMessage> traceMessages = null;
					if (id == null) {
						traceMessages = camelTracer.dumpAllTracedMessages();
					} else {
						traceMessages = camelTracer.dumpTracedMessages(id);
					}
					traceList.addBackLogTraceMessages(traceMessages);
				} else {
					// TODO should we highlight in the UI that there's no tracer enabled?
				}
			} else {
				CamelFabricTracerMBean fabricTracer = (CamelFabricTracerMBean)tracer;
				if (fabricTracer != null) {
					String traceXml = fabricTracer.dumpAllTracedMessagesAsXml();
					List<BacklogTracerEventMessage> traceMessages;
					if (id == null) {
						traceMessages = getTraceMessagesFromXml(traceXml);
					} else {
						traceMessages = getTraceMessagesFromXml(traceXml, id);
					}
					traceList.addFabricTraceMessages(traceMessages);
				} else {
					// TODO should we highlight in the UI that there's no tracer enabled?
				}
			}			
		} catch (Exception e) {
			CamelJMXPlugin.showUserError("Failed to get tracing messages",
					"Failed to get tracing messages on CamelContext " + this, e);
		}
		return traceList;
	}
	
	private List<BacklogTracerEventMessage> getTraceMessagesFromXml(String xmlDump) {
		List<BacklogTracerEventMessage> events = new ArrayList<BacklogTracerEventMessage>();
		
		// TODO: unmarshal events from the xml and put to the list of messages
		System.err.println("TODO: CamelContextNode.getTraceMessagesFromXml()");
	
		System.out.println(xmlDump);
		
		return events;
	}

	private List<BacklogTracerEventMessage> getTraceMessagesFromXml(String xmlDump, String id) {
		List<BacklogTracerEventMessage> events = new ArrayList<BacklogTracerEventMessage>();
		
		// TODO: unmarshal events from the xml and put to the list of messages
		System.err.println("TODO: CamelContextNode.getTraceMessagesFromXml(id)");
		
		System.out.println(id + ": >>> \n" + xmlDump);
		
		return events;
	}
	
	@Override
	public Image getImage() {
		if (isTracing()) {
			return CamelJMXPlugin.getDefault().getImage("camel_tracing.png");
		} else {
			return CamelJMXPlugin.getDefault().getImage("camel.png");
		}
	}

	public List<IExchange> getTraceExchanges(String id) {
		return getTraceExchangeList(id).getExchangeList();
	}

	public Object getTracer() {
		try {
			ManagedBacklogTracerMBean mbean = getFacade().getCamelTracer(getManagementName());
			if (mbean != null) {
				return mbean;
			} else {
				return getFacade().getFabricTracer(getManagementName());
			}
		} catch (Exception e) {
			throw new RuntimeCamelException(e);
		}
	}

	public CamelProcessorMBean getProcessorMBean(String nodeId) {
		return CamelFacades.getProcessorMBean(getFacade(), getManagementName(), nodeId);
	}

	public Object createProcessorBeanView(String routeId, String nodeId) {
		return new ProcessorBeanView(this, routeId, nodeId);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CamelContextNode && obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if( getConnection() != null && getConnection().getProvider() != null ) {
			return ("CamelContextNode-" + camelContextsNode.toString() + "-" + toString() + "-" + getConnection().getProvider().getName(getConnection())).hashCode();
		}
		return super.hashCode();
	}
}

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

package org.fusesource.ide.jmx.camel.navigator;

import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.fusesource.ide.camel.editor.CamelEditor;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelBacklogTracerMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelContextMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelFabricTracerMBean;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelJMXFacade;
import org.fusesource.ide.camel.model.service.core.jmx.camel.CamelProcessorMBean;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.camel.model.service.core.model.CamelRouteContainerElement;
import org.fusesource.ide.foundation.core.functions.Function1;
import org.fusesource.ide.foundation.core.util.IOUtils;
import org.fusesource.ide.foundation.core.util.Objects;
import org.fusesource.ide.foundation.ui.tree.NodeSupport;
import org.fusesource.ide.foundation.ui.tree.RefreshNodeRunnable;
import org.fusesource.ide.foundation.ui.tree.Refreshable;
import org.fusesource.ide.foundation.ui.util.ContextMenuProvider;
import org.fusesource.ide.foundation.ui.util.Nodes;
import org.fusesource.ide.foundation.ui.util.Workbenches;
import org.fusesource.ide.jmx.camel.CamelJMXPlugin;
import org.fusesource.ide.jmx.camel.Messages;
import org.fusesource.ide.jmx.camel.editor.CamelContextNodeEditorInput;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessage;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessageParser;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessages;
import org.fusesource.ide.jmx.commons.messages.IExchange;
import org.fusesource.ide.jmx.commons.messages.IMessage;
import org.fusesource.ide.jmx.commons.messages.ITraceExchangeBrowser;
import org.fusesource.ide.jmx.commons.messages.ITraceExchangeList;
import org.fusesource.ide.jmx.commons.messages.NodeStatisticsContainer;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.remote.debug.RemoteCamelLaunchConfigurationDelegate;
import org.fusesource.ide.launcher.run.util.CamelContextLaunchConfigConstants;
import org.jboss.tools.jmx.core.IConnectionProvider;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.ui.ImageProvider;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class CamelContextNode 	extends NodeSupport 
							 	implements Refreshable, ContextMenuProvider, ITraceExchangeBrowser, ImageProvider {

	public static final String CAMEL_EDITOR_ID = "org.fusesource.ide.camel.editor";
	
	private final CamelContextsNode camelContextsNode;
	private final CamelJMXFacade facade;
	private final CamelContextMBean camelContextMBean;
	private CamelRouteContainerElement camelContext;
	private final RoutesNode routes;
	private static Map<String, TraceExchangeList> traceMessageMap = new ConcurrentHashMap<>();
	private NodeStatisticsContainer runtimeNodeStatisticsContainer;
	private File tempContextFile = null;

	private ILaunch launch = null;

	private IEditorPart camelEditor;
	
	public CamelContextNode(CamelContextsNode camelContextsNode, CamelJMXFacade facade, CamelContextMBean camelContext) {
		super(camelContextsNode);
		this.camelContextsNode = camelContextsNode;
		this.facade = facade;
		this.camelContextMBean = camelContext;

		routes = new RoutesNode(this);
		addChild(routes);
		addChild(new EndpointsNode(this));
		setPropertyBean(camelContext);
	}

	public CamelRouteContainerElement getRouteContainer() {
		IFile camelContextFile = createTempContextFile();
		if (camelContextFile != null) {
			CamelIOHandler handler = new CamelIOHandler();
			try {
				camelContextFile.getProject().refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor());
				CamelFile cf = handler.loadCamelModel(camelContextFile, new NullProgressMonitor());
				this.camelContext = cf.getRouteContainer();
			} catch (Exception ex) {
				CamelJMXPlugin.getLogger().error(ex);
			}			
		} else {
			CamelJMXPlugin.getLogger().error("Unable to store the remote camel context " + getContextId() + " locally");
		}
		
		return this.camelContext;
	}
	
	@Override
	public String toString() {
		return getContextId();
	}

	@Override
	public void refresh() {
		Nodes.refreshParent(this);
		Display.getDefault().syncExec(new RefreshNodeRunnable(this));
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

	public CamelJMXFacade getFacade() {
		return facade;
	}

	public CamelContextMBean getCamelContextMBean() {
		return camelContextMBean;
	}

	public String getXmlString() {
		try {
			return camelContextMBean.dumpRoutesAsXml();
		} catch (Exception e) {
			CamelJMXPlugin.getLogger().error("Cannot retrieve xml of the Camel route through JMX MBean", e);
			return "";
		}
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

	public RoutesNode getRoutes() {
		return routes;
	}

	public boolean isTracing() {
		Object tracer = getTracer();
		if (tracer == null){
			return false;
		}
		
		if (tracer instanceof CamelBacklogTracerMBean) {
			return ((CamelBacklogTracerMBean)tracer).isEnabled();
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
			if (tracer instanceof CamelBacklogTracerMBean) {
				((CamelBacklogTracerMBean)tracer).setEnabled(true);
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
		if (tracer instanceof CamelBacklogTracerMBean) {
			((CamelBacklogTracerMBean)tracer).setEnabled(false);
		} else {
			((CamelFabricTracerMBean)tracer).setEnabled(false);
		}
		traceMessageMap.remove(getContextId());
		reloadRoutes();
		refresh();
	}

	public ILaunch editRoutes() {
		IWorkbenchPage page = Workbenches.getActiveWorkbenchPage();
		if (page == null) {
			CamelJMXPlugin.getLogger().warning("No active page!");
		} else {
			IFile camelContextFile = createTempContextFile();
			if (camelContextFile != null) {
				openEditor(page, camelContextFile);
				launch = bindRemoteCamelDebug(camelContextFile);
				return launch;
			} else {
				CamelJMXPlugin.getLogger().error("Unable to store the remote camel context " + getContextId() + " locally");
			}
		}
		return null;
	}

	private ILaunch bindRemoteCamelDebug(IFile camelContextFile) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = manager.getLaunchConfigurationType(RemoteCamelLaunchConfigurationDelegate.LAUNCH_CONFIGURATION_TYPE);
		try {
			ILaunchConfigurationWorkingCopy configurationWorkingCopy = launchConfigurationType.newInstance(null, "Remote Camel Debug - "+ camelContextFile.getName());
			configureJMXParameter(configurationWorkingCopy);
			configurationWorkingCopy.setAttribute(CamelContextLaunchConfigConstants.ATTR_FILE, camelContextFile.getLocation().toOSString());
			ILaunchConfiguration launchConfiguration = configurationWorkingCopy.doSave();
			return launchConfiguration.launch(ILaunchManager.DEBUG_MODE, new NullProgressMonitor());
		} catch (CoreException e) {
			CamelJMXPlugin.getLogger().warning("Cannot bind Camel Remote Debug", e);
			return null;
		}
	}

	private void configureJMXParameter(ILaunchConfigurationWorkingCopy configurationWorkingCopy) {
		IConnectionWrapper connection = getConnection();
		IConnectionProvider provider = connection.getProvider();
		String providerId = provider.getId();
		String connectionName = provider.getName(connection);
		configurationWorkingCopy.setAttribute(ICamelDebugConstants.ATTR_JMX_CONNECTION_WRAPPER_PROVIDER_ID, providerId);
		configurationWorkingCopy.setAttribute(ICamelDebugConstants.ATTR_JMX_CONNECTION_WRAPPER_CONNECTION_NAME , connectionName);
	}

	private void openEditor(IWorkbenchPage page, IFile camelContextFile) {
		IEditorInput input = new CamelContextNodeEditorInput(this, camelContextFile);
		try {
			camelEditor = page.openEditor(input, CAMEL_EDITOR_ID, true);
		} catch (PartInitException e) {
			CamelJMXPlugin.getLogger().warning("Could not open editor: " + CAMEL_EDITOR_ID + ". Reason: " + e, e);
		}
	}

	protected IFile createTempContextFile() {
		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(".FuseRemoteCamelContextData");
			if (!project.exists()) {
			    project.create(new NullProgressMonitor());
			}
			if(!project.isOpen()){
				project.open(new NullProgressMonitor());
			}
			if (tempContextFile == null) {
				tempContextFile = File.createTempFile("camelContext--" + getContextId() + "--", ".xml", project.getLocation().toFile());
				tempContextFile.deleteOnExit();
			}

			String xml = getXmlString();
			IOUtils.writeText(tempContextFile, xml);

			IFile camelContextFile = project.getFile(tempContextFile.getName());
			
			camelContextFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
			
			return camelContextFile;
		} catch (Exception e) {
			CamelJMXPlugin.getLogger().warning("Failed to create temporary file: " + e, e);
		}

		return null;
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
		String key = getContextId();
		TraceExchangeList traceList = traceMessageMap.get(key);
		if (traceList == null) {
			traceList = new TraceExchangeList();
			traceMessageMap.put(key, traceList);
		}
		try {
			// TODO we should add trace messages for a specific route ID to the
			// all routes
			Object tracer = getTracer();
			if (tracer instanceof CamelBacklogTracerMBean) {
				CamelBacklogTracerMBean camelTracer = (CamelBacklogTracerMBean)tracer;
				String traceXml = camelTracer.dumpAllTracedMessagesAsXml();
				List<BacklogTracerEventMessage> traceMessages = getTraceMessagesFromXml(traceXml);
				traceList.addBackLogTraceMessages(traceMessages);
			} else if (tracer instanceof CamelFabricTracerMBean) {
				CamelFabricTracerMBean fabricTracer = (CamelFabricTracerMBean)tracer;
				String traceXml = fabricTracer.dumpAllTracedMessagesAsXml();
				List<BacklogTracerEventMessage> traceMessages = getTraceMessagesFromXml(traceXml);
				traceList.addFabricTraceMessages(traceMessages);
			} else {
				// TODO should we highlight in the UI that there's no tracer
				// enabled?
			}
		} catch (Exception e) {
			CamelJMXPlugin.showUserError("Failed to get tracing messages",
					"Failed to get tracing messages on CamelContext " + this, e);
		}
		return traceList;
	}
	
	private List<BacklogTracerEventMessage> getTraceMessagesFromXml(String xmlDump) {
		BacklogTracerEventMessages backlogTracerEventMessages = new BacklogTracerEventMessageParser().getBacklogTracerEventMessages(xmlDump);
		return backlogTracerEventMessages.getBacklogTracerEventMessages();
	}

	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
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
		if(!isConnectionAvailable()){
			return null;
		}
		try {
			CamelBacklogTracerMBean mbean = getFacade().getCamelTracer(getManagementName());
			if (mbean != null) {
				return mbean;
			} else {
				return getFacade().getFabricTracer(getManagementName());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CamelProcessorMBean getProcessorMBean(String nodeId) {
		return CamelFacades.getProcessorMBean(getFacade(), getManagementName(), nodeId);
	}

	public Object createProcessorBeanView(String routeId, String nodeId) {
		return new ProcessorBeanView(this, routeId, nodeId);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CamelContextNode && obj.hashCode() == hashCode();
	}
	
	@Override
	public int hashCode() {
		return java.util.Objects.hash(getConnection(), camelContextMBean);
	}

	public void dispose() {
		if(launch != null){
			try {
				launch.terminate();
			} catch (DebugException e) {
				CamelJMXPlugin.getLogger().warning("Camel Debug launch cannot be terminated although corresponding JMX connection has been stopped.", e);
			}
		}
		if(camelEditor instanceof CamelEditor){
			((CamelEditor) camelEditor).updatePartName();
		}
	}
	
}

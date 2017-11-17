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
package org.fusesource.ide.launcher.debug.model;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.management.MBeanServerConnection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.fusesource.ide.camel.model.service.core.io.CamelIOHandler;
import org.fusesource.ide.camel.model.service.core.jmx.camel.IBacklogTracerHeader;
import org.fusesource.ide.camel.model.service.core.jmx.camel.ICamelDebuggerMBeanFacade;
import org.fusesource.ide.camel.model.service.core.model.CamelFile;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.foundation.ui.io.CamelXMLEditorInput;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessage;
import org.fusesource.ide.jmx.commons.backlogtracermessage.Header;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.jboss.tools.jmx.core.IConnectionWrapper;

/**
 * Camel Debug Target
 * 
 * @author lhein
 */
public class CamelDebugTarget extends CamelDebugElement implements IDebugTarget {
	
	// associated system process (VM)
	private IProcess fProcess;
	
	// containing launch object
	private ILaunch fLaunch;
	
	// JMX connection info
	private String jmxUri;
	
	// terminated state
	private boolean fTerminated = false;
	
	// processing state
	private boolean fProcessingActive = true;
	
	// program name
	private String fName;
	
	// camel context
	private String camelContextId;
	
	// the currently suspended node's id
	private String suspendedNodeId;
	
	// threads
	private HashMap<String, CamelThread> threads = new HashMap<>(); 
	
	// event dispatcher
	EventDispatchJob dispatcher;
	ThreadGarbageCollector garbageCollector;
	private JMXCamelConnectJob conJob;

	/**
	 * the debugger facade
	 */
	ICamelDebuggerMBeanFacade debugger;

	private IConnectionWrapper jmxConnectionWrapper;
	
	/**
	 * Constructs a new debug target in the given launch for the 
	 * associated Camel VM process.
	 * 
	 * @param launch containing launch
	 * @param process Camel VM
	 * @param jmxUri jmx uri to send requests to the VM
	 * @param jmxUser user name for jmx auth
	 * @param jmxPass password for jmx auth
	 * @exception CoreException if unable to connect to host
	 */
	public CamelDebugTarget(ILaunch launch, IProcess process, String jmxUri, String jmxUser, String jmxPass) throws CoreException {
		super(null);
		this.fLaunch = launch;
		this.fTarget = this;
		this.fProcess = process;
		this.jmxUri = jmxUri;
		
		// retrieve the context id
		initCamelContextId();
		
		// start connector job
		scheduleJobs(jmxUser, jmxPass);
		
		registerAsBreakpointListener();
	}
	
	public CamelDebugTarget(ILaunch launch, IProcess process, IConnectionWrapper jmxConnectionWrapper) throws CoreException {
		super(null);
		this.fLaunch = launch;
		this.fTarget = this;
		this.fProcess = process;
		this.jmxConnectionWrapper = jmxConnectionWrapper;
		
		// retrieve the context id
		initCamelContextId();
		
		// start connector job
		scheduleJobs();
		
		registerAsBreakpointListener();
	}

	private void scheduleJobs() {
		scheduleJMXConnectJob();
		scheduleEventDispatcherJob();
		scheduleGarbageCollectorJob();
	}

	void registerAsBreakpointListener() {
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}

	void scheduleJobs(String jmxUser, String jmxPass) {
		scheduleJMXConnectJob(jmxUser, jmxPass);
		scheduleEventDispatcherJob();
		scheduleGarbageCollectorJob();
	}

	private void scheduleGarbageCollectorJob() {
		garbageCollector = new ThreadGarbageCollector(this);
		garbageCollector.schedule();
	}

	private void scheduleEventDispatcherJob() {
		dispatcher = new EventDispatchJob(this);
		dispatcher.schedule();
	}

	private void scheduleJMXConnectJob() {
		conJob = new JMXCamelConnectJob(this);
		conJob.schedule();
	}
	
	private void scheduleJMXConnectJob(String jmxUser, String jmxPass) {
		conJob = new JMXCamelConnectJob(this, jmxUser, jmxPass);
		conJob.schedule();
	}
	
	/**
	 * retrieves the camel thread for the given id
	 * 
	 * @param uniqueId
	 * @return
	 */
	public CamelThread getThreadForId(String uniqueId) {
		CamelThread t;
		if (threads.containsKey(uniqueId)) {
			t = threads.get(uniqueId);
		} else {
			t = new CamelThread(this, uniqueId);
			threads.put(uniqueId, t);
		}
		return t;
	}
	
	/**
	 * returns the id of the node which is currently suspended
	 * 
	 * @return
	 */
	public String getSuspendedNodeId() {
		return suspendedNodeId;
	}
	
	/**
	 * generates a unique key for the thread
	 * 
	 * @param msg	the message to take data from
	 * @return	a unique key or null if msg was null
	 */
	public String generateKey(BacklogTracerEventMessage msg) {
		if (msg == null) {
			return null;
		}
		final List<Header> headers = msg.getMessage().getHeaders();
		for (IBacklogTracerHeader h : headers) {
			if ("breadcrumbid".equalsIgnoreCase(h.getKey())) {
				// there is a breadcrumbId - use that as key
				return h.getValue();
			}
		}
		// otherwise use the exchange id
		return msg.getExchangeId();
	}
	
	/**
	 * retrieves the camel context id
	 */
	private void initCamelContextId() {
		this.camelContextId = null;
		
		String filePath = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(getLaunch().getLaunchConfiguration());
		if (filePath != null) {
			File f = new File(filePath);
			if (f.exists() && f.isFile() && CamelUtils.isCamelContextFile(filePath)) {
				CamelFile cf = loadCamelModelFromFile(f);
				if (cf != null) {
					this.camelContextId = cf.getRouteContainer().getId();
				}
			}
		}
	}
	
	/**
	 * loads the camel context from the file
	 * 
	 * @param f
	 * @return
	 */
	protected CamelFile loadCamelModelFromFile(File f) {
		// load the model
		try {
			CamelIOHandler ioHandler = new CamelIOHandler();
			return ioHandler.loadCamelModel(f, new NullProgressMonitor());
		} catch (Exception ex) {
			Activator.getLogger().error("Unable to load Camel context file: " + f.getPath(), ex);
		}
		return null;
	}

	
	@Override
	public IProcess getProcess() {
		return fProcess;
	}
	
	@Override
	public CamelThread[] getThreads() throws DebugException {
		return threads.values().toArray(new CamelThread[threads.size()]);
	}
	
	@Override
	public boolean hasThreads() throws DebugException {
		return true; // WTB Changed per bug #138600
	}
	
	@Override
	public String getName() {
		if (fName == null) {
			fName = String.format("Camel Context at %s", getJMXDisplayName());
		}
		return fName;
	}

	private String getJMXDisplayName() {
		if(jmxConnectionWrapper != null){
			return jmxConnectionWrapper.getProvider().getName(jmxConnectionWrapper);
		} else {
			return jmxUri;
		}
	}
	
	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint.getModelIdentifier().equals(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL)) {
			String file = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(getLaunch().getLaunchConfiguration());
			if (file != null) {
				File f = new File(file);
				IMarker marker = breakpoint.getMarker();
				// ensure that the camel file exists - it may not if the project was deleted
				if (marker != null && marker.getResource() != null && f.exists()) {
					return f.getPath().equals(marker.getResource().getLocation().toFile().getPath());
				}
			}
		}
		return false;
	}
	
	@Override
	public IDebugTarget getDebugTarget() {
		return this;
	}
	
	@Override
	public ILaunch getLaunch() {
		return fLaunch;
	}
	
	@Override
	public boolean canTerminate() {
		return getProcess() != null && !isTerminated();
	}
	
	@Override
	public boolean isTerminated() {
		return fTerminated || (getProcess() != null && getProcess().isTerminated());
	}
	
	@Override
	public void terminate() throws DebugException {
		fTerminated = true;
		unregisterBreakpointListener();
		try {
			// abort a possible stuck connect
			if (conJob != null) {
				conJob.cancel();
			}
			if(!isDisconnected()){
				disconnect();
			}
			if (fProcess != null && !fProcess.isTerminated()) {
				fProcess.terminate();
			}
		} finally {
			closeRemoteContextEditor();
			fireTerminateEvent();
			cleanJobs();
		}
	}

	private void cleanJobs() {
		if (conJob != null) {
			conJob.cancel();
		}
		if (dispatcher != null) {
			dispatcher.cancel();
			dispatcher = null;
		}
		if (garbageCollector != null) {
			garbageCollector.cancel();
			garbageCollector = null;			
		}
	}
	
	void unregisterBreakpointListener() {
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
	}
	
	@Override
	public boolean canResume() {
		return !isTerminated() && isSuspended() && !isDisconnected();
	}
	
	@Override
	public boolean canSuspend() {
		return !isTerminated() && !isSuspended() && !isDisconnected();
	}
	
	@Override
	public boolean isSuspended() {
		return !fProcessingActive;
	}
	
	@Override
	public void resume() throws DebugException {
		this.fProcessingActive = true;
		if(dispatcher == null){
			scheduleEventDispatcherJob();
		}
		if(garbageCollector == null){
			scheduleGarbageCollectorJob();
		}
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
	}
	
	@Override
	public void suspend() throws DebugException {
		this.fProcessingActive = false;
		fireSuspendEvent(DebugEvent.CLIENT_REQUEST);
	}
	
	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		try {
			if (supportsBreakpoint(breakpoint) && breakpoint.isEnabled() && !isDisconnected()) {
				String markerType = breakpoint.getMarker().getType();
				if (ICamelDebugConstants.ID_CAMEL_CONDITIONALBREAKPOINT_MARKER_TYPE.equals(markerType)) {
					debugger.addConditionalBreakpoint(CamelDebugUtils.getEndpointNodeId(breakpoint), CamelDebugUtils.getLanguage(breakpoint), CamelDebugUtils.getCondition(breakpoint));
				} else if (ICamelDebugConstants.ID_CAMEL_BREAKPOINT_MARKER_TYPE.equals(markerType)) {
					debugger.addBreakpoint(CamelDebugUtils.getEndpointNodeId(breakpoint));						
				}
			}
		} catch (CoreException e) {
			Activator.getLogger().error("Failed to add breakpoint.", e);
		}
	}
	
	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint) && !isDisconnected()) {
			this.debugger.removeBreakpoint(CamelDebugUtils.getEndpointNodeId(breakpoint));
		}
	}
	
	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled() && isBreakpointManagerEnabled()) {
					breakpointAdded(breakpoint);
				} else if (!breakpoint.isEnabled() || !isBreakpointManagerEnabled()) {
					breakpointRemoved(breakpoint, null);
				}
			} catch (CoreException e) {
				Activator.getLogger().error("Failed to change breakpoint state.", e);
			}
		}
	}

	/**
	 * Check if BreakpointManager is enabled
	 * @return
	 */
	protected boolean isBreakpointManagerEnabled() {
		IBreakpointManager bpManager = DebugPlugin.getDefault().getBreakpointManager();
		return bpManager != null && bpManager.isEnabled();
	}
	
	@Override
	public boolean canDisconnect() {
		return fProcess == null && !isDisconnected();
	}
	
	@Override
	public void disconnect() throws DebugException {
		if (debugger != null) {
			debugger.resumeAll();
			debugger.disableDebugger();
			for (CamelThread t : threads.values()){
				t.terminate();
			}
			debugger = null;
		}
		fireTerminateEvent();
		cleanJobs();
	}
	
	@Override
	public boolean isDisconnected() {
		return debugger == null;
	}
	
	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}
	
	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		return null;
	}

	/**
	 * Notification we have connected to the VM and it has started.
	 * Resume the VM.
	 * 
	 * @param createEditorInput	flag if the editor input should be created
	 */
	public void started(boolean createEditorInput) {
		fireCreationEvent();
		installDeferredBreakpoints();
		if (createEditorInput) {
			createRemoteDebugContextEditorInput();
		}
		try {
			resume();
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/**
	 * resumes all threads
	 */
	public void resumeAllThreads() {
		try {
			for (CamelThread t : threads.values()) {
				t.resume();
			} 
			this.resume();
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/**
	 * Install breakpoints that are already registered with the breakpoint
	 * manager, only if BreakpointManager is enabled
	 */
	private void installDeferredBreakpoints() {
		if (isBreakpointManagerEnabled()) {
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL);
			for (IBreakpoint bp : breakpoints) {
				installDeferredBreakpoint(bp);
			}
		}
	}

	private void installDeferredBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint instanceof CamelEndpointBreakpoint) {
			CamelEndpointBreakpoint ceb = (CamelEndpointBreakpoint) breakpoint;
			// first get the file path from the launch config
			String fileUnderDebug = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(getLaunch().getLaunchConfiguration());
			// then get the project for the file
			IProject p = CamelDebugUtils.getProjectForFilePath(fileUnderDebug);
			// only add breakpoints for if project matches
			if (p.getName().equals(ceb.getProjectName())) {
				breakpointAdded(breakpoint);
			}
		}
	}
	
	/**
	 * returns the debugger facade 
	 * 
	 * @return
	 */
	public ICamelDebuggerMBeanFacade getDebugger() {
		return debugger;
	}
	
	/**
	 * returns a dump of all messages waiting in the node
	 * 
	 * @param endpointNodeId
	 * @return
	 */
	public String getMessagesForNode(String endpointNodeId) {
		return debugger.dumpTracedMessagesAsXml(endpointNodeId);
	}
	
	/**
	 * Notification a breakpoint was encountered. Determine
	 * which breakpoint was hit and fire a suspend event.
	 * 
	 * @param nodeid the id of the node
	 */
	void breakpointHit(String nodeId, BacklogTracerEventMessage msg) {
		boolean bpFound = false;
		String id = generateKey(msg);
		CamelThread t = getThreadForId(id);
		
		// determine which breakpoint was hit, and set the thread's breakpoint
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (supportsBreakpoint(breakpoint) && breakpoint instanceof CamelEndpointBreakpoint) {
				CamelEndpointBreakpoint bp = (CamelEndpointBreakpoint)breakpoint;
				if (bp.getEndpointNodeId().equals(nodeId)) {
					t.setBreakpoints(new IBreakpoint[]{breakpoint});
					t.breakpointHit(nodeId, msg);
					this.suspendedNodeId = nodeId;
					bpFound = true;
					break;
				}
			}
		}
		
		// we process breakpoints reported by runtime even if not set in the UI when
		// the thread is in stepping mode, because in stepping mode the next node will 
		// be always a breakpoint regardless if set in UI or not
		if (!bpFound && getThreadForId(generateKey(msg)).isStepping()) {
			t.setBreakpoints(new IBreakpoint[0]);
			t.breakpointHit(nodeId, msg);
		}
	}
	
	/**
	 * closes the remote context in editor
	 */
	private void closeRemoteContextEditor() {
//		if (getLaunch() == null || getLaunch().getLaunchConfiguration() == null) return;
//		CamelDebugRegistryEntry entry = CamelDebugRegistry.getInstance().getEntry(getLaunch().getLaunchConfiguration());
//		if (entry == null) return;
//		CamelDebugContextEditorInput input = entry.getEditorInput();
//		CamelDebugRegistry.getInstance().removeEntry(getLaunch().getLaunchConfiguration());
//		IWorkbench wb = PlatformUI.getWorkbench();
//		if (wb != null) {
//			IWorkbenchWindow[] wbw = wb.getWorkbenchWindows();
//			if (wbw.length > 0) {
//				final IWorkbenchPage page = wbw[0].getActivePage();
//				if (page != null) {
//					final IEditorPart ep = page.getActiveEditor();
//					if (ep != null && ep.getEditorInput() instanceof CamelDebugContextEditorInput) {
//						CamelDebugContextEditorInput ip = (CamelDebugContextEditorInput)ep.getEditorInput();
//						IFile f = (IFile)input.getAdapter(IFile.class);
//						IFile fIp = (IFile)ip.getAdapter(IFile.class);
//						if (fIp.getFullPath().toFile().getPath().equals(f.getFullPath().toFile().getPath())) {
//							Display.getDefault().asyncExec(new Runnable() {
//								@Override
//								public void run() {
//									page.closeEditor(ep, false);	
//								}
//							});
//						}						
//					}
//				}
//			}
//		}		
	}
	
	/**
	 * creates the remote editor input 
	 * 
	 * @param contextId
	 */
	private void createRemoteDebugContextEditorInput() {
		try {
			// create the editor input
			String fullPath = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(getLaunch().getLaunchConfiguration());
			IFile contextFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fullPath));
			CamelXMLEditorInput input = new CamelXMLEditorInput(contextFile, camelContextId);
			// and save it in the registry
			CamelDebugRegistry.getInstance().createEntry(this, this.camelContextId, input, getLaunch().getLaunchConfiguration());
		} catch (Exception ex) {
			Activator.getLogger().error(ex);
		}
	}

	/**
	 * update the editor input to contain latest changes
	 */
	public void updateEditorInput() {
//		CamelDebugRegistryEntry entry = CamelDebugRegistry.getInstance().getEntry(getLaunch().getLaunchConfiguration());
//		if (entry != null) {
//			IEditorInput input = entry.getEditorInput();
//			input.refresh();
//		}
		
//		closeRemoteContextEditor();
	}
		
	public void setDebugger(ICamelDebuggerMBeanFacade camelDebugFacade) {
		this.debugger = camelDebugFacade;
	}

	public String getCamelContextId() {
		return camelContextId;
	}

	public IConnectionWrapper getJmxConnectionWrapper() {
		return jmxConnectionWrapper;
	}

	public String getJmxUri() {
		return jmxUri;
	}
	
	/** 
	 * Used for test purpose
	 * @return
	 */
	public EventDispatchJob getDispatcher() {
		return dispatcher;
	}
	
	/** 
	 * Used for test purpose
	 * @return
	 */
	public ThreadGarbageCollector getGarbageCollector() {
		return garbageCollector;
}

	public MBeanServerConnection getMBeanConnection() {
		return conJob.getMBeanConnection();
	}
}

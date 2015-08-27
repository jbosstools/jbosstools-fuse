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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.camel.model.RouteContainer;
import org.fusesource.ide.camel.model.io.XmlContainerMarshaller;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.foundation.core.util.CamelUtils;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.exchange.BacklogTracerEventMessage;
import org.fusesource.ide.launcher.debug.model.exchange.Header;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistry;
import org.fusesource.ide.launcher.debug.util.CamelDebugRegistryEntry;
import org.fusesource.ide.launcher.debug.util.CamelDebugUtils;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;
import org.fusesource.ide.launcher.util.CamelDebugContextEditorInput;

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
	private String jmxUser;
	private String jmxPass;
	
	private JMXServiceURL url;
	private JMXConnector jmxc;
	private MBeanServerConnection mbsc;
	
	// terminated state
	private boolean fTerminated = false;
	
	// processing state
	private boolean fProcessingActive = true;
	
	// program name
	private String fName;
	
	// camel context
	private String camelContextId;
	private String contentType;
	
	// the currently suspended node's id
	private String suspendedNodeId;
	
	// threads
	private HashMap<String, CamelThread> threads = new HashMap<String, CamelThread>(); 
	
	// event dispatcher
	private EventDispatchJob dispatcher;
	private ThreadGarbageCollector garbageCollector;

	/**
	 * the debugger facade
	 */
	private CamelDebugFacade debugger;
	
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
		this.jmxUser = jmxUser;
		this.jmxPass = jmxPass;
		
		// retrieve the context id
		initCamelContextId();
		
		// start connector job
		JMXConnectJob conJob = new JMXConnectJob();
		conJob.schedule();
		
		this.dispatcher = new EventDispatchJob();
		this.dispatcher.schedule();
		
		this.garbageCollector = new ThreadGarbageCollector();
		this.garbageCollector.schedule();
		
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
	}
	
	/**
	 * retrieves the camel thread for the given id
	 * 
	 * @param uniqueId
	 * @return
	 */
	public CamelThread getThreadForId(String uniqueId) {
		CamelThread t = null;
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
		return this.suspendedNodeId;
	}
	
	/**
	 * generates a unique key for the thread
	 * 
	 * @param msg	the message to take data from
	 * @return	a unique key or null if msg was null
	 */
	public String generateKey(BacklogTracerEventMessage msg) {
		if (msg == null) return null;
		if (msg.getMessage().getHeaders().size()>0) {
			for (Header h : msg.getMessage().getHeaders()) {
				if (h.getKey().equalsIgnoreCase("breadcrumbid")) {
					// there is a breadcrumbId - use that as key
					return h.getValue();
				}
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
				XmlContainerMarshaller m = new XmlContainerMarshaller();
				RouteContainer c = m.loadRoutes(f);	
				if (c != null) {
					this.camelContextId = c.getCamelContextId();
				}
				if (CamelUtils.isBlueprintFile(filePath)) {
					this.contentType = ICamelDebugConstants.CAMEL_CONTEXT_CONTENT_TYPE_BLUEPRINT;
				} else if (CamelUtils.isSpringFile(filePath)) {
					this.contentType = ICamelDebugConstants.CAMEL_CONTEXT_CONTENT_TYPE_SPRING;
				} 
			}
		}
	}
	
	/**
	 * establish JMX connection to process
	 * 
	 * @return
	 */
	private boolean connectToVM() {
		try {
			if (this.url == null) this.url = new JMXServiceURL(this.jmxUri); 
			if (!Strings.isBlank(this.jmxUser)) {
				// credentials defined - so use them
				Map<String, Object> envMap = new HashMap<String, Object>();
				envMap.put("jmx.remote.credentials", new String[] { this.jmxUser, this.jmxPass });
				this.jmxc = JMXConnectorFactory.connect(this.url, envMap); 
			} else {
				// no need for using credentials if no user is defined
				this.jmxc = JMXConnectorFactory.connect(this.url); 
			}
			this.mbsc = this.jmxc.getMBeanServerConnection(); 	
			return true;
		} catch (IOException ex) {
			//ignore
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getProcess()
	 */
	public IProcess getProcess() {
		return fProcess;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getThreads()
	 */
	public IThread[] getThreads() throws DebugException {
		return threads.values().toArray(new IThread[threads.size()]);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#hasThreads()
	 */
	public boolean hasThreads() throws DebugException {
		return true; // WTB Changed per bug #138600
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#getName()
	 */
	public String getName() throws DebugException {
		if (fName == null) {
			fName = String.format("Camel Context at %s", jmxUri);
		}
		return fName;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugTarget#supportsBreakpoint(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint.getModelIdentifier().equals(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL)) {
			String file = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(getLaunch().getLaunchConfiguration());
			if (file != null) {
				File f = new File(file);
				IMarker marker = breakpoint.getMarker();
				if (marker != null && marker.getResource() != null) {
					return f.getPath().equals(marker.getResource().getLocation().toFile().getPath());
				}
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return this;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return fLaunch;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return getProcess() != null && getProcess().canTerminate();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return fTerminated || (getProcess() != null && getProcess().isTerminated());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		fTerminated = true;
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
		try {
			if (fProcess != null && !fProcess.isTerminated()) {
				disconnect();
				fProcess.terminate();
			}
		} catch (DebugException ex) {
//			Activator.getLogger().error(ex);
		}
		closeRemoteContextEditor();
		fireTerminateEvent();
		// clean up the jobs
		this.dispatcher.cancel();
		this.garbageCollector.cancel();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return !isTerminated() && isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return !isTerminated() && !isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return this.fProcessingActive == false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		this.fProcessingActive = true;
		fireResumeEvent(DebugEvent.CLIENT_REQUEST);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		this.fProcessingActive = false;
		fireSuspendEvent(DebugEvent.CLIENT_REQUEST);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointAdded(org.eclipse.debug.core.model.IBreakpoint)
	 */
	public void breakpointAdded(IBreakpoint breakpoint) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled() && this.debugger != null) {
					if (breakpoint.getMarker().getType().equals(ICamelDebugConstants.ID_CAMEL_CONDITIONALBREAKPOINT_MARKER_TYPE)) {
						this.debugger.addConditionalBreakpoint(CamelDebugUtils.getEndpointNodeId(breakpoint), CamelDebugUtils.getLanguage(breakpoint), CamelDebugUtils.getCondition(breakpoint));
					} else if (breakpoint.getMarker().getType().equals(ICamelDebugConstants.ID_CAMEL_BREAKPOINT_MARKER_TYPE)) {
						this.debugger.addBreakpoint(CamelDebugUtils.getEndpointNodeId(breakpoint));						
					}
				}
			} catch (CoreException e) {
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointRemoved(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			this.debugger.removeBreakpoint(CamelDebugUtils.getEndpointNodeId(breakpoint));
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.IBreakpointListener#breakpointChanged(org.eclipse.debug.core.model.IBreakpoint, org.eclipse.core.resources.IMarkerDelta)
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if (supportsBreakpoint(breakpoint)) {
			try {
				if (breakpoint.isEnabled()) {
					breakpointAdded(breakpoint);
				} else {
					breakpointRemoved(breakpoint, null);
				}
			} catch (CoreException e) {
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#canDisconnect()
	 */
	public boolean canDisconnect() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#disconnect()
	 */
	public void disconnect() throws DebugException {
		if (this.debugger != null) {
			this.debugger.disableDebugger();
			this.debugger.resumeAll();
			for (CamelThread t : threads.values()) t.terminate();
			this.debugger = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IDisconnect#isDisconnected()
	 */
	public boolean isDisconnected() {
		return this.debugger == null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#supportsStorageRetrieval()
	 */
	public boolean supportsStorageRetrieval() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IMemoryBlockRetrieval#getMemoryBlock(long, long)
	 */
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
	 * manager.
	 */
	private void installDeferredBreakpoints() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL);
		for (IBreakpoint bp : breakpoints) {
			if (bp instanceof CamelEndpointBreakpoint) {
				CamelEndpointBreakpoint ceb = (CamelEndpointBreakpoint)bp;
				// first get the file path from the launch config
				String fileUnderDebug = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(getLaunch().getLaunchConfiguration());
				// then get the project for the file
				IProject p = CamelDebugUtils.getProjectForFilePath(fileUnderDebug);
				// only add breakpoints for if project matches
				if (p.getName().equals(ceb.getProjectName())) {
					breakpointAdded(bp);	
				}				
			}
		}
	}
	
	/**
	 * returns the debugger facade 
	 * 
	 * @return
	 */
	public CamelDebugFacade getDebugger() {
		return this.debugger;
	}
	
	/**
	 * returns a dump of all messages waiting in the node
	 * 
	 * @param endpointNodeId
	 * @return
	 */
	public String getMessagesForNode(String endpointNodeId) {
		return this.debugger.dumpTracedMessagesAsXml(endpointNodeId);
	}
	
	/**
	 * Notification a breakpoint was encountered. Determine
	 * which breakpoint was hit and fire a suspend event.
	 * 
	 * @param nodeid the id of the node
	 */
	private void breakpointHit(String nodeId, BacklogTracerEventMessage msg) {
		boolean bpFound = false;
		String id = generateKey(msg);
		CamelThread t = getThreadForId(id);
		
		// determine which breakpoint was hit, and set the thread's breakpoint
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL);
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			if (supportsBreakpoint(breakpoint)) {
				if (breakpoint instanceof CamelEndpointBreakpoint) {
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
		if (getLaunch() == null || getLaunch().getLaunchConfiguration() == null) return;
		CamelDebugRegistryEntry entry = CamelDebugRegistry.getInstance().getEntry(getLaunch().getLaunchConfiguration());
		if (entry == null) return;
		CamelDebugContextEditorInput input = entry.getEditorInput();
		CamelDebugRegistry.getInstance().removeEntry(getLaunch().getLaunchConfiguration());
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow[] wbw = wb.getWorkbenchWindows();
			if (wbw.length > 0) {
				final IWorkbenchPage page = wbw[0].getActivePage();
				if (page != null) {
					final IEditorPart ep = page.getActiveEditor();
					if (ep != null && ep.getEditorInput() instanceof CamelDebugContextEditorInput) {
						CamelDebugContextEditorInput ip = (CamelDebugContextEditorInput)ep.getEditorInput();
						IFile f = (IFile)input.getAdapter(IFile.class);
						IFile fIp = (IFile)ip.getAdapter(IFile.class);
						if (fIp.getFullPath().toFile().getPath().equals(f.getFullPath().toFile().getPath())) {
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									page.closeEditor(ep, false);	
								}
							});
						}						
					}
				}
			}
		}		
	}
	
	/**
	 * creates the remote editor input 
	 * 
	 * @param contextId
	 */
	private void createRemoteDebugContextEditorInput() {
		try {
			// create the editor input
			CamelDebugContextEditorInput input = new CamelDebugContextEditorInput(debugger, getLaunch().getLaunchConfiguration());
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
		CamelDebugRegistryEntry entry = CamelDebugRegistry.getInstance().getEntry(getLaunch().getLaunchConfiguration());
		if (entry != null) {
			CamelDebugContextEditorInput input = entry.getEditorInput();
			input.refresh();
		}
		
//		closeRemoteContextEditor();
	}
	
	/**
	 * Listens to events from the CAMEL VM and fires corresponding 
	 * debug events.
	 */
	class JMXConnectJob extends Job {
		
		private static final long CONNECTION_TIMEOUT = 60 * 1000; // 60 seconds timeout
		
		public JMXConnectJob() {
			super("Connect to Camel Debugger...");
			setSystem(false);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Connect to Camel VM...", 1);
			
			final long startTime = System.currentTimeMillis();
			boolean connected = false;
			
			// run until connected or timed out
			while (!connected && System.currentTimeMillis()-startTime <= CONNECTION_TIMEOUT && !monitor.isCanceled()) {
				try {
					if (connectToVM()) {
						// connected to the camel vm
						CamelDebugTarget.this.debugger = new CamelDebugFacade(CamelDebugTarget.this, mbsc, camelContextId, contentType);
						connected = true;
						started(true);
						if (CamelDebugTarget.this.debugger.isEnabled() == false) CamelDebugTarget.this.debugger.enableDebugger();
					}
				} catch (Exception ex) {
					Activator.getLogger().error(ex);
					// runtime not yet up...wait a bit and retry
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						monitor.setCanceled(true);
					}
				}
			}
			
			if (!connected) {
				try {
					abort("Unable to connect to Camel VM", new Exception("Unable to connect to Camel Debugger"));
				} catch (DebugException ex) {
					Activator.getLogger().error(ex);
				}
			}
			
			monitor.done();
			
			return connected == true ? Status.OK_STATUS : Status.CANCEL_STATUS;
		}
	}
	
	/**
	 * Listens to events from the CAMEL VM and fires corresponding 
	 * debug events.
	 */
	class EventDispatchJob extends Job {
		
		public EventDispatchJob() {
			super("Camel Debug Event Dispatch");
			setSystem(true);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			while (!isTerminated() && !monitor.isCanceled()) {
				// check for suspended breakpoints
				if (CamelDebugTarget.this.debugger != null && !isSuspended()) {
					try {
						Set<String> suspendedBreakpoints = CamelDebugTarget.this.debugger.getSuspendedBreakpointNodeIds();
						if (suspendedBreakpoints != null && !suspendedBreakpoints.isEmpty()) {
							// we need to suspend the debug target
							suspend();
							
							for (String nodeId : suspendedBreakpoints) {
								BacklogTracerEventMessage evMsg = CamelDebugUtils.getBacklogTracerEventMessage(getMessagesForNode(nodeId));
								String id = generateKey(evMsg);
								CamelThread t = getThreadForId(id);
									
								// now we can access the stack frames
								String endpointId = t.getTopStackFrame() != null ? ((CamelStackFrame)t.getTopStackFrame()).getEndpointId() : null;
								if (nodeId.equals(endpointId)) {
									// its the same breakpoint we already hit for that exchange - ignore it
									continue;
								}
								
								if (!t.isSuspended()) {
									// process the breakpoint
									breakpointHit(nodeId, evMsg);
									
									// now resume
									resume();
								}
							}
						}
						// wait a bit to keep jmx traffic lower - if we are too fast sometimes a breakpoint is
						// hit but no message dump is available yet -> bad coding on camel side?
						Thread.sleep(2000);
					} catch (Exception ex) {
						Activator.getLogger().error(ex);
					}
				}
			}
			return Status.OK_STATUS;
		}
	}
	
	class ThreadGarbageCollector extends Job {
		
		private final long THREAD_LIFE_DURATION = 5*30*1000; // 5 minutes
		
		public ThreadGarbageCollector() {
			super("Thread CleanUp Service");
			setSystem(true);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			while (!isTerminated() && !monitor.isCanceled()) {
				try {
					// check all threads
					for (CamelThread t : threads.values()) {
						// we clean all threads not suspended in the last x seconds and state running
						if (t.isSuspended() == false && (System.currentTimeMillis() - t.getLastSuspended()) > THREAD_LIFE_DURATION) {
							t.terminate();
						}
					}
					Thread.sleep(60000); // run every 60 secs
				} catch (InterruptedException ex) {
					Activator.getLogger().error(ex);
				} catch (DebugException de) {
					Activator.getLogger().error(de);
				}
			}
			return Status.OK_STATUS;
		}
	}
}

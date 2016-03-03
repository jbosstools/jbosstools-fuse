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
package org.fusesource.ide.camel.model.service.core.debug;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.fusesource.ide.camel.model.service.core.debug.model.CamelEndpointBreakpoint;
import org.fusesource.ide.camel.model.service.core.debug.model.exchange.BacklogTracerEventMessage;
import org.fusesource.ide.camel.model.service.core.debug.util.CamelDebugUtils;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

/**
 * A Camel VM thread. A Camel VM is single threaded.
 * 
 * @author lhein
 */
public class CamelThread extends CamelDebugElement implements IThread {
	/**
	 * Breakpoints this thread is suspended at or <code>null</code>
	 * if none.
	 */
	private IBreakpoint[] fBreakpoints;
	
	/**
	 * holds all stackframes
	 */
	private ArrayList<CamelStackFrame> stackFrames = new ArrayList<CamelStackFrame>();
	
	/**
	 * Whether this thread is stepping
	 */
	private boolean fStepping = false;
	
	/**
	 * the name of the thread
	 */
	private String name;
	
	private boolean suspended = false;
	
	private boolean terminated = false;
	
	private long lastSuspended = -1;
	
	private String uniqueId;
	
	/**
	 * Constructs a new thread for the given target
	 * 
	 * @param target VM
	 * @param uniqueId a unique id
	 */
	public CamelThread(CamelDebugTarget target, String uniqueId) {
		super(target);
		this.uniqueId = uniqueId;
		fireCreationEvent();	
		setName(this.uniqueId);
	}
	
	/**
	 * called upon suspend of the thread to generate the new stackframe
	 */
	public void breakpointHit(String nodeId, BacklogTracerEventMessage msg) {
		File f = null;
		try {
			String file = CamelDebugUtils.getRawCamelContextFilePathFromLaunchConfig(getLaunch().getLaunchConfiguration());
			if (file != null) {
				f = new File(file);
			}
			
			CamelStackFrame lastStack = this.stackFrames.isEmpty() ? null : this.stackFrames.get(0);
			CamelStackFrame newStack  = new CamelStackFrame(this, nodeId, (msg.getExchangeId() + "@" + nodeId + "@" + msg.getRouteId()).hashCode(), f, msg);
			
			// mark changed variable values
			if (lastStack != null) {
				newStack.updateChangedFieldsFromLastStack(lastStack);
			}
			
			// add stack frame to the list of stack frames
			stackFrames.add(0, newStack);
		} catch (Exception ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		} finally {
			// first we suspend
			try {
				suspend();
			} catch (DebugException ex) {
				CamelModelServiceCoreActivator.pluginLog().logError(ex);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getStackFrames()
	 */
	public IStackFrame[] getStackFrames() throws DebugException {
		if (isSuspended()) {
			return this.stackFrames.toArray(new IStackFrame[this.stackFrames.size()]);
		} else {
			return new IStackFrame[0];
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#hasStackFrames()
	 */
	public boolean hasStackFrames() throws DebugException {
		return isSuspended() && this.stackFrames.size()>0;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getPriority()
	 */
	public int getPriority() throws DebugException {
		return Thread.NORM_PRIORITY;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getTopStackFrame()
	 */
	public IStackFrame getTopStackFrame() throws DebugException {
		if (this.stackFrames.size() > 0) {
			return stackFrames.get(0);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getName()
	 */
	public String getName() throws DebugException {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
		fireChangeEvent(DebugEvent.STATE);	// only name change - no need to update children
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IThread#getBreakpoints()
	 */
	public IBreakpoint[] getBreakpoints() {
		if (fBreakpoints == null) {
			return new CamelEndpointBreakpoint[0];
		}
		return fBreakpoints;
	}
	
	/**
	 * @return the lastSuspended
	 */
	public long getLastSuspended() {
		return this.lastSuspended;
	}
	
	/**
	 * Sets the breakpoints this thread is suspended at, or <code>null</code>
	 * if none.
	 * 
	 * @param breakpoints the breakpoints this thread is suspended at, or <code>null</code>
	 * if none
	 */
	protected void setBreakpoints(IBreakpoint[] breakpoints) {
		fBreakpoints = breakpoints;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
	 */
	public boolean canResume() {
		return isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
	 */
	public boolean canSuspend() {
		return !isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
	 */
	public boolean isSuspended() {
		return this.suspended;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
	 */
	public void resume() throws DebugException {
		if (this.fStepping == false) {
			// normal resume without stepping
			((CamelDebugTarget)getDebugTarget()).getDebugger().resumeBreakpoint(((CamelStackFrame)getTopStackFrame()).getEndpointId());	
			fireResumeEvent(DebugEvent.CLIENT_REQUEST);
		} else {
			// step over resume
			fireResumeEvent(DebugEvent.STEP_OVER);
		}
		this.suspended = false;
		this.lastSuspended = System.currentTimeMillis();
		
		// no contents in resumed state - inform UI
		fireChangeEvent(DebugEvent.CONTENT);
		
		getDebugTarget().resume();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
	 */
	public void suspend() throws DebugException {
		this.suspended = true;
		
		// we have new children on suspend, so inform UI
		fireChangeEvent(DebugEvent.CONTENT);

		// 2 possible reasons for suspend - a) step over ended or b) breakpoint hit
		if (this.fStepping == true) {
			// suspended because step over is finished
			fireSuspendEvent(DebugEvent.STEP_END);
			fStepping = false;
		} else {
			// suspended because breakpoint hit
			fireSuspendEvent(DebugEvent.BREAKPOINT);
		}
		fireChangeEvent(DebugEvent.STATE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepInto()
	 */
	public boolean canStepInto() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepOver()
	 */
	public boolean canStepOver() {
		return isSuspended();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
	 */
	public boolean canStepReturn() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#isStepping()
	 */
	public boolean isStepping() {
		return fStepping;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepInto()
	 */
	public void stepInto() throws DebugException {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepOver()
	 */
	public void stepOver() throws DebugException {
		CamelDebugFacade debugger = ((CamelDebugTarget)getDebugTarget()).getDebugger();
		String endpointId = ((CamelStackFrame)getTopStackFrame()).getEndpointId();
		try {
			// mark this thread as in stepping mode
			this.fStepping = true;
			
			// if not yet in single step mode 
			if (!debugger.isSingleStepMode()) {
				// change to single step mode and step
				debugger.stepBreakpoint(endpointId);	
			} else {
				// otherwise do step only
				debugger.step();
			}
		} finally {
			resume();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IStep#stepReturn()
	 */
	public void stepReturn() throws DebugException {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return !isTerminated();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return this.terminated;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		this.terminated = true;
		fireTerminateEvent();	
	}
}

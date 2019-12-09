/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.camel.model.service.core.jmx.camel.ICamelDebuggerMBeanFacade;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessage;
import org.fusesource.ide.jmx.commons.backlogtracermessage.Message;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.values.BaseCamelValue;
import org.fusesource.ide.launcher.debug.model.values.CamelDebuggerValue;
import org.fusesource.ide.launcher.debug.model.values.CamelExchangeValue;
import org.fusesource.ide.launcher.debug.model.values.CamelMessageValue;
import org.fusesource.ide.launcher.debug.model.values.CamelProcessorValue;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelDebuggerVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelExchangeVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelMessageVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelProcessorVariable;
import org.fusesource.ide.launcher.debug.model.variables.IVariableConstants;

/**
 * Camel Stack Frame
 * 
 * @author lhein
 */
public class CamelStackFrame extends CamelDebugElement implements IStackFrame, IVariableConstants {
	
	private CamelThread fThread;
	private CamelDebugTarget debugTarget;
	private int fId;
	private ArrayList<IVariable> fVariables = new ArrayList<>();
	private String data;
	private File contextFile;
	private BacklogTracerEventMessage backlogTracerEventMessage;
	
	/**
	 * Constructs a stack frame in the given thread with the given
	 * frame data.
	 * 
	 * @param thread
	 * @param data frame data
	 * @param id stack frame id (0 is the bottom of the stack)
	 * @param f	the camel context file
	 * @param xmlDump the message xml dump for that stackframe
	 */
	public CamelStackFrame(CamelThread thread, String data, int id, File f, BacklogTracerEventMessage msg) {
		super((CamelDebugTarget) thread.getDebugTarget());
		this.data = data;
		this.fId = id;
		this.contextFile = f;
		this.debugTarget = (CamelDebugTarget) thread.getDebugTarget();
		this.fThread = thread;
		this.backlogTracerEventMessage = msg;
		try {
			initVariables();
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/**
	 * Initializes this frame based on its data
	 */
	private void initVariables() throws DebugException {
		BaseCamelVariable var = null;
		BaseCamelValue val = null;
		
		// DEBUGGER - CAMEL
		var = new CamelDebuggerVariable(this.debugTarget, VARIABLE_NAME_DEBUGGER, String.class);
		val = new CamelDebuggerValue(this.fTarget, this, var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// CURRENT ENDPOINT
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_ENDPOINT, String.class);
		val = new BaseCamelValue(this.fTarget, getEndpointId(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// PROCESSOR
		var = new CamelProcessorVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR, String.class);
		val = new CamelProcessorValue(this.fTarget, this , getEndpointId(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
				
		// EXCHANGE
		var = new CamelExchangeVariable(this.debugTarget, VARIABLE_NAME_EXCHANGE, String.class);
		val = new CamelExchangeValue(this.fTarget, this.backlogTracerEventMessage, var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// MESSAGE
		var = new CamelMessageVariable(this.debugTarget, VARIABLE_NAME_MESSAGE, Message.class);
		val = new CamelMessageValue(this.fTarget, this.backlogTracerEventMessage.getMessage(), var.getReferenceType(), (CamelMessageVariable)var);
		var.setValue(val);
		this.fVariables.add(var);
	}
	
	/**
	 * marks those variables as changed which are changed in comparison
	 * to the last stackframe
	 * 
	 * @param lastStackFrame	the last stack frame
	 */
	public void updateChangedFieldsFromLastStack(CamelStackFrame lastStackFrame) throws DebugException {
		updateChangedFields(lastStackFrame.getVariables(), getVariables());
	}
	
	/**
	 * compares 2 sets of variable's values and marks changes to the new variables
	 * 
	 * @param oldVariables
	 * @param newVariables
	 * @throws DebugException
	 */
	private void updateChangedFields(IVariable[] oldVariables, IVariable[] newVariables ) throws DebugException {
		for (IVariable nV : newVariables) {
			BaseCamelVariable newVar = (BaseCamelVariable)nV;
			for (IVariable oldVar : oldVariables) {
				if (newVar.getName().equals(oldVar.getName())) {
					IValue oldValue = oldVar.getValue();
					IValue newValue = newVar.getValue();
					// first check only values
					if (!oldValue.getValueString().equals(newValue.getValueString())) {
						newVar.markChanged();
					}
					// also check for changed nested variables
					if (newValue.hasVariables() && oldValue.hasVariables()) {
						updateChangedFields(oldValue.getVariables(), newValue.getVariables());
					}
				}
			}
		}
	}
	
	public String getEndpointId() {
		return this.data;
	}
	
	@Override
	public IThread getThread() {
		return fThread;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		return fVariables.toArray(new IVariable[this.fVariables.size()]);
	}
	
	@Override
	public boolean hasVariables() throws DebugException {
		return !fVariables.isEmpty();
	}
	
	@Override
	public int getLineNumber() throws DebugException {
		return -1;
	}
	
	@Override
	public int getCharStart() throws DebugException {
		return -1;
	}
	
	@Override
	public int getCharEnd() throws DebugException {
		return -1;
	}
	
	@Override
	public String getName() throws DebugException {
		if (this.backlogTracerEventMessage != null) {
			return String.format("%s in %s [%s]", this.backlogTracerEventMessage.getToNode(), this.backlogTracerEventMessage.getRouteId(), this.contextFile != null ? contextFile.getName() : "unknown");
		}
		return this.contextFile != null ? contextFile.getName() + ": " + data : data;
	}
	
	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	}
	
	@Override
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}
	
	@Override
	public boolean canStepInto() {
		return getThread().canStepInto();
	}
	
	@Override
	public boolean canStepOver() {
		return getThread().canStepOver();
	}
	
	@Override
	public boolean canStepReturn() {
		return getThread().canStepReturn();
	}
	
	@Override
	public boolean isStepping() {
		return getThread().isStepping();
	}
	
	@Override
	public void stepInto() throws DebugException {
		getThread().stepInto();
	}
	
	@Override
	public void stepOver() throws DebugException {
		getThread().stepOver();
	}
	
	@Override
	public void stepReturn() throws DebugException {
		getThread().stepReturn();
	}
	
	@Override
	public boolean canResume() {
		return getThread().canResume();
	}
	
	@Override
	public boolean canSuspend() {
		return getThread().canSuspend();
	}
	
	@Override
	public boolean isSuspended() {
		return getThread().isSuspended();
	}
	
	@Override
	public void resume() throws DebugException {
		getThread().resume();
	}
	
	@Override
	public void suspend() throws DebugException {
		getThread().suspend();
	}
	
	@Override
	public boolean canTerminate() {
		return getThread().canTerminate();
	}
	
	@Override
	public boolean isTerminated() {
		return getThread().isTerminated();
	}
	
	@Override
	public void terminate() throws DebugException {
		getThread().terminate();
	}
	
	/**
	 * @return the backlogTracerEventMessage
	 */
	public BacklogTracerEventMessage getBacklogTracerEventMessage() {
		return this.backlogTracerEventMessage;
	}
	
	/**
	 * Returns this stack frame's unique identifier within its thread
	 * 
	 * @return this stack frame's unique identifier within its thread
	 */
	protected int getIdentifier() {
		return fId;
	}
	
	/**
	 * @return the contextFile
	 */
	public File getContextFile() {
		return this.contextFile;
	}
	
	public String getSource() {
		return contextFile != null ? contextFile.getName() : null;
	}
	
	public ICamelDebuggerMBeanFacade getDebugger() {
		return ((CamelDebugTarget)getDebugTarget()).getDebugger();
	}
}

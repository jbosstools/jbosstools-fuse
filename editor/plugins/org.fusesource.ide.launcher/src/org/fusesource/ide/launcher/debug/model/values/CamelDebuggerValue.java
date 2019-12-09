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
package org.fusesource.ide.launcher.debug.model.values;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.CamelStackFrame;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;

/**
 * @author lhein
 */
public class CamelDebuggerValue extends BaseCamelValue {
	
	private List<IVariable> fVariables = new ArrayList<>();
	private CamelDebugTarget debugTarget;
	private CamelStackFrame stackFrame; 
	
	/**
	 * creates a value
	 * 
	 * @param target
	 * @param stackFrame
	 * @param type 
	 */
	public CamelDebuggerValue(CamelDebugTarget target, CamelStackFrame stackFrame, Class<?> type) {
		super(target, target != null ? Integer.toString(target.hashCode()) : "", type);
		this.debugTarget = target;
		this.stackFrame = stackFrame;
		try {
			init();
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/**
	 * initialize variables
	 */
	private void init() throws DebugException {
		fillFVariables(VARIABLE_NAME_BODYMAXCHARS, Integer.toString(getBodyMaxChars()));
		fillFVariables(VARIABLE_NAME_BODYINCLUDEFILES, Boolean.toString(isBodyIncludeFiles()));
		fillFVariables(VARIABLE_NAME_BODYINCLUDESTREAMS, Boolean.toString(isBodyIncludeStreams()));
		fillFVariables(VARIABLE_NAME_DEBUGCOUNTER, Long.toString(getDebugCounter()));
		fillFVariables(VARIABLE_NAME_LOGLEVEL, getLogLevel());
	}

	/**
	 * @param variableNameProcessorId
	 * @param valueString
	 * @throws DebugException
	 */
	private void fillFVariables(String variableNameProcessorId, String valueString) throws DebugException {
		BaseCamelVariable var = new BaseCamelVariable(this.debugTarget, variableNameProcessorId, String.class);
		BaseCamelValue val = new BaseCamelValue(this.fTarget, valueString, var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
	}
	
	@Override
	public boolean hasVariables() throws DebugException {
		return !this.fVariables.isEmpty();
	}
	
	@Override
	public IVariable[] getVariables() throws DebugException {
		return this.fVariables.toArray(new IVariable[this.fVariables.size()]);
	}
	
	@Override
	protected String getVariableDisplayString() {
		return "CamelDebuggerSettings";
	}
	
	private String getLogLevel() {
		return this.stackFrame.getDebugger().getLoggingLevel();
	}
	
	private long getDebugCounter() {
		return this.stackFrame.getDebugger().getDebugCounter();
	}
	
	private int getBodyMaxChars() {
		return this.stackFrame.getDebugger().getBodyMaxChars();
	}
	
	private boolean isBodyIncludeFiles() {
		return this.stackFrame.getDebugger().isBodyIncludeFiles();
	}
	
	private boolean isBodyIncludeStreams() {
		return this.stackFrame.getDebugger().isBodyIncludeStreams();
	}
}

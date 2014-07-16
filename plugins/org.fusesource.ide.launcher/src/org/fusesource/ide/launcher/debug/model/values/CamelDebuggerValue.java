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
package org.fusesource.ide.launcher.debug.model.values;

import java.util.ArrayList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.CamelStackFrame;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelBodyIncludeFilesVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelBodyIncludeStreamsVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelBodyMaxCharsVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelLogLevelVariable;

/**
 * @author lhein
 */
public class CamelDebuggerValue extends BaseCamelValue {
	
	private ArrayList<IVariable> fVariables = new ArrayList<IVariable>();
	private CamelDebugTarget debugTarget;
	private CamelStackFrame stackFrame; 
	
	/**
	 * creates a value
	 * 
	 * @param target
	 * @param stackFrame
	 * @param type 
	 */
	public CamelDebuggerValue(CamelDebugTarget target, CamelStackFrame stackFrame, Class type) {
		super(target, "" + target.hashCode(), type);
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
		BaseCamelVariable var = null;
		BaseCamelValue val = null;
		
		// BODY MAX CHARS
		var = new CamelBodyMaxCharsVariable(this.debugTarget, VARIABLE_NAME_BODYMAXCHARS, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getBodyMaxChars(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);

		// BODY INCLUDE FILES
		var = new CamelBodyIncludeFilesVariable(this.debugTarget, VARIABLE_NAME_BODYINCLUDEFILES, String.class);
		val = new BaseCamelValue(this.fTarget, "" + isBodyIncludeFiles(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// BODY INCLUDE STREAMS
		var = new CamelBodyIncludeStreamsVariable(this.debugTarget, VARIABLE_NAME_BODYINCLUDESTREAMS, String.class);
		val = new BaseCamelValue(this.fTarget, "" + isBodyIncludeStreams(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);

		// DEBUG COUNTER
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_DEBUGCOUNTER, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getDebugCounter(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// LOG LEVEL
		var = new CamelLogLevelVariable(this.debugTarget, VARIABLE_NAME_LOGLEVEL, String.class);
		val = new BaseCamelValue(this.fTarget, getLogLevel(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.values.BaseCamelValue#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		return this.fVariables.size()>0;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.values.BaseCamelValue#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {
		return this.fVariables.toArray(new IVariable[this.fVariables.size()]);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.values.BaseCamelValue#getVariableDisplayString()
	 */
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

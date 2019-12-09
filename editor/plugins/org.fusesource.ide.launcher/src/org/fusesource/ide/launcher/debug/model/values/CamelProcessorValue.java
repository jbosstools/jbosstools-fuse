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
 *
 */
public class CamelProcessorValue extends BaseCamelValue {
	
	private List<IVariable> fVariables = new ArrayList<>();
	private CamelDebugTarget debugTarget;
	private CamelStackFrame stackFrame; 
	private String processorId;
	
	/**
	 * creates a value
	 * 
	 * @param target
	 * @param stackFrame
	 * @param value
	 * @param type 
	 */
	public CamelProcessorValue(CamelDebugTarget target, CamelStackFrame stackFrame, String value, Class<?> type) {
		super(target, value, type);
		this.debugTarget = target;
		this.stackFrame = stackFrame;
		this.processorId = value;
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
		fillFVariables(VARIABLE_NAME_PROCESSOR_ID, getValueString());
		fillFVariables(VARIABLE_NAME_PROCESSOR_ROUTE_ID, getRouteId());
		fillFVariables(VARIABLE_NAME_PROCESSOR_CAMEL_ID, getCamelId());
		fillFVariables(VARIABLE_NAME_PROCESSOR_EXCHANGES_COMPLETED, Long.toString(getExchangesCompleted()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_EXCHANGES_FAILED, Long.toString(getExchangesFailed()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_EXCHANGES_TOTAL, Long.toString(getTotalExchanges()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_REDELIVERIES, Long.toString(getRedeliveries()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_EXTERNAL_REDELIVERIES, Long.toString(getExternalRedeliveries()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_FAILURES_HANDLED, Long.toString(getFailuresHandled()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_LAST_PROCESSING_TIME, Long.toString(getLastProcessingTime()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_MIN_PROCESSING_TIME, Long.toString(getMinProcessingTime()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_AVG_PROCESSING_TIME, Long.toString(getMeanProcessingTime()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_MAX_PROCESSING_TIME, Long.toString(getMaxProcessingTime()));
		fillFVariables(VARIABLE_NAME_PROCESSOR_TOTAL_PROCESSING_TIME, Long.toString(getTotalProcessingTime()));
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

	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.values.BaseCamelValue#hasVariables()
	 */
	@Override
	public boolean hasVariables() throws DebugException {
		return !this.fVariables.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.values.BaseCamelValue#getVariables()
	 */
	@Override
	public IVariable[] getVariables() throws DebugException {
		return this.fVariables.toArray(new IVariable[this.fVariables.size()]);
	}
	
	private String getCamelId() {
		return this.stackFrame.getDebugger().getCamelId(this.processorId);
	}
	
	private String getRouteId() {
		return this.stackFrame.getDebugger().getRouteId(this.processorId);
	}
	
	private long getExchangesCompleted() {
		return this.stackFrame.getDebugger().getCompletedExchanges(this.processorId);
	}
	
	private long getExchangesFailed() {
		return this.stackFrame.getDebugger().getFailedExchanges(this.processorId);
	}
	
	private long getTotalExchanges() {
		return this.stackFrame.getDebugger().getTotalExchanges(this.processorId);
	}
	
	private long getExternalRedeliveries() {
		return this.stackFrame.getDebugger().getExternalRedeliveries(this.processorId);
	}
	
	private long getFailuresHandled() {
		return this.stackFrame.getDebugger().getHandledFailures(this.processorId);
	}
	
	private long getRedeliveries() {
		return this.stackFrame.getDebugger().getRedeliveries(this.processorId);
	}
	
	private long getLastProcessingTime() {
		return this.stackFrame.getDebugger().getLastProcessingTime(this.processorId);
	}
	
	private long getMinProcessingTime() {
		return this.stackFrame.getDebugger().getMinProcessingTime(this.processorId);
	}

	private long getMaxProcessingTime() {
		return this.stackFrame.getDebugger().getMaxProcessingTime(this.processorId);
	}

	private long getMeanProcessingTime() {
		return this.stackFrame.getDebugger().getAverageProcessingTime(this.processorId);
	}

	private long getTotalProcessingTime() {
		return this.stackFrame.getDebugger().getTotalProcessingTime(this.processorId);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.values.BaseCamelValue#getVariableDisplayString()
	 */
	@Override
	protected String getVariableDisplayString() {
		return "CamelProcessor";
	}
}

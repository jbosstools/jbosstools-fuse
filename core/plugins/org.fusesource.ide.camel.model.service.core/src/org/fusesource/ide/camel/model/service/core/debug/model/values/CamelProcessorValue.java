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
package org.fusesource.ide.camel.model.service.core.debug.model.values;

import java.util.ArrayList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.camel.model.service.core.debug.CamelDebugTarget;
import org.fusesource.ide.camel.model.service.core.debug.CamelStackFrame;
import org.fusesource.ide.camel.model.service.core.debug.model.variables.BaseCamelVariable;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

/**
 * @author lhein
 *
 */
public class CamelProcessorValue extends BaseCamelValue {
	
	private ArrayList<IVariable> fVariables = new ArrayList<IVariable>();
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
	public CamelProcessorValue(CamelDebugTarget target, CamelStackFrame stackFrame, String value, Class type) {
		super(target, value, type);
		this.debugTarget = target;
		this.stackFrame = stackFrame;
		this.processorId = value;
		try {
			init();
		} catch (DebugException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
	}
	
	/**
	 * initialize variables
	 */
	private void init() throws DebugException {
		BaseCamelVariable var = null;
		BaseCamelValue val = null;
		
		// PROCESSOR ID
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_ID, String.class);
		val = new BaseCamelValue(this.fTarget, getValueString(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// ROUTE ID
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_ROUTE_ID, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getRouteId(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// CAMEL ID
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_CAMEL_ID, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getCamelId(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);

		// EXCHANGES COMPLETED
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_EXCHANGES_COMPLETED, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getExchangesCompleted(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// EXCHANGES FAILED
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_EXCHANGES_FAILED, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getExchangesFailed(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// TOTAL EXCHANGES
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_EXCHANGES_TOTAL, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getTotalExchanges(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);

		// REDELIVERIES
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_REDELIVERIES, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getRedeliveries(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// EXTERNAL REDELIVERIES
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_EXTERNAL_REDELIVERIES, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getExternalRedeliveries(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// FAILURES HANDLED
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_FAILURES_HANDLED, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getFailuresHandled(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// LAST PROCESSING TIME
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_LAST_PROCESSING_TIME, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getLastProcessingTime(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);

		// MIN PROCESSING TIME
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_MIN_PROCESSING_TIME, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getMinProcessingTime(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// MEAN PROCESSING TIME
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_AVG_PROCESSING_TIME, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getMeanProcessingTime(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// MAX PROCESSING TIME
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_MAX_PROCESSING_TIME, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getMaxProcessingTime(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// TOTAL PROCESSING TIME
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_PROCESSOR_TOTAL_PROCESSING_TIME, String.class);
		val = new BaseCamelValue(this.fTarget, "" + getTotalProcessingTime(), var.getReferenceType());
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

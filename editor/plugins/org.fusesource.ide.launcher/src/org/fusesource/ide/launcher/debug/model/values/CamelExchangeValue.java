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
import java.util.Date;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.jmx.commons.backlogtracermessage.BacklogTracerEventMessage;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;

/**
 * @author lhein
 */
public class CamelExchangeValue extends BaseCamelValue {
	
	private BacklogTracerEventMessage exchange;
	private List<IVariable> fVariables = new ArrayList<>();
	private CamelDebugTarget debugTarget;
	
	/**
	 * creates a value
	 * 
	 * @param target
	 * @param exchange
	 * @param type 
	 */
	public CamelExchangeValue(CamelDebugTarget target, BacklogTracerEventMessage exchange, Class<?> type) {
		super(target, exchange.getExchangeId(), type);
		this.debugTarget = target;
		this.exchange = exchange;
		try {
			initExchange();
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/**
	 * initialize variables
	 */
	private void initExchange() throws DebugException {
		fillfVariables(VARIABLE_NAME_EXCHANGEID, String.class, exchange.getExchangeId());
		fillfVariables(VARIABLE_NAME_NODEID, String.class, exchange.getToNode());
		fillfVariables(VARIABLE_NAME_ROUTEID, String.class, exchange.getRouteId());
		
		final Date timestamp = exchange.getTimestamp();
		fillfVariables(VARIABLE_NAME_TIMESTAMP, Date.class, timestamp != null ? String.valueOf(timestamp.getTime()) : null);

		fillfVariables(VARIABLE_NAME_UID, String.class, String.valueOf(exchange.getUid()));
	}
	
	/**
	 * @param variableName
	 * @param class1
	 * @param value
	 * @throws DebugException
	 */
	private void fillfVariables(String variableName, Class<?> type, String value) throws DebugException {
		BaseCamelVariable var = new BaseCamelVariable(this.debugTarget, variableName, type);
		BaseCamelValue val = new BaseCamelValue(this.fTarget, value, var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return !fVariables.isEmpty();
	}
	
	@Override
	public IVariable[] getVariables() throws DebugException {
		return this.fVariables.toArray(new IVariable[this.fVariables.size()]);
	}
	
	@Override
	protected String getVariableDisplayString() {
		return "CamelExchange";
	}
}

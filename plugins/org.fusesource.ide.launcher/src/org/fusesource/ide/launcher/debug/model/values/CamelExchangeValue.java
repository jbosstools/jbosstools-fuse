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
import java.util.Date;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.exchange.BacklogTracerEventMessage;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;

/**
 * @author lhein
 */
public class CamelExchangeValue extends BaseCamelValue {
	
	private BacklogTracerEventMessage exchange;
	private ArrayList<IVariable> fVariables = new ArrayList<IVariable>();
	private CamelDebugTarget debugTarget;
	
	/**
	 * creates a value
	 * 
	 * @param target
	 * @param exchange
	 * @param type 
	 */
	public CamelExchangeValue(CamelDebugTarget target, BacklogTracerEventMessage exchange, Class type) {
		super(target, exchange.getExchangeId(), type);
		this.debugTarget = target;
		this.exchange = exchange;
		try {
			initExchange();
		} catch (DebugException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * initialize variables
	 */
	private void initExchange() throws DebugException {
		BaseCamelVariable var = null;
		BaseCamelValue val = null;

		// EXCHANGE ID
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_EXCHANGEID, String.class);
		val = new BaseCamelValue(this.fTarget, this.exchange.getExchangeId(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// NODE ID
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_NODEID, String.class);
		val = new BaseCamelValue(this.fTarget, this.exchange.getToNode(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// ROUTE ID
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_ROUTEID, String.class);
		val = new BaseCamelValue(this.fTarget, this.exchange.getRouteId(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// TIMESTAMP
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_TIMESTAMP, Date.class);
		val = new BaseCamelValue(this.fTarget, this.exchange.getTimestamp(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);

		// UID
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_UID, String.class);
		val = new BaseCamelValue(this.fTarget, this.exchange.getUid(), var.getReferenceType());
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
		return "CamelExchange";
	}
}

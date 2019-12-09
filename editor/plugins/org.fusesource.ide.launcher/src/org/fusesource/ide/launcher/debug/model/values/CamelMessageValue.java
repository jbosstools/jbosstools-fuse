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
import org.fusesource.ide.camel.model.service.core.jmx.camel.IBacklogTracerMessage;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelBodyVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelHeadersVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelMessageVariable;

/**
 * @author lhein
 */
public class CamelMessageValue extends BaseCamelValue {
	
	private CamelMessageVariable parent;
	private IBacklogTracerMessage message;
	private List<IVariable> fVariables = new ArrayList<>();
	private CamelDebugTarget debugTarget;
	
	/**
	 * creates a message value
	 * 
	 * @param target
	 * @param message
	 * @param type
	 */
	public CamelMessageValue(CamelDebugTarget target, IBacklogTracerMessage message, Class<?> type, CamelMessageVariable parent) {
		super(target, message.getExchangeId(), type);
		this.parent = parent;
		this.debugTarget = target;
		this.message = message;
		try {
			initMessage();
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/**
	 * initialize variables
	 */
	private void initMessage() throws DebugException {
		BaseCamelVariable var;
		BaseCamelValue val;
		
		// BODY
		var = new CamelBodyVariable(this.debugTarget, VARIABLE_NAME_MESSAGEBODY, String.class, parent);
		val = new BaseCamelValue(this.fTarget, this.message.getBody(), var.getReferenceType());
		var.setValue(val);
		this.fVariables.add(var);
		
		// HEADERS
		var = new CamelHeadersVariable(this.debugTarget, VARIABLE_NAME_MESSAGEHEADERS, ArrayList.class);
		val = new CamelHeadersValue(this.fTarget, this.message.getHeaders(), var.getReferenceType(), (CamelHeadersVariable)var);
		var.setValue(val);
		this.fVariables.add(var);
		
		// MESSAGE ID
		var = new BaseCamelVariable(this.debugTarget, VARIABLE_NAME_MESSAGEID, String.class);
		val = new BaseCamelValue(this.fTarget, this.message.getExchangeId(), var.getReferenceType());
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
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.values.BaseCamelValue#getVariableDisplayString()
	 */
	@Override
	protected String getVariableDisplayString() {
		return "CamelMessage";
	}
}

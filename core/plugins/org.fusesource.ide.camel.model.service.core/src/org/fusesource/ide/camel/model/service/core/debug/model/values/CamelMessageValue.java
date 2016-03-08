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
import org.fusesource.ide.camel.model.service.core.debug.model.exchange.Message;
import org.fusesource.ide.camel.model.service.core.debug.model.variables.BaseCamelVariable;
import org.fusesource.ide.camel.model.service.core.debug.model.variables.CamelBodyVariable;
import org.fusesource.ide.camel.model.service.core.debug.model.variables.CamelHeadersVariable;
import org.fusesource.ide.camel.model.service.core.debug.model.variables.CamelMessageVariable;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

/**
 * @author lhein
 */
public class CamelMessageValue extends BaseCamelValue {
	
	private CamelMessageVariable parent;
	private Message message;
	private ArrayList<IVariable> fVariables = new ArrayList<IVariable>();
	private CamelDebugTarget debugTarget;
	
	/**
	 * creates a message value
	 * 
	 * @param target
	 * @param message
	 * @param type
	 */
	public CamelMessageValue(CamelDebugTarget target, Message message, Class type, CamelMessageVariable parent) {
		super(target, message.getExchangeId(), type);
		this.parent = parent;
		this.debugTarget = target;
		this.message = message;
		try {
			initMessage();
		} catch (DebugException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		}
	}
	
	/**
	 * initialize variables
	 */
	private void initMessage() throws DebugException {
		BaseCamelVariable var = null;
		BaseCamelValue val = null;
		
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
		return "CamelMessage";
	}
}

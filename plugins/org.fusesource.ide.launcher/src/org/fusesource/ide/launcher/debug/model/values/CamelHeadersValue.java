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
import org.fusesource.ide.launcher.debug.model.exchange.Header;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelHeaderVariable;

/**
 * @author lhein
 */
public class CamelHeadersValue extends BaseCamelValue {
	
	private ArrayList<IVariable> fVariables = new ArrayList<IVariable>();
	private ArrayList<Header> headers;
	private CamelDebugTarget debugTarget;
	
	/**
	 * 
	 * @param debugTarget
	 * @param value
	 * @param type
	 * @param msg
	 */
	public CamelHeadersValue(CamelDebugTarget debugTarget, ArrayList<Header> headers, Class type) {
		super(debugTarget, "" + headers.hashCode(), type);
		this.debugTarget = debugTarget;
		this.headers = headers;
		if (this.headers == null) this.headers = new ArrayList<Header>();
		try {
			initHeaders();
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
	}
	
	/**
	 * initialize variables
	 */
	private void initHeaders() throws DebugException {
		BaseCamelVariable var = null;
		BaseCamelValue val = null;

		for (Header h : this.headers) {
			// MESSAGE ID
			var = new CamelHeaderVariable(this.debugTarget, h.getKey(), String.class);
			val = new CamelHeaderValue(this.fTarget, h, var.getReferenceType());
			var.setValue(val);
			this.fVariables.add(var);
		}
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
		return "MessageHeaders";
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.values.BaseCamelValue#getValueString()
	 */
	@Override
	public String getValueString() throws DebugException {
		StringBuffer sb = new StringBuffer();
		for (IVariable v : this.fVariables) {
			sb.append(v.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}

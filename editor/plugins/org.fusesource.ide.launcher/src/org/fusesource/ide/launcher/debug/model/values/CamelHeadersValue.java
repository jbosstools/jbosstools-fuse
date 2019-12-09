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
import java.util.stream.Collectors;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.camel.model.service.core.jmx.camel.IBacklogTracerHeader;
import org.fusesource.ide.jmx.commons.backlogtracermessage.Header;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelHeaderVariable;
import org.fusesource.ide.launcher.debug.model.variables.CamelHeadersVariable;

/**
 * @author lhein
 */
public class CamelHeadersValue extends BaseCamelValue {
	
	private CamelHeadersVariable parent;
	private List<IVariable> fVariables = new ArrayList<>();
	private List<? extends IBacklogTracerHeader> headers;
	private CamelDebugTarget debugTarget;
	
	/**
	 * 
	 * @param debugTarget
	 * @param value
	 * @param type
	 * @param msg
	 */
	public CamelHeadersValue(CamelDebugTarget debugTarget, List<? extends IBacklogTracerHeader> headers, Class<?> type, CamelHeadersVariable parent) {
		super(debugTarget, headers != null ? Integer.toString(headers.hashCode()) : "", type);
		this.parent = parent;
		this.debugTarget = debugTarget;
		this.headers = headers;
		if (this.headers == null)
			this.headers = new ArrayList<>();
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
		for (IBacklogTracerHeader h : this.headers) {
			BaseCamelVariable var = new CamelHeaderVariable(this.debugTarget, h.getKey(), String.class, parent);
			BaseCamelValue val = new CamelHeaderValue(this.fTarget, h, var.getReferenceType());
			var.setValue(val);
			this.fVariables.add(var);
		}
	}

	/**
	 * adds a new header to the message
	 * 
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, String value) {
		try {
			this.debugTarget.getDebugger().setMessageHeaderOnBreakpoint(this.debugTarget.getSuspendedNodeId(), key, value);
			CamelHeaderVariable newVar = new CamelHeaderVariable(debugTarget, key, String.class, parent);
			CamelHeaderValue newVal = new CamelHeaderValue(debugTarget, new Header(key, value, String.class.getName()), String.class);
			newVar.setValue(newVal);
			newVar.markChanged();
			this.fVariables.add(newVar);
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		} finally {
			fireCreationEvent();
		}
	}
	
	/**
	 * deletes the header variable with the given key
	 * 
	 * @param key
	 */
	public void deleteHeader(String key) {
		try {
			IVariable v = null;
			this.debugTarget.getDebugger().removeMessageHeaderOnBreakpoint(this.debugTarget.getSuspendedNodeId(), key);
			for (IVariable var : fVariables) {
				if (((CamelHeaderValue)var.getValue()).getHeader().getKey().equals(key)) {
					v = var;
					break;
				}
			}
			this.fVariables.remove(v);
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		} finally {
			fireChangeEvent(DebugEvent.CONTENT);
		}
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
		return "MessageHeaders";
	}
	
	@Override
	public String getValueString() throws DebugException {
		return fVariables.stream().map(v -> v.toString()).collect(Collectors.joining("\n"));
	}
}

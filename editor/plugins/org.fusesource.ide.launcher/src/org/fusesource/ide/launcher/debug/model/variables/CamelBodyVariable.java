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
package org.fusesource.ide.launcher.debug.model.variables;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.fusesource.ide.camel.model.service.core.jmx.camel.ICamelDebuggerMBeanFacade;
import org.fusesource.ide.foundation.core.util.Strings;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;
import org.fusesource.ide.launcher.debug.model.values.BaseCamelValue;

/**
 * @author lhein
 *
 */
public class CamelBodyVariable extends BaseCamelVariable {
	
	private CamelMessageVariable parent;
	
	/**
	 * 
	 * @param thread
	 * @param name
	 * @param type
	 */
	public CamelBodyVariable(CamelDebugTarget debugTarget, String name, Class<?> type, CamelMessageVariable parent) {
		super(debugTarget, name, type);
		this.parent = parent;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#supportsValueModification()
	 */
	@Override
	public boolean supportsValueModification() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String expression) throws DebugException {
		super.setValue(new BaseCamelValue(fTarget, expression, String.class));
		markChanged();
		fireChangeEvent(DebugEvent.CONTENT);
		updateValueOnRuntime(((CamelDebugTarget)getDebugTarget()).getDebugger());
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	@Override
	public boolean verifyValue(IValue value) throws DebugException {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#verifyValue(java.lang.String)
	 */
	@Override
	public boolean verifyValue(String expression) throws DebugException {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#updateValueOnRuntime(org.fusesource.ide.launcher.debug.model.CamelDebugFacade)
	 */
	@Override
	protected void updateValueOnRuntime(ICamelDebuggerMBeanFacade debugger)
			throws DebugException {
		if (Strings.isBlank(getValue().getValueString())) {
			// remove value
			delete();
		} else {
			// change value
			debugger.setMessageBodyOnBreakpoint(getCurrentEndpointNodeId(), getValue().getValueString());
		}
	}
	
	/**
	 * deletes the value
	 */
	public void delete() {
		try {
			ICamelDebuggerMBeanFacade debugger = ((CamelDebugTarget)getDebugTarget()).getDebugger();
			debugger.removeMessageBodyOnBreakpoint(getCurrentEndpointNodeId());
			if (Strings.isBlank(getValue().getValueString()) == false) {
				super.setValue(new BaseCamelValue(fTarget, "[Body is null]", String.class));
				markChanged();
			}
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		} finally {
			fireChangeEvent(DebugEvent.CONTENT);
		}
	}
}

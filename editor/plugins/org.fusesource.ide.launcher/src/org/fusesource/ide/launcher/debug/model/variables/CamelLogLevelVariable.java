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

import org.eclipse.debug.core.DebugException;
import org.fusesource.ide.camel.model.service.core.jmx.camel.ICamelDebuggerMBeanFacade;
import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;

/**
 * @author lhein
 */
public class CamelLogLevelVariable extends BaseWritableCamelVariable {
	
	/**
	 * 
	 * @param thread
	 * @param name
	 * @param type
	 */
	public CamelLogLevelVariable(CamelDebugTarget debugTarget, String name, Class<?> type) {
		super(debugTarget, name, type);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#verifyValue(java.lang.String)
	 */
	@Override
	public boolean verifyValue(String expression) throws DebugException {
		if (expression != null) {
			// check for default log4j log levels
			if (expression.trim().equalsIgnoreCase("DEBUG") || 
				expression.trim().equalsIgnoreCase("ERROR") || 
				expression.trim().equalsIgnoreCase("INFO") || 
				expression.trim().equalsIgnoreCase("OFF") || 
				expression.trim().equalsIgnoreCase("TRACE") || 
				expression.trim().equalsIgnoreCase("WARN")) {
				return true;
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#updateValueOnRuntime(org.fusesource.ide.launcher.debug.model.CamelDebugFacade)
	 */
	@Override
	protected void updateValueOnRuntime(ICamelDebuggerMBeanFacade debugger)
			throws DebugException {
		// change value
		debugger.setLoggingLevel(getValue().getValueString().toUpperCase().trim());
	}
}

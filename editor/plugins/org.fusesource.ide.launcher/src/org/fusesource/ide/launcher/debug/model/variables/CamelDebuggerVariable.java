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

import org.fusesource.ide.launcher.debug.model.CamelDebugTarget;

/**
 * @author lhein
 *
 */
public class CamelDebuggerVariable extends BaseCamelVariable {
	
	/**
	 * 
	 * @param thread
	 * @param name
	 * @param type
	 */
	public CamelDebuggerVariable(CamelDebugTarget debugTarget, String name, Class<?> type) {
		super(debugTarget, name, type);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#getVariableDisplayString()
	 */
	@Override
	protected String getVariableDisplayString() {
		return "CamelDebuggerSettings";
	}
}

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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.camel.model.service.core.debug.CamelDebugElement;
import org.fusesource.ide.camel.model.service.core.debug.CamelDebugTarget;
import org.fusesource.ide.camel.model.service.core.debug.model.variables.IVariableConstants;

/**
 * Value of a Camel variable.
 * 
 * @author lhein
 */
public class BaseCamelValue extends CamelDebugElement implements IValue, IVariableConstants {
	
	private String fValue;
	private Class fType;
	
	/**
	 * creates a value
	 * 
	 * @param target
	 * @param value
	 * @param type
	 */
	public BaseCamelValue(CamelDebugTarget target, String value, Class type) {
		super(target);
		this.fValue = value;
		this.fType = type;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return String.format("%s (id=%d)", getVariableDisplayString(), getVariableIDCode());
	}
	
	/**
	 * returns the value shown as ID in the variables view
	 * 
	 * @return
	 * @throws DebugException
	 */
	protected int getVariableIDCode() throws DebugException {
		return fValue.hashCode();
	}
	
	/**
	 * returns the string shown as variable name
	 * 
	 * @return
	 */
	protected String getVariableDisplayString() {
		return this.fType.getName();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String getValueString() throws DebugException {
		if (hasVariables()) {
			// node with children  / composite object
			return getReferenceTypeName();
		}
		return this.fValue;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		return new IVariable[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		return false;
	}
}

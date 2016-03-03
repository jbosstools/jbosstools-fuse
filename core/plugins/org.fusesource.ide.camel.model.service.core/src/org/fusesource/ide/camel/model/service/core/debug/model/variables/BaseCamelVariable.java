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
package org.fusesource.ide.camel.model.service.core.debug.model.variables;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.fusesource.ide.camel.model.service.core.debug.CamelDebugElement;
import org.fusesource.ide.camel.model.service.core.debug.CamelDebugFacade;
import org.fusesource.ide.camel.model.service.core.debug.CamelDebugTarget;
import org.fusesource.ide.camel.model.service.core.debug.model.values.BaseCamelValue;

/**
 * A variable in a Camel stack frame
 * 
 * @author lhein
 */
public class BaseCamelVariable extends CamelDebugElement implements IVariable {

	// name & type
	private String fName;
	private Class fType;
	
	boolean valueChanged = false;
	
	// the value
	private BaseCamelValue value;
	
	/**
	 * Constructs a variable contained in the given stack frame
	 * with the given name.
	 * 
	 * @param thread the debug thread
	 * @param name variable name
	 * @param type	value type
	 */
	public BaseCamelVariable(CamelDebugTarget debugTarget, String name, Class type) {
		super(debugTarget);
		this.fName = name;
		this.fType = type;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	public IValue getValue() throws DebugException {
		return this.value;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	public String getName() throws DebugException {
		return this.fName;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	public boolean hasValueChanged() throws DebugException {
		return this.valueChanged;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(java.lang.String)
	 */
	public void setValue(String expression) throws DebugException {
		notSupported("No variable modifications!", null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(org.eclipse.debug.core.model.IValue)
	 */
	public void setValue(IValue value) throws DebugException {
		this.value = (BaseCamelValue)value;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	public boolean supportsValueModification() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(java.lang.String)
	 */
	public boolean verifyValue(String expression) throws DebugException {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(org.eclipse.debug.core.model.IValue)
	 */
	public boolean verifyValue(IValue value) throws DebugException {
		return true;
	}
	
	/**
	 * returns the expected class of the value
	 * 
	 * @return
	 */
	public Class getReferenceType() {
		return this.fType;
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
		return getValue().getValueString().hashCode();
	}
	
	/**
	 * returns the string shown as variable name
	 * 
	 * @return
	 */
	protected String getVariableDisplayString() {
		return this.fType.getName();
	}
	
	/**
	 * marks the value as changed
	 * 
	 * @param changed	changed?
	 */
	public void markChanged() throws DebugException {
		this.valueChanged = true;
	}
	
	/**
	 * 
	 * @return
	 */
	protected String getCurrentEndpointNodeId() throws DebugException {
		CamelDebugTarget cdt = (CamelDebugTarget)getDebugTarget();
		return cdt.getSuspendedNodeId();
	}
	
	/**
	 * this method should be used to update the value in the runtime
	 */
	protected void updateValueOnRuntime(CamelDebugFacade debugger) throws DebugException {
	}
}

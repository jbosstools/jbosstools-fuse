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
package org.fusesource.ide.launcher.ui.debug.editors;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.ui.actions.IVariableValueEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.fusesource.ide.launcher.debug.model.variables.BaseWriteableCamelBooleanVariable;
import org.fusesource.ide.launcher.ui.Activator;

/**
 * @author lhein
 */
public class VariableValueEditor implements IVariableValueEditor {

	/**
	 * 
	 */
	public VariableValueEditor() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IVariableValueEditor#editVariable(org.eclipse.debug.core.model.IVariable, org.eclipse.swt.widgets.Shell)
	 */
	@Override
	public boolean editVariable(IVariable variable, Shell shell) {
		try {
			if (variable instanceof BaseWriteableCamelBooleanVariable) {
				BaseWriteableCamelBooleanVariable var = (BaseWriteableCamelBooleanVariable) variable;
				IValue value = variable.getValue();
				BooleanVariableEditor editor = new BooleanVariableEditor(shell, "Edit value...", "Please select the new value...", Boolean.parseBoolean(value.getValueString()));
				if (editor.open() == Window.OK && Boolean.parseBoolean(value.getValueString()) != editor.getValue()) {
					var.setValue(Boolean.toString(editor.getValue()));
				}
				return true;
			}
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IVariableValueEditor#saveVariable(org.eclipse.debug.core.model.IVariable, java.lang.String, org.eclipse.swt.widgets.Shell)
	 */
	@Override
	public boolean saveVariable(IVariable variable, String expression,
			Shell shell) {
		try {
			if (variable instanceof BaseWriteableCamelBooleanVariable) {
				IValue value = variable.getValue();
				((BaseWriteableCamelBooleanVariable) value).setValue(expression);
				return true;
			}
		} catch (DebugException ex) {
			Activator.getLogger().error(ex);
		}
		return false;
	}
}

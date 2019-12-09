/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.launcher.debug.model;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.fusesource.ide.launcher.Activator;
import org.fusesource.ide.launcher.debug.util.ICamelDebugConstants;

/**
 * Common function of Camel debug model elements
 * 
 * @author lhein
 */
public abstract class CamelDebugElement extends DebugElement {
	
	// containing target 
	protected CamelDebugTarget fTarget;
	
	/**
	 * Constructs a new debug element contained in the given
	 * debug target.
	 * 
	 * @param target debug target (Camel VM)
	 */
	public CamelDebugElement(CamelDebugTarget target) {
		super(target);
		fTarget = target;
	}
	
	@Override
	public String getModelIdentifier() {
		return ICamelDebugConstants.ID_CAMEL_DEBUG_MODEL;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return fTarget;
	}

	@Override
	public ILaunch getLaunch() {
		return getDebugTarget().getLaunch();
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IDebugElement.class) {
			return this;
		}
		return super.getAdapter(adapter);
	}
	
	protected void abort(String message, Throwable e) throws DebugException {
		throw new DebugException(new Status(IStatus.ERROR, Activator.getBundleID(),	DebugPlugin.INTERNAL_ERROR, message, e));
	}
}

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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.fusesource.ide.camel.model.service.core.debug.CamelDebugTarget;
import org.fusesource.ide.camel.model.service.core.debug.model.values.CamelHeadersValue;
import org.fusesource.ide.camel.model.service.core.internal.CamelModelServiceCoreActivator;

/**
 * @author lhein
 *
 */
public class CamelHeadersVariable extends BaseCamelVariable {
	/**
	 * 
	 * @param thread
	 * @param name
	 * @param type
	 */
	public CamelHeadersVariable(CamelDebugTarget debugTarget, String name, Class type) {
		super(debugTarget, name, type);
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.variables.BaseCamelVariable#getVariableDisplayString()
	 */
	@Override
	protected String getVariableDisplayString() {
		return "MessageHeaders";
	}
	
	/**
	 * adds a new header to the message
	 * 
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, String value) {
		try {
			if (getValue() instanceof CamelHeadersValue) {
				CamelHeadersValue val = (CamelHeadersValue)getValue();
				val.addHeader(key, value);
			}
		} catch (DebugException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
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
			if (getValue() instanceof CamelHeadersValue) {
				CamelHeadersValue val = (CamelHeadersValue)getValue();
				val.deleteHeader(key);
			}
		} catch (DebugException ex) {
			CamelModelServiceCoreActivator.pluginLog().logError(ex);
		} finally {
			fireChangeEvent(DebugEvent.CONTENT);
		}
	}
}

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
import org.fusesource.ide.camel.model.service.core.debug.CamelDebugTarget;
import org.fusesource.ide.camel.model.service.core.debug.model.exchange.Header;

/**
 * @author lhein
 *
 */
public class CamelHeaderValue extends BaseCamelValue {
	
	private Header header;

	/**
	 * 
	 * @param debugTarget
	 * @param header
	 * @param type
	 */
	public CamelHeaderValue(CamelDebugTarget debugTarget, Header header, Class type) {
		super(debugTarget, header.getValue(), type);
		this.header = header;
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.CamelValue#getReferenceTypeName()
	 */
	@Override
	public String getReferenceTypeName() throws DebugException {
		return this.header.getType();
	}
	
	/* (non-Javadoc)
	 * @see org.fusesource.ide.launcher.debug.model.CamelValue#getValueString()
	 */
	@Override
	public String getValueString() throws DebugException {
		return this.header.getValue();
	}
	
	/**
	 * @return the header
	 */
	public Header getHeader() {
		return this.header;
	}
}

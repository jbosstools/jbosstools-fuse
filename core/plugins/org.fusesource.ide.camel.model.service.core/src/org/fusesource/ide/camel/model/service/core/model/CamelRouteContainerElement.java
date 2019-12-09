/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.model;

import org.w3c.dom.Node;

/**
 * @author lhein
 */
public abstract class CamelRouteContainerElement extends AbstractCamelModelElement implements IFuseDetailsPropertyContributor {
	
	/**
	 * 
	 * @param parent
	 * @param underlyingXmlNode
	 */
	public CamelRouteContainerElement(AbstractCamelModelElement parent, Node underlyingXmlNode) {
		super(parent, underlyingXmlNode);
	}
	
	@Override
	public boolean supportsBreakpoint() {
		return false;
	}
}

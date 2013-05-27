/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.camel.editor.propertysheet;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.fusesource.ide.camel.editor.AbstractNodes;
import org.fusesource.ide.camel.model.AbstractNode;


public class RiderTypeMapper implements ITypeMapper {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITypeMapper#mapType(java.lang.Object)
	 */
	@Override
	public Class mapType(Object object) {
		AbstractNode node = AbstractNodes.toAbstractNode(object);
		if (node != null) {
			return AbstractNode.class;
		}
		return object.getClass();
	}
}

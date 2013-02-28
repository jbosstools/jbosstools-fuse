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

package org.fusesource.ide.jmx.ui.internal.propertysheet;


import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.fusesource.ide.jmx.core.tree.ObjectNameNode;
import org.fusesource.ide.jmx.ui.internal.editors.MBeanEditor;


public class JmxTypeMapper implements ITypeMapper {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITypeMapper#mapType(java.lang.Object)
	 */
	public Class mapType(Object object) {
		if (object instanceof MBeanEditor) {
			return ObjectNameNode.class;
		} else {
			return object.getClass();
		}
	}
}

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
package org.fusesource.ide.camel.editor.properties;

import org.eclipse.ui.views.properties.tabbed.ITypeMapper;
import org.fusesource.ide.camel.editor.utils.NodeUtils;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;

/**
 * @author lhein
 */
public class CamelTypeMapper implements ITypeMapper {
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.tabbed.ITypeMapper#mapType(java.lang.Object)
	 */
	@Override
	public Class<?> mapType(Object object) {
		AbstractCamelModelElement node = resolveCamelModelElement(object);
		if (node != null) {
			return node.getClass();
		}
		return object.getClass();
	}

	/**
	 * @param object
	 * @return
	 */
	protected AbstractCamelModelElement resolveCamelModelElement(Object object) {
		return NodeUtils.toCamelElement(object);
	}
}

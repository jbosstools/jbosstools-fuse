/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.CamelBean;

/**
 * @author bfitzpat
 */
public class BeanPropertiesFilter implements IFilter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	@Override
	public boolean select(Object toTest) {
		AbstractCamelModelElement ep = getSelectedObject(toTest);
		return ep != null && CamelBean.BEAN_NODE.equalsIgnoreCase(ep.getNodeTypeId());
	}

	protected AbstractCamelModelElement getSelectedObject(Object toTest) {
		if (toTest instanceof CamelBean) {
			return (CamelBean)toTest;
		}
		return null;
	}
}

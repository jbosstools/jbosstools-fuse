/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties.rest;

import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.RestElement;

/**
 * @author bfitzpat
 */
public class RestPropertiesFilter implements IFilter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	@Override
	public boolean select(Object toTest) {
		AbstractCamelModelElement ep = getSelectedObject(toTest);
		return ep != null && RestElement.REST_TAG.equals(ep.getNodeTypeId());
	}

	protected AbstractCamelModelElement getSelectedObject(Object toTest) {
		if (toTest instanceof RestElement) {
			return (RestElement)toTest;
		}
		return null;
	}
}

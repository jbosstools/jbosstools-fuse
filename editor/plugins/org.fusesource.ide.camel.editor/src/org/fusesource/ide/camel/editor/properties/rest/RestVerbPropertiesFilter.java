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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.model.RestVerbElement;

/**
 * @author bfitzpat
 */
public class RestVerbPropertiesFilter implements IFilter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	@Override
	public boolean select(Object toTest) {
		AbstractCamelModelElement ep = getSelectedObject(toTest);
		List<String> restPropertyElements = new ArrayList<>();
		restPropertyElements.add(RestVerbElement.CONNECT_VERB);
		restPropertyElements.add(RestVerbElement.DELETE_VERB);
		restPropertyElements.add(RestVerbElement.GET_VERB);
		restPropertyElements.add(RestVerbElement.HEAD_VERB);
		restPropertyElements.add(RestVerbElement.OPTIONS_VERB);
		restPropertyElements.add(RestVerbElement.PATCH_VERB);
		restPropertyElements.add(RestVerbElement.POST_VERB);
		restPropertyElements.add(RestVerbElement.PUT_VERB);
		restPropertyElements.add(RestVerbElement.TRACE_VERB);
		return ep != null && restPropertyElements.contains(ep.getNodeTypeId());
	}

	protected AbstractCamelModelElement getSelectedObject(Object toTest) {
		if (toTest instanceof RestVerbElement) {
			return (RestVerbElement)toTest;
		}
		return null;
	}
}

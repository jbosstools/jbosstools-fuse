/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.model.service.core.model;

import org.fusesource.ide.camel.model.service.core.catalog.Parameter;
import org.fusesource.ide.camel.model.service.core.catalog.eips.Eip;

public class CamelModelElementIDUtil {

	public void ensureUniqueID(AbstractCamelModelElement elem) {
		Eip underlyingMetaModelObject = elem.getUnderlyingMetaModelObject();
		if(isParentElementParameter(elem, underlyingMetaModelObject)){
			return;
		}

		if (underlyingMetaModelObject == null && !(elem instanceof CamelContextElement)) {
			// don't give ID for attributes
		} else {
			if (elem.getId() == null || elem.getId().trim().length() < 1) {
				elem.setId(elem.getNewID());
			}
		}
		for (AbstractCamelModelElement e : elem.getChildElements()) {
			ensureUniqueID(e);
		}
	}

	/**
	 * @param elem
	 * @param underlyingMetaModelObject
	 * @return 	if this element is also a parent element parameter then we don't set ID values (example: parent = onException, element: exception)
	 */
	private boolean isParentElementParameter(AbstractCamelModelElement elem, Eip underlyingMetaModelObject) {
		AbstractCamelModelElement parentElement = elem.getParent();
		if(parentElement != null){
			Eip parentUnderlyingMetaModelObject = parentElement.getUnderlyingMetaModelObject();
			String nodeName = elem.getTagNameWithoutPrefix();
			if (parentElement.getParameter(nodeName) != null && parentUnderlyingMetaModelObject != null) {
				Parameter parameter = parentUnderlyingMetaModelObject.getParameter(nodeName);
				if(AbstractCamelModelElement.NODE_KIND_ELEMENT.equals(parameter.getKind()) &&
						!AbstractCamelModelElement.OTHERWISE_NODE_NAME.equalsIgnoreCase(underlyingMetaModelObject.getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
}

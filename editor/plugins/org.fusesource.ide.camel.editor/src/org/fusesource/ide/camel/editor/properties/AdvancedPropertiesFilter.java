/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.fusesource.ide.camel.editor.properties;

import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.AbstractCamelModelElement;
import org.fusesource.ide.camel.model.service.core.util.CamelComponentUtils;

/**
 * @author lhein
 */
public class AdvancedPropertiesFilter implements IFilter {

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
     */
    @Override
    public boolean select(Object toTest) {
        AbstractCamelModelElement ep = getSelectedEndpoint(toTest);
		if (ep != null && ("from".equalsIgnoreCase(ep.getNodeTypeId()) || "to".equalsIgnoreCase(ep.getNodeTypeId()))) {
			final String uri = (String) ep.getParameter("uri");
			if (uri == null || uri.trim().isEmpty()) {
				return false;
			}
			int protocolSeparatorIdx = uri.indexOf(':');
            if (protocolSeparatorIdx != -1) {
				Component comp = CamelComponentUtils.getComponentModel(uri.substring(0, protocolSeparatorIdx), ep.getCamelFile());
				return comp != null && !comp.getParameters().isEmpty();
            }            
        }
        return false;
    }
    
    protected AbstractCamelModelElement getSelectedEndpoint(Object toTest) {
		Object bo = toTest;
        if (toTest instanceof ContainerShapeEditPart) {
            bo = ((ContainerShapeEditPart)toTest).getFeatureProvider().getBusinessObjectForPictogramElement(((ContainerShapeEditPart)toTest).getPictogramElement());
        }
        if (bo instanceof AbstractCamelModelElement) {
            return (AbstractCamelModelElement)bo;
        }
        return null;
    }
}

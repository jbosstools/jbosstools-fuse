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
package org.fusesource.ide.camel.editor.properties;

import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;
import org.fusesource.ide.camel.model.service.core.model.CamelModelElement;
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
        CamelModelElement ep = getSelectedEndpoint(toTest);
        if (ep != null && (ep.getNodeTypeId().equalsIgnoreCase("from") || ep.getNodeTypeId().equalsIgnoreCase("to"))) {
            if (ep.getParameter("uri") == null || ((String)ep.getParameter("uri")).trim().length()<1) return false;
        	int protocolSeparatorIdx = ((String)ep.getParameter("uri")).indexOf(":");
            if (protocolSeparatorIdx != -1) {
				Component comp = CamelComponentUtils.getComponentModel(((String) ep.getParameter("uri")).substring(0, protocolSeparatorIdx),
						ep.getCamelFile().getResource().getProject());
                return comp != null && comp.getUriParameters().isEmpty() == false;
            }            
        }
        return false;
    }
    
    protected CamelModelElement getSelectedEndpoint(Object toTest) {
        Object bo = null;
        if (toTest instanceof ContainerShapeEditPart) {
            bo = ((ContainerShapeEditPart)toTest).getFeatureProvider().getBusinessObjectForPictogramElement(((ContainerShapeEditPart)toTest).getPictogramElement());
        }
        if (bo instanceof CamelModelElement) {
            return (CamelModelElement)bo;
        }
        return null;
    }
}

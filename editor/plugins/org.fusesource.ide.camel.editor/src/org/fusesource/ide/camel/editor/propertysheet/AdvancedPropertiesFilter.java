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
package org.fusesource.ide.camel.editor.propertysheet;

import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.camel.model.Endpoint;
import org.fusesource.ide.camel.model.service.core.catalog.components.Component;

/**
 * @author lhein
 */
public class AdvancedPropertiesFilter implements IFilter {

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
     */
    @Override
    public boolean select(Object toTest) {
        Endpoint ep = getSelectedEndpoint(toTest);
        if (ep != null) {
            int protocolSeparatorIdx = ep.getUri().indexOf(":");
            if (protocolSeparatorIdx != -1) {
            	Component comp = CamelComponentUtils.getComponentModel(ep.getUri().substring(0, protocolSeparatorIdx));
                return comp != null && comp.getUriParameters().isEmpty() == false;
            }            
        }
        return false;
    }
    
    protected Endpoint getSelectedEndpoint(Object toTest) {
        Object bo = null;
        if (toTest instanceof ContainerShapeEditPart) {
            bo = ((ContainerShapeEditPart)toTest).getFeatureProvider().getBusinessObjectForPictogramElement(((ContainerShapeEditPart)toTest).getPictogramElement());
        }
        if (bo instanceof Endpoint) {
            return (Endpoint)bo;
        }
        return null;
    }
}

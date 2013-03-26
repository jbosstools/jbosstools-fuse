/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 ******************************************************************************/
package org.fusesource.ide.jmx.ui.internal.propertysheet;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.commons.tree.NodeSupport;
import org.fusesource.ide.jmx.core.MBeanFeatureInfoWrapper;

/**
 * Filter for "Properties" tab section.
 */
public class PropertiesSectionFilter implements IFilter {

    @Override
    public boolean select(Object toTest) {
        IPropertySource properties = null;
        if (toTest instanceof NodeSupport) {
            properties = ((NodeSupport) toTest).getPropertySource();
        } else if (toTest instanceof Node || toTest instanceof MBeanFeatureInfoWrapper) {
            properties = (IPropertySource) Platform.getAdapterManager().getAdapter(toTest, IPropertySource.class);
        }
        return properties != null;
    }

}

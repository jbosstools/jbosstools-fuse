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

import java.util.List;

import org.eclipse.jface.viewers.IFilter;
import org.fusesource.ide.commons.tree.RefreshableCollectionNode;

/**
 * Filter for "Contents" tab section.
 */
public class ContentsSectionFilter implements IFilter {

    @Override
    public boolean select(Object toTest) {
        if (toTest instanceof RefreshableCollectionNode) {
            List<?> propertySources = ((RefreshableCollectionNode) toTest).getPropertySourceList();
            return propertySources != null && propertySources.size() > 0;
        }
        return false;
    }

}

/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.tables;

import javax.management.MBeanAttributeInfo;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.fusesource.ide.jmx.core.MBeanAttributeInfoWrapper;


class AttributesViewerSorter extends ViewerSorter {
    int direction, index;

    protected AttributesViewerSorter(int direction, int index) {
        this.direction = (direction == SWT.UP ? -1 : 1);
        this.index = index;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof MBeanAttributeInfoWrapper
                && e2 instanceof MBeanAttributeInfoWrapper) {
            MBeanAttributeInfo attrInfo1 = ((MBeanAttributeInfoWrapper) e1)
                    .getMBeanAttributeInfo();
            MBeanAttributeInfo attrInfo2 = ((MBeanAttributeInfoWrapper) e2)
                    .getMBeanAttributeInfo();
            if (index == 0)
                return direction
                        * attrInfo1.getName().compareTo(attrInfo2.getName());

        }
        return direction * super.compare(viewer, e1, e2);
    }
}
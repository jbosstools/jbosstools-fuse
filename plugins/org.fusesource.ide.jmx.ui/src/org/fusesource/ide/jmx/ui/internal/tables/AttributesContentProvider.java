/*******************************************************************************
 * Copyright (c) 2006 Jeff Mesnil
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.fusesource.ide.jmx.ui.internal.tables;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.jmx.core.MBeanAttributeInfoWrapper;


class AttributesContentProvider implements IStructuredContentProvider {
    private MBeanAttributeInfoWrapper[] attributes;

    public Object[] getElements(Object inputElement) {
        if (attributes == null) {
            return new Object[0];
        }
        return attributes;
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        attributes = (MBeanAttributeInfoWrapper[]) newInput;
    }
}
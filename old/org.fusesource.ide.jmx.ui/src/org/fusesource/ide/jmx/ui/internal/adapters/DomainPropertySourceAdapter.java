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

package org.fusesource.ide.jmx.ui.internal.adapters;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.jmx.ui.Messages;


public class DomainPropertySourceAdapter implements IPropertySource {

    private String domain;

    public DomainPropertySourceAdapter(String domain) {
        this.domain = domain;
    }

    public Object getEditableValue() {
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
        // General properties
        addDescriptor("name", Messages.domain, Messages.general, descriptors); //$NON-NLS-1$
        return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }

    private void addDescriptor(String id, String displayName, String category,
            List<IPropertyDescriptor> descriptors) {
        PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
        descriptor.setCategory(category);
        descriptors.add(descriptor);
    }

    public Object getPropertyValue(Object id) {
        if ("name".equals(id)) { //$NON-NLS-1$
            return domain;
        }
        return null;
    }

    public boolean isPropertySet(Object id) {
        return false;
    }

    public void resetPropertyValue(Object id) {
    }

    public void setPropertyValue(Object id, Object value) {
    }

}

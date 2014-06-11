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

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.jmx.core.Impact;
import org.fusesource.ide.jmx.ui.Messages;


public class MBeanOperationInfoPropertySourceAdapter implements IPropertySource {

    private MBeanOperationInfo opInfo;

    public MBeanOperationInfoPropertySourceAdapter(MBeanOperationInfo opInfo) {
        this.opInfo = opInfo;
    }

    public Object getEditableValue() {
        return null;
    }

    public IPropertyDescriptor[] getPropertyDescriptors() {
        List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
        // General properties
        addDescriptor("name", Messages.name, Messages.general, descriptors); //$NON-NLS-1$
        addDescriptor(
                "description", Messages.description, Messages.general, descriptors); //$NON-NLS-1$
        addDescriptor(
                "returnType", Messages.returnType, Messages.general, descriptors); //$NON-NLS-1$
        addDescriptor("impact", Messages.impact, Messages.general, descriptors); //$NON-NLS-1$
        addDescriptor(
                "writable", Messages.writable, Messages.general, descriptors); //$NON-NLS-1$
        MBeanParameterInfo[] paramInfos = opInfo.getSignature();
        for (int i = 0; i < paramInfos.length; i++) {
            MBeanParameterInfo paramInfo = paramInfos[i];
            addDescriptor(
                    "param" + i, paramInfo.getName(), Messages.parameters, //$NON-NLS-1$
                    descriptors);
        }
        return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }

    private void addDescriptor(String id, String displayName, String category,
            List<PropertyDescriptor> descriptors) {
        PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
        descriptor.setCategory(category);
        descriptors.add(descriptor);
    }

    public Object getPropertyValue(Object id) {
        if ("name".equals(id)) { //$NON-NLS-1$
            return opInfo.getName();
        }
        if ("description".equals(id)) { //$NON-NLS-1$
            return opInfo.getDescription();
        }
        if ("returnType".equals(id)) { //$NON-NLS-1$
            return opInfo.getReturnType();
        }
        if ("impact".equals(id)) { //$NON-NLS-1$
            return Impact.parseInt(opInfo.getImpact());
        }
        if (id instanceof String) {
            String idStr = (String) id;
            if (idStr.startsWith("param")) { //$NON-NLS-1$
                String indexStr = idStr.substring(idStr.length() - 1);
                int i = new Integer(indexStr).intValue();
                for (int j = 0; j < opInfo.getSignature().length; j++) {
                    MBeanParameterInfo paramInfo = opInfo.getSignature()[j];
                    if (i == j) {
                        return paramInfo.getType();
                    }
                }
            }

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

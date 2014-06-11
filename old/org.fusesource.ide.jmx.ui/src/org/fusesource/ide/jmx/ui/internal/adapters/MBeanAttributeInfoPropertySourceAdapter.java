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
import java.util.Arrays;
import java.util.List;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;


import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jmx.ui.Messages;


public class MBeanAttributeInfoPropertySourceAdapter implements IPropertySource {

    private final MBeanAttributeInfo attrInfo;

    private final ObjectName on;

    private final MBeanServerConnection mbsc;

    public MBeanAttributeInfoPropertySourceAdapter(MBeanAttributeInfo attrInfo,
            ObjectName on, MBeanServerConnection mbsc) {
        Assert.isNotNull(attrInfo);
        Assert.isNotNull(on);
        Assert.isNotNull(mbsc);
        this.attrInfo = attrInfo;
        this.on = on;
        this.mbsc = mbsc;
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
        addDescriptor("type", Messages.type, Messages.general, descriptors); //$NON-NLS-1$
        addDescriptor(
                "readable", Messages.readable, Messages.general, descriptors); //$NON-NLS-1$
        addDescriptor(
                "writable", Messages.writable, Messages.general, descriptors); //$NON-NLS-1$
        addDescriptor("value", Messages.value, Messages.general, descriptors); //$NON-NLS-1$
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
            return attrInfo.getName();
        }
        if ("description".equals(id)) { //$NON-NLS-1$
            return attrInfo.getDescription();
        }
        if ("type".equals(id)) { //$NON-NLS-1$
            Object obj = attrInfo.getType();
            if (obj instanceof Object[]) {
                return Arrays.asList((Object[]) obj).toString();
            }
            return obj;
        }
        if ("readable".equals(id)) { //$NON-NLS-1$
            return Boolean.valueOf(attrInfo.isReadable());
        }
        if ("writable".equals(id)) { //$NON-NLS-1$
            return Boolean.valueOf(attrInfo.isWritable());
        }
        if ("value".equals(id)) { //$NON-NLS-1$
            try {
                Object obj = mbsc.getAttribute(on, attrInfo.getName());
                if (obj instanceof Object[]) {
                    return Arrays.asList((Object[]) obj).toString();
                }
                return obj;
            } catch (Exception e) {
        	JMXUIActivator.log(IStatus.WARNING, NLS.bind(
			Messages.MBeanAttributeValue_Warning,
			attrInfo.getName()), e);
		return null;
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

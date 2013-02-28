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


import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.fusesource.ide.jmx.core.DomainWrapper;
import org.fusesource.ide.jmx.core.MBeanAttributeInfoWrapper;
import org.fusesource.ide.jmx.core.MBeanInfoWrapper;
import org.fusesource.ide.jmx.core.MBeanOperationInfoWrapper;
import org.fusesource.ide.jmx.core.tree.ObjectNameNode;


public class JMXAdapterFactory implements IAdapterFactory {

    private Class<?>[] adapterClasses = { DomainWrapper.class, MBeanInfoWrapper.class, MBeanAttributeInfoWrapper.class, MBeanOperationInfoWrapper.class, ObjectNameNode.class };

    public Class[] getAdapterList() {
        return new Class[] { IPropertySource.class };
    }

    /**
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
     *      java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
        if (adapterType == IPropertySource.class) {
            return adaptToPropertySource(adaptableObject);
        }
        return null;
    }

    private IPropertySource adaptToPropertySource(Object adaptableObject) {
        if (adaptableObject instanceof DomainWrapper) {
            DomainWrapper domain = (DomainWrapper) adaptableObject;
            return new DomainPropertySourceAdapter(domain.getName());
        }
        if (adaptableObject instanceof MBeanInfoWrapper) {
            MBeanInfoWrapper wrapper = (MBeanInfoWrapper) adaptableObject;
            return new MBeanInfoPropertySourceAdapter(wrapper.getObjectName(),
                    wrapper.getMBeanInfo());
        }
        if (adaptableObject instanceof MBeanAttributeInfoWrapper) {
            MBeanAttributeInfoWrapper wrapper = (MBeanAttributeInfoWrapper) adaptableObject;
            return new MBeanAttributeInfoPropertySourceAdapter(wrapper
                    .getMBeanAttributeInfo(), wrapper.getObjectName(), wrapper
                    .getMBeanServerConnection());
        }
        if (adaptableObject instanceof MBeanOperationInfoWrapper) {
            MBeanOperationInfoWrapper wrapper = (MBeanOperationInfoWrapper) adaptableObject;
            return new MBeanOperationInfoPropertySourceAdapter(wrapper
                    .getMBeanOperationInfo());
        }
        if (adaptableObject instanceof ObjectNameNode) {
        	ObjectNameNode objectName = (ObjectNameNode) adaptableObject;
        	return new ObjectNameNodePropertySourceAdapter(objectName);
        }
        return null;
    }

	public Class<?>[] getAdapterClasses() {
		return adapterClasses;
	}
}

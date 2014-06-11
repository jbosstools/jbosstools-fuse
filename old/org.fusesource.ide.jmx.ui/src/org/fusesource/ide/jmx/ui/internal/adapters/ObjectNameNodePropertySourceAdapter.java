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

import javax.management.MBeanAttributeInfo;


import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.fusesource.ide.jmx.core.MBeanAttributeInfoWrapper;
import org.fusesource.ide.jmx.core.tree.ObjectNameNode;


public class ObjectNameNodePropertySourceAdapter implements IPropertySource {

	private final ObjectNameNode objectName;

	public ObjectNameNodePropertySourceAdapter(ObjectNameNode objectName) {
		this.objectName = objectName;
	}

	public Object getEditableValue() {
		return false;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
        List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
        // General properties
        
        MBeanAttributeInfoWrapper[] attributes = getAttributes();
        for (MBeanAttributeInfoWrapper attribute : attributes) {
        	MBeanAttributeInfo attributeInfo = attribute.getMBeanAttributeInfo();
			String id = attributeInfo.getName();
			PropertyDescriptor descriptor = new PropertyDescriptor(id, id);
			//descriptor.setLabelProvider(new ILabelProvider() {});
			descriptors.add(descriptor);
		}
        return descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
    }

	protected MBeanAttributeInfoWrapper[] getAttributes() {
		return objectName.getMbeanInfoWrapper().getMBeanAttributeInfoWrappers();
	}

	private void addDescriptor(String id, String displayName, String category, List<IPropertyDescriptor> descriptors) {
		PropertyDescriptor descriptor = new PropertyDescriptor(id, displayName);
		descriptor.setCategory(category);
		descriptors.add(descriptor);
	}

	public Object getPropertyValue(Object anId) {
        MBeanAttributeInfoWrapper[] attributes = getAttributes();
        for (MBeanAttributeInfoWrapper attribute : attributes) {
        	MBeanAttributeInfo attributeInfo = attribute.getMBeanAttributeInfo();
			String id = attributeInfo.getName();
			if (anId.equals(id)) {
				try {
					return attribute.getValue();
				} catch (Exception e) {
					// TODO...
					e.printStackTrace();
				}
			}
		}
        return null;
	}

	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub

	}

}

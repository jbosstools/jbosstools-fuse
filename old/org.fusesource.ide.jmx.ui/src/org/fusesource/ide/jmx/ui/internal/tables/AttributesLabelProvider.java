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


import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jmx.core.MBeanAttributeInfoWrapper;
import org.fusesource.ide.jmx.core.util.StringUtils;
import org.fusesource.ide.jmx.ui.JMXUIActivator;
import org.fusesource.ide.jmx.ui.Messages;


class AttributesLabelProvider extends LabelProvider implements
        ITableLabelProvider {
    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof MBeanAttributeInfoWrapper))
            return super.getText(element);

        MBeanAttributeInfoWrapper wrapper = (MBeanAttributeInfoWrapper) element;
        MBeanAttributeInfo attrInfo = wrapper.getMBeanAttributeInfo();
        switch (columnIndex) {
        case 0:
            return attrInfo.getName();
        case 1:
            try {
                return StringUtils.toString(wrapper.getValue(), false);
            } catch (Throwable t) {
                JMXUIActivator.log(IStatus.ERROR, NLS.bind(
			Messages.MBeanAttributeValue_Warning,
			attrInfo.getName()), t);
                return Messages.unavailable;
            }
        }
        return getText(element);
    }

    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }
}
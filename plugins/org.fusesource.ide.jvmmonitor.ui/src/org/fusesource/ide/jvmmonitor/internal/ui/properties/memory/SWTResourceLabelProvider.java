/*******************************************************************************
 * Copyright (c) 2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.fusesource.ide.jvmmonitor.core.ISWTResourceElement;
import org.fusesource.ide.jvmmonitor.internal.ui.IConstants;
import org.fusesource.ide.jvmmonitor.ui.Activator;


/**
 * The label provider for SWT resources viewer extending LabelProvider to change
 * the filter behavior.
 */
public class SWTResourceLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return super.getImage(element);
    }

    /*
     * @see ITableLabelProvider#getColumnText(Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        return super.getText(element);
    }

    /*
     * Gets the text for filtering.
     * 
     * @see LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object obj) {
        if (obj instanceof ISWTResourceElement) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(((ISWTResourceElement) obj).getName()).append(' ');

            // stack traces
            if (Activator.getDefault().getPreferenceStore()
                    .getBoolean(IConstants.WIDE_SCOPE_SWT_RESOURCE_FILTER)) {
                for (StackTraceElement element : ((ISWTResourceElement) obj)
                        .getStackTraceElements()) {
                    buffer.append(element.getClassName()).append('.')
                            .append(element.getMethodName()).append(' ');
                    buffer.append(element.getFileName()).append(' ');
                }
            }
            return buffer.toString();
        }
        return super.getText(obj);
    }
}

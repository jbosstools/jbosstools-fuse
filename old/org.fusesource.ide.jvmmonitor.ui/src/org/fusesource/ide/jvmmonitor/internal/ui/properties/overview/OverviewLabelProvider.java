/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.overview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * The heap label provider.
 */
public class OverviewLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    /*
     * @see ITableLabelProvider#getColumnText(Object, int)
     */
    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof OverviewCategory) {
            if (columnIndex == 0) {
                return ((OverviewCategory) element).displayName;
            }
            return ""; //$NON-NLS-1$
        } else if (element instanceof OverviewProperty) {
            OverviewProperty property = (OverviewProperty) element;
            if (columnIndex == 0) {
                return property.getDisplayName();
            } else if (columnIndex == 1) {
                return property.getValueString();
            }
            return ""; //$NON-NLS-1$
        }
        return super.getText(element);
    }

    /*
     * @see ITableLabelProvider#getColumnImage(Object, int)
     */
    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /*
     * Gets the text for filtering.
     * 
     * @see LabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object obj) {
        return getColumnText(obj, 0);
    }
}

/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;

/**
 * The label provider contributing to the extension point
 * <tt>org.eclipse.ui.views.properties.tabbed.propertyContributor</tt> to show
 * title on Properties view.
 */
public class TitleProvider extends LabelProvider {

    /*
     * @see LabelProvider#getText(Object)
     */
    @Override
    public String getText(Object element) {
        if (!(element instanceof StructuredSelection)) {
            return super.getText(element);
        }

        Object firstElement = ((StructuredSelection) element).getFirstElement();
        if (!(firstElement instanceof IActiveJvm)) {
            return super.getText(element);
        }

        IActiveJvm jvm = (IActiveJvm) firstElement;
        return jvm.getMainClass() + " [PID: " + jvm.getPid() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}

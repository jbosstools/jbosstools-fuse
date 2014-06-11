/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import org.fusesource.ide.jvmmonitor.core.cpu.ICpuModel;

/**
 * The call tree content provider.
 */
public class CallTreeContentProvider extends AbstractContentProvider {

    /*
     * @see IStructuredContentProvider#getElements(Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof ICpuModel) {
            return ((ICpuModel) inputElement).getCallTreeRoots();
        }
        return new Object[0];
    }
}

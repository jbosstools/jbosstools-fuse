/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import javax.management.MBeanOperationInfo;

import org.eclipse.jface.viewers.ArrayContentProvider;

/**
 * The operations content provider.
 */
public class OperationsContentProvider extends ArrayContentProvider {

    /** The operations. */
    private MBeanOperationInfo[] mBeanOperations;

    /*
     * @see ArrayContentProvider#getElements(Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        if (mBeanOperations != null) {
            return mBeanOperations;
        }
        return new MBeanOperationInfo[0];
    }

    /**
     * Refreshes the content provider.
     * 
     * @param operations
     *            The operations
     */
    protected void refresh(MBeanOperationInfo[] operations) {
        this.mBeanOperations = operations;
    }
}

/*******************************************************************************
 * Copyright (c) 2010-2011 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.jvmmonitor.core.IStackTraceProvider;

/**
 * The content provider for stack trace list.
 */
public class StackTraceContentProvider implements IStructuredContentProvider {

    /** The element. */
    private Object element;

    /*
     * @see IContentProvider#dispose()
     */
    @Override
    public void dispose() {
        // do nothing
    }

    /*
     * @see IContentProvider#inputChanged(Viewer, Object, Object)
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput instanceof IStructuredSelection) {
            element = ((IStructuredSelection) newInput).getFirstElement();
        }
    }

    /*
     * @see IStructuredContentProvider#getElements(Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        if (element instanceof IStackTraceProvider) {
            return ((IStackTraceProvider) element).getStackTraceElements();
        }
        return new Object[0];
    }
}

/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.jvmmonitor.core.IHost;
import org.fusesource.ide.jvmmonitor.core.IJvm;
import org.fusesource.ide.jvmmonitor.core.ISnapshot;
import org.fusesource.ide.jvmmonitor.core.JvmModel;

/**
 * The content provider for JVMs tree viewer.
 */
public class JvmTreeContentProvider implements ITreeContentProvider {

    /*
     * @see ITreeContentProvider#getChildren(Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IHost) {
            return ((IHost) parentElement).getJvms().toArray(new IJvm[0]);
        } else if (parentElement instanceof IJvm) {
            return ((IJvm) parentElement).getShapshots().toArray(
                    new ISnapshot[0]);
        }
        return null;
    }

    /*
     * @see ITreeContentProvider#getParent(Object)
     */
    @Override
    public Object getParent(Object element) {
        if (element instanceof IJvm) {
            return ((IJvm) element).getHost();
        } else if (element instanceof ISnapshot) {
            return ((ISnapshot) element).getJvm();
        }
        return null;
    }

    /*
     * @see ITreeContentProvider#hasChildren(Object)
     */
    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof IHost) {
            return ((IHost) element).getJvms().size() > 0;
        } else if (element instanceof IJvm) {
            return ((IJvm) element).getShapshots().size() > 0;
        }
        return false;
    }

    /*
     * @see IStructuredContentProvider#getElements(Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        return JvmModel.getInstance().getHosts().toArray(new IHost[0]);
    }

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
        // do nothing
    }
}

/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.cpu;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.fusesource.ide.jvmmonitor.core.cpu.ITreeNode;

/**
 * The tree content provider.
 */
abstract public class AbstractContentProvider implements ITreeContentProvider {

    /*
     * @see ITreeContentProvider#getChildren(Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ITreeNode) {
            List<? extends ITreeNode> children = ((ITreeNode) parentElement)
                    .getChildren();
            if (children != null) {
                return children.toArray(new ITreeNode[0]);
            }
        }
        return null;
    }

    /*
     * @see ITreeContentProvider#getParent(Object)
     */
    @Override
    public Object getParent(Object element) {
        if (element instanceof ITreeNode) {
            return ((ITreeNode) element).getParent();
        }
        return null;
    }

    /*
     * @see ITreeContentProvider#hasChildren(Object)
     */
    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof ITreeNode) {
            return ((ITreeNode) element).hasChildren();
        }
        return false;
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

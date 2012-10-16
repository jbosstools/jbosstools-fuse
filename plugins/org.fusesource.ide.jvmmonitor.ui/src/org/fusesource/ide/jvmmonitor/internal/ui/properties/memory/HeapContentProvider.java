/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.memory;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * The heap content provider.
 */
public class HeapContentProvider implements ITreeContentProvider {

    /** The heap viewer. */
    private TreeViewer heapViewer;

    /**
     * The constructor.
     * 
     * @param heapViewer
     *            The heap viewer
     */
    public HeapContentProvider(TreeViewer heapViewer) {
        this.heapViewer = heapViewer;
    }

    /*
     * @see IStructuredContentProvider#getElements(Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        Object input = heapViewer.getInput();
        if (input instanceof IHeapInput) {
            return ((IHeapInput) input).getHeapListElements();
        }
        return new Object[0];
    }

    /*
     * @see ITreeContentProvider#getChildren(Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        return null;
    }

    /*
     * @see ITreeContentProvider#getParent(Object)
     */
    @Override
    public Object getParent(Object element) {
        return null;
    }

    /*
     * @see ITreeContentProvider#hasChildren(Object)
     */
    @Override
    public boolean hasChildren(Object element) {
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
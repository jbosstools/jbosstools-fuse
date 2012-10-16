/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.thread;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * The thread content provider.
 */
public class ThreadContentProvider implements ITreeContentProvider {

    /** The thread viewer. */
    private TreeViewer threadViewer;

    /**
     * The constructor.
     * 
     * @param threadViewer
     *            The thread viewer
     */
    public ThreadContentProvider(TreeViewer threadViewer) {
        this.threadViewer = threadViewer;
    }

    /*
     * @see IStructuredContentProvider#getElements(Object)
     */
    @Override
    public Object[] getElements(Object inputElement) {
        Object input = threadViewer.getInput();
        if (input instanceof IThreadInput) {
            return ((IThreadInput) input).getThreadListElements();
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

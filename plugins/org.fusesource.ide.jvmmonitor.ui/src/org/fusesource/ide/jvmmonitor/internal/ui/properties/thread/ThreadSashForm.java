/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.thread;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractSashForm;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.StackTraceViewer;

/**
 * The sash form to show thread and stack traces.
 */
public class ThreadSashForm extends AbstractSashForm {

    /** The sash weights. */
    private static final int[] SASH_WEIGHTS = new int[] { 55, 45 };

    /** The thread viewer. */
    private TreeViewer threadViewer;

    /** The stack trace viewer. */
    StackTraceViewer stackTraceViewer;

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent composite
     * @param actionBars
     *            The action bars
     */
    public ThreadSashForm(Composite parent, IActionBars actionBars) {
        super(parent, actionBars, SASH_WEIGHTS);
        createSashFormControls(this, actionBars);
        setWeights(initialSashWeights);
    }

    /*
     * @see AbstractSashForm#createSashFormControls(SashForm, IActionBars)
     */
    @Override
    protected void createSashFormControls(SashForm sashForm,
            IActionBars actionBars) {
        threadViewer = new ThreadFilteredTree(sashForm, actionBars).getViewer();
        threadViewer
                .setContentProvider(new ThreadContentProvider(threadViewer));
        threadViewer.setLabelProvider(new ThreadLabelProvider(threadViewer));
        threadViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        ISelection selection = event.getSelection();
                        if (selection.isEmpty()) {
                            selection = null;
                        }
                        stackTraceViewer.setInput(selection);
                    }
                });

        stackTraceViewer = new StackTraceViewer(sashForm, actionBars);
    }

    /**
     * Refreshes the appearance.
     */
    public void refresh() {
        if (!threadViewer.getControl().isDisposed()) {
            threadViewer.refresh();

            // select the first item if no item is selected
            if (threadViewer.getSelection().isEmpty()) {
                TreeItem[] items = threadViewer.getTree().getItems();
                if (items != null && items.length > 0) {
                    threadViewer.getTree().select(items[0]);
                    stackTraceViewer.setInput(threadViewer.getSelection());
                } else {
                    stackTraceViewer.setInput(null);
                }
            }
        }
        if (!stackTraceViewer.getControl().isDisposed()) {
            stackTraceViewer.refresh();
        }
    }

    /**
     * Sets the thread input.
     * 
     * @param threadInput
     *            The thread input
     */
    public void setInput(IThreadInput threadInput) {
        threadViewer.setInput(threadInput);
    }
}

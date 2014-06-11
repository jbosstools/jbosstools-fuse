/*******************************************************************************
 * Copyright (c) 2010 JVM Monitor project. All rights reserved. 
 * 
 * This code is distributed under the terms of the Eclipse Public License v1.0
 * which is available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.fusesource.ide.jvmmonitor.internal.ui.properties.mbean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.fusesource.ide.jvmmonitor.core.IActiveJvm;
import org.fusesource.ide.jvmmonitor.internal.ui.RefreshJob;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractJvmPropertySection;
import org.fusesource.ide.jvmmonitor.internal.ui.properties.AbstractSashForm;


/**
 * The sash form to show thread and stack traces.
 */
public class MBeanSashForm extends AbstractSashForm {

    /** The sash weight. */
    private static final int[] SASH_WEIGHTS = new int[] { 40, 60 };

    /** The MBean viewer. */
    TreeViewer mBeanViewer;

    /** The MBean tab folder. */
    MBeanTabFolder mBeanTabFolder;

    /** The MBean content provider. */
    MBeanContentProvider mBeanContentProvider;

    /** The selection changed listener. */
    ISelectionChangedListener selectionChangedListener;

    /** The property section. */
    AbstractJvmPropertySection section;

    /**
     * The constructor.
     * 
     * @param parent
     *            The parent composite
     * @param section
     *            The property section
     */
    public MBeanSashForm(Composite parent, AbstractJvmPropertySection section) {
        super(parent, section.getActionBars(), SASH_WEIGHTS);
        this.section = section;

        createSashFormControls(this, section.getActionBars());
        setWeights(initialSashWeights);
    }

    /*
     * @see AbstractSashForm#createSashFormControls(SashForm, IActionBars)
     */
    @Override
    protected void createSashFormControls(SashForm sashForm,
            final IActionBars actionBars) {
        mBeanViewer = new MBeanFilteredTree(sashForm, section).getViewer();
        mBeanContentProvider = new MBeanContentProvider();
        mBeanViewer.setContentProvider(mBeanContentProvider);
        mBeanViewer.setLabelProvider(new MyDecoratingStyledCellLabelProvider());
        selectionChangedListener = new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                mBeanTabFolder.selectionChanged(event.getSelection());
            }
        };
        mBeanViewer.addSelectionChangedListener(selectionChangedListener);
        mBeanViewer.setInput(new Object());
        mBeanTabFolder = new MBeanTabFolder(sashForm, section);
    }

    /**
     * Refreshes the appearance.
     */
    protected void refresh() {
        new RefreshJob(Messages.refreshMBeanSectionJobLabel,
                toString()) {

            @Override
            protected void refreshModel(IProgressMonitor monitor) {
                IActiveJvm jvm = section.getJvm();
                if (jvm != null && jvm.isConnected()) {
                    mBeanContentProvider.refresh(jvm);
                }
            }

            @Override
            protected void refreshUI() {
                if (mBeanViewer.getControl().isDisposed()
                        || mBeanTabFolder.isDisposed()) {
                    return;
                }

                mBeanViewer.refresh();
                mBeanTabFolder.refresh();

                IActiveJvm jvm = section.getJvm();
                if (jvm != null && jvm.isConnected()) {
                    mBeanViewer
                            .addSelectionChangedListener(selectionChangedListener);
                } else {
                    mBeanViewer
                            .removeSelectionChangedListener(selectionChangedListener);
                    return;
                }

                // select the first item if no item is selected
                if (mBeanViewer.getSelection().isEmpty()) {
                    TreeItem[] items = mBeanViewer.getTree().getItems();
                    if (items != null && items.length > 0) {
                        mBeanViewer.getTree().select(items[0]);
                        mBeanTabFolder.selectionChanged(mBeanViewer
                                .getSelection());
                    } else {
                        mBeanTabFolder.selectionChanged(null);
                    }
                }
            }
        }.schedule();
    }

    /**
     * The decorating styled cell label provider. To support filtering,
     * <tt>ILabelProvider</tt> has to be implemented.
     */
    private static class MyDecoratingStyledCellLabelProvider extends
            DecoratingStyledCellLabelProvider implements ILabelProvider {

        /**
         * The constructor.
         */
        public MyDecoratingStyledCellLabelProvider() {
            super(new MBeanLabelProvider(), PlatformUI.getWorkbench()
                    .getDecoratorManager().getLabelDecorator(), null);
        }

        /*
         * @see ILabelProvider#getText(Object)
         */
        @Override
        public String getText(Object element) {
            return getStyledStringProvider().getStyledText(element).toString();
        }
    }

    /**
     * Invoked when section is deactivated.
     */
    protected void deactivated() {
        Job.getJobManager().cancel(toString());
        if (!mBeanTabFolder.isDisposed()) {
            mBeanTabFolder.deactivated();
        }
    }

}
